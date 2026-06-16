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

package top.nustar.nustarmythicmobsextension.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.entity.MobThreat;

/**
 * @author : NuStar Date : 2025/6/24 21:09 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
@Component
@Scope(Scope.SINGLETON)
@SuppressWarnings({"unused"})
public class MobThreatManager {
    private final Map<UUID, MobThreat> mobThreatMap = new ConcurrentHashMap<>();

    public MobThreat getMobThreat(Creature mob) {
        return mobThreatMap.computeIfAbsent(mob.getUniqueId(), uuid -> new MobThreat(mob));
    }

    public void updateMobThreat(Creature mob, Consumer<MobThreat> consumer) {
        MobThreat mobThreat = getMobThreat(mob);
        consumer.accept(mobThreat);
    }

    public void removeMobThreat(UUID mobUid) {
        mobThreatMap.remove(mobUid);
    }

    public void transferMobThreat(UUID from, LivingEntity target) {
        mobThreatMap.values().forEach(mobThreat -> mobThreat.transferThreat(from, target.getUniqueId()));
    }

    public void clearThreat(UUID uuid) {
        mobThreatMap.values().forEach(mobThreat -> mobThreat.removeEntityThreat(uuid));
    }
}
