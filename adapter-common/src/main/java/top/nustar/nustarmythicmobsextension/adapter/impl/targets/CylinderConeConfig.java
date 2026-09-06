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

package top.nustar.nustarmythicmobsextension.adapter.impl.targets;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import team.idealstate.sugar.logging.Log;

/** 保存原始配置和版本占位符函数，不持有施法实体或候选目标。 */
public final class CylinderConeConfig<M> {
    private static final String[][] KEYS = {
        {"r", "range"}, {"a", "angle"}, {"rot", "rotation"}, {"down"}, {"up"}, {"padding"}, {"close"}
    };
    private static final String[] DEFAULTS = {"10", "30", "0", "16", "10", "1.25", "2.5"};
    private final String[] raw = new String[KEYS.length];
    private final boolean[] creationFailed = new boolean[KEYS.length];
    private final List<Function<M, String>> expressions = new ArrayList<>();
    private final boolean origin;
    private final boolean ignorePlayers;
    private final boolean ignoreArmorStands;

    public CylinderConeConfig(
            BiFunction<String, String, String> config, Function<String, Function<M, String>> placeholderFactory) {
        for (int i = 0; i < KEYS.length; i++) {
            String value = config.apply(KEYS[i][0], null);
            if (value == null) {
                value = config.apply(KEYS[i][KEYS[i].length - 1], DEFAULTS[i]);
            }
            raw[i] = value;
            try {
                expressions.add(placeholderFactory.apply(value));
            } catch (RuntimeException e) {
                // 注册必须完成，不能让 MM 保留默认 TriggerTargeter；执行时重抛原错误而非重试或默认值。
                creationFailed[i] = true;
                expressions.add(metadata -> {
                    throw e;
                });
            }
        }
        origin = parseBoolean(config.apply("origin", "false"), false);
        ignorePlayers = parseBoolean(config.apply("ignoreplayers", "false"), false)
                || config.apply("ignore", "").toLowerCase(Locale.ROOT).contains("player");
        ignoreArmorStands = parseBoolean(config.apply("ignorearmorstands", "true"), true);
    }

    /** 每次选择仅展开一次每个参数；静态字面量也经过同一严格校验。 */
    public CylinderConeGeometry evaluate(M metadata) {
        String[] expanded = new String[KEYS.length];
        double[] values = new double[KEYS.length];
        for (int i = 0; i < KEYS.length; i++) {
            try {
                expanded[i] = expressions.get(i).apply(metadata);
            } catch (RuntimeException e) {
                throw invalid(KEYS[i][0], raw[i], "<展开失败>", creationFailed[i] ? "占位符创建失败" : "占位符求值失败", e);
            }
            try {
                values[i] = Double.parseDouble(expanded[i]);
            } catch (NumberFormatException | NullPointerException e) {
                throw invalid(KEYS[i][0], raw[i], expanded[i], "必须展开为数值字面量", e);
            }
            if (!Double.isFinite(values[i]) || (i != 2 && values[i] < 0) || (i == 1 && values[i] > 180)) {
                throw invalid(KEYS[i][0], raw[i], expanded[i], "必须有限，尺寸不得为负，半角必须在 [0,180] 内", null);
            }
        }
        double radius = padded(values, expanded, 0);
        double down = padded(values, expanded, 3);
        double up = padded(values, expanded, 4);
        return new CylinderConeGeometry(radius, values[1], values[2], down, up, values[6]);
    }

    private double padded(double[] values, String[] expanded, int index) {
        double result = values[index] + values[5];
        if (!Double.isFinite(result)) {
            throw invalid(
                    KEYS[index][0] + "+padding",
                    raw[index] + " + " + raw[5],
                    expanded[index] + " + " + expanded[5],
                    "派生范围必须有限",
                    null);
        }
        return result;
    }

    private static boolean parseBoolean(String value, boolean fallback) {
        // 与脚本一致：布尔值不 trim，未知字符串回到该参数自己的默认值。
        if ("true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equals(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) || "0".equals(value)) {
            return false;
        }
        return fallback;
    }

    private static IllegalStateException invalid(
            String parameter, String raw, String expanded, String reason, RuntimeException cause) {
        // MM 会将 IllegalArgumentException 转为空集；在数值边界记录一次并保留真实失败。
        IllegalStateException error = new IllegalStateException(
                "NuStarCylinderCone 参数 " + parameter + ": raw=[" + raw + "], expanded=[" + expanded + "]; " + reason,
                cause);
        Log.error(error);
        return error;
    }

    public boolean usesOrigin() {
        return origin;
    }

    public boolean ignoresPlayers() {
        return ignorePlayers;
    }

    public boolean ignoresArmorStands() {
        return ignoreArmorStands;
    }
}
