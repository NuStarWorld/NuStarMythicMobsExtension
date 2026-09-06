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
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("圆柱扇形 Bukkit 候选选择契约")
class CylinderConeSelectorAdapterTest {
    @Test
    @DisplayName("排除自身UUID与非Living，默认包含玩家、忽略盔甲架，结果可变去重")
    void defaultFilteringAndDeduplication() {
        BukkitCylinderFixtures f = new BukkitCylinderFixtures();
        Location center = f.position(0, 0, 0);
        Entity caster = f.entity(LivingEntity.class, center);
        Entity sameId = f.entity(LivingEntity.class, caster.getUniqueId(), f.position(0, 0, 2));
        Entity nonLiving = f.entity(Entity.class, f.position(0, 0, 2));
        Entity living = f.entity(LivingEntity.class, f.position(0, 0, 5));
        Entity player = f.entity(Player.class, f.position(0, 0, 6));
        Entity armor = f.entity(ArmorStand.class, f.position(0, 0, 7));
        f.candidates.addAll(Arrays.asList(caster, sameId, nonLiving, living, living, player, armor));
        CylinderConeConfig<Object> config = config(Collections.emptyMap());
        HashSet<Entity> result = CylinderConeSelectorAdapter.select(
                config, config.evaluate(null), caster, center, center, entity -> entity);
        assertEquals(new HashSet<>(Arrays.asList(living, player)), result);
        assertEquals(HashSet.class, result.getClass());
        result.clear();
        assertTrue(result.isEmpty());
        assertEquals(1, f.queries);
        assertSame(center, f.queryArguments[0]);
        double[] exact = {11.25, 17.25, 11.25};
        for (int axis = 0; axis < exact.length; axis++) {
            double queried = (double) f.queryArguments[axis + 1];
            assertTrue(queried > exact[axis]);
            assertTrue(queried - exact[axis] <= 4 * Math.ulp(exact[axis]));
        }
    }

    @ParameterizedTest(name = "忽略玩家={0}，忽略盔甲架={1}")
    @CsvSource({"false,false,2", "true,false,1", "false,true,1", "true,true,0"})
    void configurableEntityKinds(boolean ignorePlayers, boolean ignoreArmor, int expected) {
        BukkitCylinderFixtures f = new BukkitCylinderFixtures();
        Location center = f.position(0, 0, 0);
        Entity caster = f.entity(Entity.class, center);
        Entity player = f.entity(Player.class, f.position(0, 0, 3));
        Entity armor = f.entity(ArmorStand.class, f.position(0, 0, 4));
        f.candidates.addAll(Arrays.asList(player, armor));
        Map<String, String> values = new HashMap<>();
        values.put("ignoreplayers", String.valueOf(ignorePlayers));
        values.put("ignorearmorstands", String.valueOf(ignoreArmor));
        CylinderConeConfig<Object> config = config(values);
        HashSet<Entity> result = CylinderConeSelectorAdapter.select(
                config, config.evaluate(null), caster, center, center, entity -> entity);
        assertEquals(expected, result.size());
        assertEquals(!ignorePlayers, result.contains(player));
        assertEquals(!ignoreArmor, result.contains(armor));
    }

    @Test
    @DisplayName("原点可在另一个世界，朝向只取施法者，不修改任何 Location")
    void crossWorldOriginUsesCasterFacingAndPreservesLocations() {
        BukkitCylinderFixtures casterWorld = new BukkitCylinderFixtures();
        BukkitCylinderFixtures originWorld = new BukkitCylinderFixtures();
        Location casterLocation = new Location(casterWorld.world, 900, 80, 900, 90, 90);
        Location center = new Location(originWorld.world, 100, 50, -100, -90, -90);
        Location hitLocation = originWorld.position(95, 50, -100);
        Entity caster = casterWorld.entity(LivingEntity.class, casterLocation);
        Entity hit = originWorld.entity(LivingEntity.class, hitLocation);
        Entity centerFacing = originWorld.entity(LivingEntity.class, originWorld.position(105, 50, -100));
        originWorld.candidates.addAll(Arrays.asList(hit, centerFacing));
        Location beforeCaster = casterLocation.clone();
        Location beforeCenter = center.clone();
        Location beforeHit = hitLocation.clone();
        CylinderConeConfig<Object> config = config(Collections.singletonMap("origin", "true"));
        assertEquals(
                Collections.singleton(hit),
                CylinderConeSelectorAdapter.select(
                        config, config.evaluate(null), caster, casterLocation, center, entity -> entity));
        assertEquals(beforeCaster, casterLocation);
        assertEquals(beforeCenter, center);
        assertEquals(beforeHit, hitLocation);
        assertEquals(0, casterWorld.queries);
        assertEquals(1, originWorld.queries);
        assertSame(center, originWorld.queryArguments[0]);
    }

    @Test
    @DisplayName("粗筛命中仍按位置点精判；圆柱边界外一步不因包围盒相交而命中")
    void coarseBoxCandidatesAreRefinedByPosition() {
        BukkitCylinderFixtures f = new BukkitCylinderFixtures();
        Location center = f.position(0, 0, 0);
        Entity caster = f.entity(Entity.class, center);
        Entity radiusEdge = f.entity(LivingEntity.class, f.position(0, 0, 11.25));
        Entity downEdge = f.entity(LivingEntity.class, f.position(0, -17.25, 3));
        Entity upEdge = f.entity(LivingEntity.class, f.position(0, 11.25, 3));
        Entity overlap = f.entity(LivingEntity.class, center.clone());
        f.candidates.addAll(Arrays.asList(
                radiusEdge,
                downEdge,
                upEdge,
                overlap,
                f.entity(LivingEntity.class, f.position(0, 0, Math.nextUp(11.25))),
                f.entity(LivingEntity.class, f.position(0, Math.nextDown(-17.25), 3)),
                f.entity(LivingEntity.class, f.position(0, Math.nextUp(11.25), 3)),
                f.entity(LivingEntity.class, f.position(11.25, 0, 11.25)),
                f.entity(LivingEntity.class, f.position(0, 0, -3))));
        CylinderConeConfig<Object> config = config(Collections.emptyMap());
        assertEquals(
                new HashSet<>(Arrays.asList(radiusEdge, downEdge, upEdge, overlap)),
                CylinderConeSelectorAdapter.select(
                        config, config.evaluate(null), caster, center, center, entity -> entity));
    }

    @ParameterizedTest(name = "候选数 {0} 不增加任何参数求值次数")
    @ValueSource(ints = {0, 1, 100})
    void candidatesDoNotReevaluateMetadata(int count) {
        BukkitCylinderFixtures f = new BukkitCylinderFixtures();
        Location center = f.position(0, 0, 0);
        Entity caster = f.entity(Entity.class, center);
        Object metadata = new Object();
        AtomicInteger evaluations = new AtomicInteger();
        Map<String, AtomicInteger> eachParameter = new HashMap<>();
        CylinderConeConfig<Object> config = new CylinderConeConfig<>((key, fallback) -> fallback, raw -> {
            AtomicInteger calls = new AtomicInteger();
            eachParameter.put(raw + "#" + eachParameter.size(), calls);
            return supplied -> {
                assertSame(metadata, supplied);
                evaluations.incrementAndGet();
                calls.incrementAndGet();
                return raw;
            };
        });
        for (int i = 0; i < count; i++) f.candidates.add(f.entity(LivingEntity.class, f.position(0, 0, 5)));
        AtomicInteger adaptations = new AtomicInteger();
        HashSet<UUID> result = CylinderConeSelectorAdapter.select(
                config, config.evaluate(metadata), caster, center, center, entity -> {
                    adaptations.incrementAndGet();
                    return entity.getUniqueId();
                });
        assertEquals(count, result.size());
        assertEquals(count, adaptations.get());
        assertEquals(7, evaluations.get());
        assertEquals(7, eachParameter.size());
        eachParameter.values().forEach(calls -> assertEquals(1, calls.get()));
        assertEquals(1, f.queries);
    }

    private static CylinderConeConfig<Object> config(Map<String, String> values) {
        return new CylinderConeConfig<>(values::getOrDefault, raw -> metadata -> raw);
    }
}
