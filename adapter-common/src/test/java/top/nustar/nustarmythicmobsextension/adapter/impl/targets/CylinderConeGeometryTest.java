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

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("圆柱扇形纯几何契约")
class CylinderConeGeometryTest {
    @ParameterizedTest(name = "半角 {0}° 的有符号边界")
    @ValueSource(doubles = {0, 30, 90, 120, 150, 180})
    void signedAngleBoundaries(double angle) {
        CylinderConeGeometry geometry = new CylinderConeGeometry(20, angle, 0, 2, 3, 0);
        assertTrue(atAngle(geometry, angle));
        assertTrue(atAngle(geometry, -angle));
        assertTrue(atAngle(geometry, Math.max(0, angle - 0.001)));
        if (angle < 180) {
            assertFalse(atAngle(geometry, angle + 0.001));
            assertFalse(atAngle(geometry, -angle - 0.001));
        } else {
            assertTrue(atAngle(geometry, 179.999));
        }
    }

    @Test
    @DisplayName("角度仅使用 1e-12 点积容差，不把后方误判到前方")
    void angularEpsilonAndSignedDot() {
        CylinderConeGeometry geometry = new CylinderConeGeometry(20, 90, 0, 2, 3, 0);
        assertTrue(geometry.contains(10, 0, -5e-12, 0, 1));
        assertFalse(geometry.contains(10, 0, -2e-11, 0, 1));
        assertFalse(geometry.contains(0, 0, -10, 0, 1));
        assertTrue(new CylinderConeGeometry(20, 120, 0, 2, 3, 0).contains(10, 0, -5, 0, 1));
        assertFalse(new CylinderConeGeometry(20, 150, 0, 2, 3, 0).contains(0, 0, -10, 0, 1));
    }

    @Test
    @DisplayName("半径与不对称高度为精确闭区间，没有几何容差")
    void exactCylinderBoundaries() {
        CylinderConeGeometry geometry = new CylinderConeGeometry(11.25, 180, 0, 17.25, 11.25, 100);
        assertEquals(11.25, geometry.horizontalRange());
        assertEquals(17.25, geometry.verticalSearchRange());
        assertTrue(geometry.contains(11.25, -17.25, 0, 0, 1));
        assertTrue(geometry.contains(0, 11.25, 11.25, 0, 1));
        assertFalse(geometry.contains(Math.nextUp(11.25), 0, 0, 0, 1));
        assertFalse(geometry.contains(0, Math.nextDown(-17.25), 0, 0, 1));
        assertFalse(geometry.contains(0, Math.nextUp(11.25), 0, 0, 1));
        assertEquals(19, new CylinderConeGeometry(1, 0, 0, 2, 19, 0).verticalSearchRange());
    }

    @Test
    @DisplayName("近身全向不增加 padding，且仍受半径和高度限制")
    void closeRangeDoesNotBypassCylinder() {
        CylinderConeGeometry geometry = new CylinderConeGeometry(11.25, 30, 0, 17.25, 11.25, 2.5);
        assertTrue(geometry.contains(0, 0, -2.5, 0, 1));
        assertTrue(geometry.contains(2.5, 0, 0, 0, 1));
        assertFalse(geometry.contains(0, 0, -Math.nextUp(2.5), 0, 1));
        CylinderConeGeometry small = new CylinderConeGeometry(1, 0, 0, 1, 1, 100);
        assertFalse(small.contains(0, 0, -2, 0, 1));
        assertFalse(small.contains(0, 2, 0, 0, 1));
        assertFalse(small.contains(0, -2, 0, 0, 1));
    }

    @Test
    @DisplayName("半径、近身与高度均为零时重叠命中且不除零")
    void zeroOverlap() {
        CylinderConeGeometry geometry = new CylinderConeGeometry(0, 0, 0, 0, 0, 0);
        assertTrue(geometry.contains(0, 0, 0, 0, 1));
        assertFalse(geometry.contains(Double.MIN_VALUE, 0, 0, 0, 1));
        assertFalse(geometry.contains(0, Double.MIN_VALUE, 0, 0, 1));
    }

    @ParameterizedTest(name = "旋转 {0}° 指向 ({1},{2})")
    @CsvSource({"90,-1,0", "-90,1,0", "450,-1,0", "-450,1,0", "360,0,1"})
    void positiveRotationTurnsRight(double rotation, double x, double z) {
        CylinderConeGeometry geometry = new CylinderConeGeometry(20, 0, rotation, 1, 1, 0);
        double[] direction = geometry.direction(0, 100, 0);
        assertArrayEquals(new double[] {x, z}, direction, 1e-12);
        assertTrue(geometry.contains(x * 10, 0, z * 10, direction[0], direction[1]));
        assertFalse(geometry.contains(-x * 10, 0, -z * 10, direction[0], direction[1]));
    }

    @ParameterizedTest(name = "俯仰 {0}° 仍保留施法者 yaw")
    @ValueSource(floats = {-90, -89.999f, 89.999f, 90})
    void extremePitchUsesYaw(float pitch) {
        Location location = new Location(null, 0, 0, 0, 90, pitch);
        Vector facing = location.getDirection();
        double[] direction =
                new CylinderConeGeometry(1, 0, 0, 1, 1, 0).direction(facing.getX(), facing.getZ(), location.getYaw());
        assertArrayEquals(new double[] {-1, 0}, direction, 1e-12);
    }

    @Test
    @DisplayName("水平投影小于阈值才回退 yaw，再施加旋转")
    void yawFallbackThreshold() {
        CylinderConeGeometry geometry = new CylinderConeGeometry(1, 0, 90, 1, 1, 0);
        assertArrayEquals(new double[] {0, -1}, geometry.direction(0, 0.000999, 90), 1e-12);
        assertArrayEquals(new double[] {-1, 0}, geometry.direction(0, 0.001, 90), 1e-12);
    }

    @Test
    @DisplayName("有限大数使用 hypot，不因平方溢出或旋转换算而失真")
    void largeFiniteValues() {
        CylinderConeGeometry geometry = new CylinderConeGeometry(1.5e308, 180, 0, 1, 1, 0);
        assertTrue(geometry.contains(1e308, 0, 1e308, 0, 1));
        assertFalse(geometry.contains(1.1e308, 0, 1.1e308, 0, 1));
        assertFalse(geometry.contains(Double.MAX_VALUE, 0, Double.MAX_VALUE, 0, 1));
        assertArrayEquals(new double[] {Math.sqrt(0.5), Math.sqrt(0.5)}, geometry.direction(1e308, 1e308, 0), 1e-12);
        double[] expected = new CylinderConeGeometry(1, 0, Double.MAX_VALUE % 360, 1, 1, 0).direction(0, 1, 0);
        assertArrayEquals(expected, new CylinderConeGeometry(1, 0, Double.MAX_VALUE, 1, 1, 0).direction(0, 1, 0));
    }

    private static boolean atAngle(CylinderConeGeometry geometry, double degrees) {
        return geometry.contains(
                Math.sin(Math.toRadians(degrees)) * 10, 0, Math.cos(Math.toRadians(degrees)) * 10, 0, 1);
    }
}
