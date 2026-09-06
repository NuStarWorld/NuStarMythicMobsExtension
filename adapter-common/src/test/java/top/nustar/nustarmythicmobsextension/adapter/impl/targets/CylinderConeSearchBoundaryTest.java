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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("严格 AABB 宽阶段与闭区间位置精判")
class CylinderConeSearchBoundaryTest {
    @ParameterizedTest(name = "原始半高 {0} 的上界脚部确实被严格 AABB 排除")
    @ValueSource(doubles = {0, 2})
    void fixtureReproducesStrictUpperBoundary(double height) {
        BukkitCylinderFixtures f = new BukkitCylinderFixtures();
        f.strictAabb = true;
        Location center = f.position(0, 64, 0);
        Entity upper = f.entity(LivingEntity.class, f.position(0, 64 + height, 3));
        f.candidates.add(upper);
        assertTrue(f.world.getNearbyEntities(center, 10, height, 10).isEmpty());
        assertEquals(1, f.candidates.size());
    }

    @ParameterizedTest(name = "中心({0},{1},{2}) r={3} down={4} up={5} 保留闭边界")
    @CsvSource({
        "0,64,0,10,0,0",
        "0,64,0,0,0,0",
        "0,64,0,10,2,2",
        "0,64,0,10,1,3",
        "0,64,0,10,3,1",
        "0,0,0,0,0,0",
        "30000000,30000000,30000000,0,0,0",
        "30000000,30000000,30000000,10,2,2",
        "-30000000,-30000000,-30000000,0,0,0",
        "-30000000,-30000000,-30000000,10,1,3"
    })
    void closedPointsSurviveStrictBroadPhase(double x, double y, double z, double radius, double down, double up) {
        BukkitCylinderFixtures f = new BukkitCylinderFixtures();
        f.strictAabb = true;
        Location center = f.position(x, y, z);
        Location before = center.clone();
        Entity caster = f.entity(Entity.class, center);
        double front = Math.min(radius, 3);
        Entity overlap = f.entity(LivingEntity.class, center.clone());
        Entity upper = f.entity(LivingEntity.class, f.position(x, y + up, z + front));
        Entity lower = f.entity(LivingEntity.class, f.position(x, y - down, z + front));
        Entity radiusX = f.entity(LivingEntity.class, f.position(x + radius, y, z));
        Entity radiusZ = f.entity(LivingEntity.class, f.position(x, y, z + radius));
        Entity negativeX = f.entity(LivingEntity.class, f.position(x - radius, y, z));
        Entity negativeZ = f.entity(LivingEntity.class, f.position(x, y, z - radius));
        Entity outsideX = f.entity(LivingEntity.class, f.position(Math.nextUp(x + radius), y, z));
        Entity outsideZ = f.entity(LivingEntity.class, f.position(x, y, Math.nextUp(z + radius)));
        Entity outsideUp = f.entity(LivingEntity.class, f.position(x, Math.nextUp(y + up), z + front));
        Entity outsideDown = f.entity(LivingEntity.class, f.position(x, Math.nextDown(y - down), z + front));
        HashSet<Entity> expected =
                new HashSet<>(Arrays.asList(overlap, upper, lower, radiusX, radiusZ, negativeX, negativeZ));
        f.candidates.addAll(expected);
        f.candidates.addAll(Arrays.asList(outsideX, outsideZ, outsideUp, outsideDown));
        Map<String, String> values = new HashMap<>();
        values.put("r", String.valueOf(radius));
        values.put("a", "180");
        values.put("down", String.valueOf(down));
        values.put("up", String.valueOf(up));
        values.put("padding", "0");
        values.put("close", "0");
        CylinderConeConfig<Object> config = new CylinderConeConfig<>(values::getOrDefault, raw -> metadata -> raw);
        HashSet<Entity> result = CylinderConeSelectorAdapter.select(
                config, config.evaluate(null), caster, center, center, entity -> entity);
        assertEquals(expected, result);
        // 半径外一步仍可能包围盒相交，必须是位置精判排除，而非 fixture 丢弃。
        assertTrue(f.nearbyCandidates.contains(outsideX));
        assertTrue(f.nearbyCandidates.contains(outsideZ));
        assertTrue(f.nearbyCandidates.contains(outsideDown));
        if (down > up) assertTrue(f.nearbyCandidates.contains(outsideUp));
        assertEquals(before, center);
        assertEquals(1, f.queries);
        assertSame(center, f.queryArguments[0]);
        assertTinyExpansion(x, radius, (double) f.queryArguments[1]);
        assertTinyExpansion(y, Math.max(down, up), (double) f.queryArguments[2]);
        assertTinyExpansion(z, radius, (double) f.queryArguments[3]);
    }

    private static void assertTinyExpansion(double center, double range, double expanded) {
        assertTrue(Double.isFinite(expanded));
        assertTrue(center - expanded < center - range);
        assertTrue(center + expanded > center + range);
        assertTrue(expanded >= range);
        assertTrue(
                expanded - range <= 4 * Math.max(Math.ulp(center), Math.ulp(range)),
                "宽阶段只允许少量 ULP 裕量：" + expanded + " / " + range);
    }
}
