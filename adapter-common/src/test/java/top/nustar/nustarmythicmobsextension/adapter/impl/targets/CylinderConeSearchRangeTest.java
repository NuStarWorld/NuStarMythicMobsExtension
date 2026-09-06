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

import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("宽阶段派生范围的有限数边界")
class CylinderConeSearchRangeTest {
    static Stream<Arguments> representableRanges() {
        return Stream.of(
                Arguments.of(0.0, Double.MIN_VALUE),
                Arguments.of(64.0, 0.0),
                Arguments.of(3e7, 0.0),
                Arguments.of(-3e7, 10.0),
                Arguments.of(0.0, 1.5e308),
                Arguments.of(1e308, 0.0),
                Arguments.of(-1e308, 1e307),
                Arguments.of(0.0, Math.nextDown(Double.MAX_VALUE)),
                Arguments.of(Math.nextDown(Double.MAX_VALUE), 0.0));
    }

    @ParameterizedTest(name = "有限中心 {0} 和范围 {1} 不做任意尺寸封顶")
    @MethodSource("representableRanges")
    void finiteStrictEnvelope(double center, double range) {
        double expanded = CylinderConeSelectorAdapter.searchRange("x", center, range);
        assertTrue(Double.isFinite(expanded));
        assertTrue(Double.isFinite(center - expanded));
        assertTrue(Double.isFinite(center + expanded));
        assertTrue(center - expanded < center - range);
        assertTrue(center + expanded > center + range);
        assertTrue(expanded >= range);
    }

    static Stream<Arguments> unrepresentableRanges() {
        return Stream.of(
                Arguments.of(0.0, Double.MAX_VALUE),
                Arguments.of(Double.MAX_VALUE, 0.0),
                Arguments.of(-Double.MAX_VALUE, 0.0),
                Arguments.of(1e308, 1e308),
                Arguments.of(-1e308, 1e308));
    }

    @ParameterizedTest(name = "中心 {0} 和范围 {1} 无法有限严格外扩时查询前显式失败")
    @MethodSource("unrepresentableRanges")
    void impossibleEnvelopeDoesNotQueryOrReturnEmpty(double coordinate, double range) {
        BukkitCylinderFixtures f = new BukkitCylinderFixtures();
        Location center = f.position(coordinate, 64, 0);
        Entity caster = f.entity(Entity.class, center);
        CylinderConeConfig<Object> config =
                new CylinderConeConfig<>((key, fallback) -> fallback, raw -> metadata -> raw);
        CylinderConeGeometry geometry = new CylinderConeGeometry(range, 180, 0, 0, 0, 0);
        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> CylinderConeSelectorAdapter.select(config, geometry, caster, center, center, entity -> entity));
        assertEquals(IllegalStateException.class, error.getClass());
        assertTrue(error.getMessage().contains("派生查询范围 x"));
        assertTrue(error.getMessage().contains("center=[" + coordinate + "]"));
        assertTrue(error.getMessage().contains("range=[" + range + "]"));
        assertTrue(error.getMessage().contains("expanded=["));
        assertEquals(0, f.queries);
    }
}
