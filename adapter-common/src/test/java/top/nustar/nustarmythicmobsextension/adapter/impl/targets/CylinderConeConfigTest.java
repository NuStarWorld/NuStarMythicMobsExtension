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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("圆柱扇形合法配置与元数据快照")
class CylinderConeConfigTest {
    @Test
    @DisplayName("默认值为 r=11.25、down=17.25、up=11.25、半角30、close=2.5")
    void defaults() {
        CylinderConeConfig<Object> config = literal(Collections.emptyMap());
        CylinderConeGeometry geometry = config.evaluate(new Object());
        assertFalse(config.usesOrigin());
        assertFalse(config.ignoresPlayers());
        assertTrue(config.ignoresArmorStands());
        assertEquals(11.25, geometry.horizontalRange());
        assertEquals(17.25, geometry.verticalSearchRange());
        assertTrue(geometry.contains(0, -17.25, 10, 0, 1));
        assertTrue(geometry.contains(0, 11.25, 10, 0, 1));
        assertFalse(geometry.contains(0, Math.nextUp(11.25), 10, 0, 1));
        assertTrue(geometry.contains(5, 0, Math.sqrt(75), 0, 1));
        assertFalse(geometry.contains(5.01, 0, Math.sqrt(75), 0, 1));
        assertTrue(geometry.contains(0, 0, -2.5, 0, 1));
        assertFalse(geometry.contains(0, 0, -Math.nextUp(2.5), 0, 1));
    }

    @Test
    @DisplayName("r/a/rot 短名优先，无短名才使用长名")
    void shortAliasesWin() {
        Map<String, String> values = new HashMap<>();
        values.put("range", "20");
        values.put("angle", "180");
        values.put("rotation", "-90");
        values.put("r", "4");
        values.put("a", "0");
        values.put("rot", "90");
        values.put("padding", "0");
        values.put("close", "0");
        CylinderConeGeometry shortValues = literal(values).evaluate(null);
        double[] right = shortValues.direction(0, 1, 0);
        assertEquals(4, shortValues.horizontalRange());
        assertArrayEquals(new double[] {-1, 0}, right, 1e-12);
        assertFalse(shortValues.contains(0, 0, 3, right[0], right[1]));
        values.keySet().removeAll(Arrays.asList("r", "a", "rot"));
        CylinderConeGeometry longValues = literal(values).evaluate(null);
        assertEquals(20, longValues.horizontalRange());
        assertArrayEquals(new double[] {1, 0}, longValues.direction(0, 1, 0), 1e-12);
        assertTrue(longValues.contains(0, 0, -10, 0, 1));
    }

    @ParameterizedTest(name = "布尔真值 {0}")
    @ValueSource(strings = {"true", "TRUE", "yes", "YeS", "1"})
    void trueBooleans(String value) {
        CylinderConeConfig<Object> config = flags(value);
        assertTrue(config.usesOrigin());
        assertTrue(config.ignoresPlayers());
        assertTrue(config.ignoresArmorStands());
    }

    @ParameterizedTest(name = "布尔假值 {0}")
    @ValueSource(strings = {"false", "FALSE", "no", "No", "0"})
    void falseBooleans(String value) {
        CylinderConeConfig<Object> config = flags(value);
        assertFalse(config.usesOrigin());
        assertFalse(config.ignoresPlayers());
        assertFalse(config.ignoresArmorStands());
    }

    @ParameterizedTest(name = "未知布尔不 trim：[{0}]")
    @ValueSource(strings = {"", "unknown", " true", "yes ", " false ", " 0"})
    void unknownBooleansKeepOwnDefaults(String value) {
        CylinderConeConfig<Object> config = flags(value);
        assertFalse(config.usesOrigin());
        assertFalse(config.ignoresPlayers());
        assertTrue(config.ignoresArmorStands());
    }

    @ParameterizedTest(name = "ignore 包含 player：{0}")
    @ValueSource(strings = {"player", "PLAYERS", "mob,Player,other", "notplayers"})
    void ignoreSubstringOrExplicitFlag(String value) {
        Map<String, String> values = new HashMap<>();
        values.put("ignoreplayers", "false");
        values.put("ignore", value);
        assertTrue(literal(values).ignoresPlayers());
        values.put("ignore", "armorstand");
        assertFalse(literal(values).ignoresPlayers());
        values.put("ignoreplayers", "yes");
        assertTrue(literal(values).ignoresPlayers());
    }

    @Test
    @DisplayName("每次调用七个表达式各求值一次，metadata 变化产生新快照")
    void metadataOnlyEvaluationIsPerInvocation() {
        Map<String, String> expressions = new HashMap<>();
        Map<String, AtomicInteger> counts = new HashMap<>();
        String[] keys = {"r", "a", "rot", "down", "up", "padding", "close"};
        for (String key : keys) {
            expressions.put(key, "<" + key + ">");
            counts.put("<" + key + ">", new AtomicInteger());
        }
        AtomicInteger factories = new AtomicInteger();
        CylinderConeConfig<Map<String, String>> config = new CylinderConeConfig<>(expressions::getOrDefault, raw -> {
            factories.incrementAndGet();
            return metadata -> {
                counts.get(raw).incrementAndGet();
                return metadata.get(raw);
            };
        });
        Map<String, String> metadata = new HashMap<>();
        String[] first = {"10", "30", "-90", "16", "10", "1.25", "2.5"};
        for (int i = 0; i < keys.length; i++) metadata.put("<" + keys[i] + ">", first[i]);
        CylinderConeGeometry snapshot = config.evaluate(metadata);
        assertEquals(7, factories.get());
        counts.values().forEach(count -> assertEquals(1, count.get()));
        metadata.put("<r>", "2e1");
        assertEquals(21.25, config.evaluate(metadata).horizontalRange());
        assertEquals(11.25, snapshot.horizontalRange());
        counts.values().forEach(count -> assertEquals(2, count.get()));
        assertEquals(7, factories.get());
    }

    private static CylinderConeConfig<Object> flags(String value) {
        Map<String, String> values = new HashMap<>();
        for (String key : Arrays.asList("origin", "ignoreplayers", "ignorearmorstands")) values.put(key, value);
        return literal(values);
    }

    private static CylinderConeConfig<Object> literal(Map<String, String> values) {
        return new CylinderConeConfig<>(values::getOrDefault, raw -> metadata -> raw);
    }
}
