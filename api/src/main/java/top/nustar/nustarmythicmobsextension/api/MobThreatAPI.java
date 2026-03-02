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

package top.nustar.nustarmythicmobsextension.api;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.api.service.MobThreatService;

/**
 * @author : NuStar Date : 2025/8/3 14:03 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
@Component
@Scope(Scope.SINGLETON)
@SuppressWarnings("unused")
public class MobThreatAPI {
    private static MobThreatService mobThreatService;

    public static void transferMobThreat(UUID from, LivingEntity target) {
        mobThreatService.transferMobThreat(from, target);
    }

    public static void addThreat(Creature mob, UUID attackerUid, long threat) {
        mobThreatService.addThreat(mob, attackerUid, threat);
    }

    public static void setThreat(Creature mob, UUID attackerUid, long threat) {
        mobThreatService.setThreat(mob, attackerUid, threat);
    }

    public static void deleteThreat(Creature mob, UUID attackerUid) {
        mobThreatService.deleteThreat(mob, attackerUid);
    }

    public static void topThreat(Creature mob, UUID attackerUid) {
        mobThreatService.topThreat(mob, attackerUid);
    }

    public static Optional<UUID> getTopThreat(Creature mob) {
        return mobThreatService.getTopThreat(mob);
    }

    public static void setTarget(Creature mob) {
        mobThreatService.setTarget(mob);
    }

    public static void clearThreat(UUID uuid) {
        mobThreatService.clearThreat(uuid);
    }

    public static void removeMobThreat(UUID mobUid) {
        mobThreatService.removeMobThreat(mobUid);
    }

    public static Map<UUID, Long> getMobThreatMap(Creature mob) {
        return mobThreatService.getMobThreatMap(mob);
    }

    @Autowired
    public void setMobThreatService(MobThreatService mobThreatService) {
        MobThreatAPI.mobThreatService = mobThreatService;
    }
}
