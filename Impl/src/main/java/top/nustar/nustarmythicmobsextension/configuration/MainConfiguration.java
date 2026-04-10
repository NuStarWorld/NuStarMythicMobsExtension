/*
 *    NuStarMythicMobsExtension
 *    Copyright (C) 2025  NuStar
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.nustar.nustarmythicmobsextension.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.*;
import lombok.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import team.idealstate.sugar.next.context.Context;
import team.idealstate.sugar.next.context.annotation.component.Configuration;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import team.idealstate.sugar.string.StringUtils;
import team.idealstate.sugar.validate.annotation.NotNull;
import top.nustar.nustarmythicmobsextension.exception.NSMMEException;

@Configuration(uri = "/config.yml", release = Context.RESOURCE_EMBEDDED + "config.yml")
@Scope(Scope.PROTOTYPE)
@Data
public class MainConfiguration {
    private final boolean debug;

    @NonNull
    private final Map<String, Variable> variables;

    @NonNull
    @JsonProperty("white-attr-list")
    private final List<String> whiteAttrList;

    @JsonCreator
    public MainConfiguration(
            @JsonProperty("debug") boolean debug,
            @JsonProperty("variables") Map<String, Object> variables,
            @JsonProperty("white-attr-list") List<String> whiteAttrList) {
        this.debug = debug;
        this.variables = toVariableMap(variables);
        this.whiteAttrList = whiteAttrList == null ? Collections.emptyList() : new ArrayList<>(whiteAttrList);
    }

    @NotNull
    private static Map<String, Variable> toVariableMap(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Variable> result = new LinkedHashMap<>(source.size());
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            Variable variable = new Variable(entry.getValue()).validate();
            variable.setName(entry.getKey());
            result.put(entry.getKey(), variable);
        }
        return Collections.unmodifiableMap(result);
    }

    @NotNull
    public List<Variable> getVariableList() {
        return new ArrayList<>(variables.values());
    }

    @Data
    @SuppressWarnings("unused")
    public static class Variable {
        private static final Set<Class<?>> NUMBER_CLASSES = Collections.unmodifiableSet(new HashSet<>(
                Arrays.asList(byte.class, short.class, int.class, long.class, float.class, double.class)));

        @NonNull
        @Setter(AccessLevel.PRIVATE)
        private String name = "";

        @NonNull
        private final Object value;

        @Getter(AccessLevel.PRIVATE)
        private Number cache = null;

        @NotNull
        public Variable validate() {
            Class<?> valueClass = value.getClass();
            if (NUMBER_CLASSES.contains(valueClass) || value instanceof Number) {
                return this;
            }
            if (!(value instanceof String)) {
                throw new NSMMEException("Unsupported value type: " + valueClass.getName());
            }
            String text = (String) value;
            if (text.trim().isEmpty()) {
                throw new NSMMEException("Unsupported value content: " + text);
            }
            return this;
        }

        @NotNull
        private Number doParseAndCache(OfflinePlayer offlinePlayer) {
            validate();
            if (value instanceof Number) {
                Number number = (Number) value;
                this.cache = number;
                return number;
            }
            String text = (String) value;
            Number number = null;
            try {
                number = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                try {
                    number = Double.parseDouble(text);
                } catch (NumberFormatException ignored) {
                }
            }
            if (number != null) {
                this.cache = number;
                return number;
            }
            String placeholders = PlaceholderAPI.setPlaceholders(offlinePlayer, text);
            if (!text.equals(placeholders)) {
                if (StringUtils.isInteger(placeholders)) {
                    try {
                        return Long.parseLong(placeholders);
                    } catch (NumberFormatException e) {
                        return new BigDecimal(placeholders);
                    }
                } else if (StringUtils.isDecimal(placeholders)) {
                    try {
                        return Double.parseDouble(placeholders);
                    } catch (NumberFormatException e) {
                        return new BigDecimal(placeholders);
                    }
                }
            }
            throw new NSMMEException("Unsupported value content: " + text);
        }

        @NotNull
        public Number asNumber(OfflinePlayer offlinePlayer) {
            if (cache == null) {
                return doParseAndCache(offlinePlayer);
            }
            return cache;
        }

        @NotNull
        public BigDecimal asBigDecimal(OfflinePlayer offlinePlayer) {
            Number number = asNumber(offlinePlayer);
            if (number instanceof BigDecimal) {
                return (BigDecimal) number;
            }
            return new BigDecimal(number.toString());
        }
    }
}
