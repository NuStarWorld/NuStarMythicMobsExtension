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

package top.nustar.nustarmythicmobsextension.adapter.impl.skills.placeholders;

import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderMetaAdapter;
import top.nustar.nustarmythicmobsextension.manager.MobThreatManager;
import top.nustar.nustarmythicmobsextension.utils.InstanceUtil;

/**
 * @author : NuStar Date : 2025/7/1 00:27 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
@Component
@Scope(Scope.SINGLETON)
@SuppressWarnings("unused")
public class NuStarThreatTopAdapter {
    @Getter
    private final BiFunction<PlaceholderMetaAdapter<?>, String, String> transformer;

    private volatile MobThreatManager mobThreatManager;

    @Autowired
    public void setMobThreatManager(MobThreatManager mobThreatManager) {
        this.mobThreatManager = mobThreatManager;
    }

    public NuStarThreatTopAdapter() {
        this.transformer = ((placeholderMetaAdapter, string) -> {
            // 触发者可能是展示实体等非生物对象，不强转以免抛异常。
            Entity trigger = placeholderMetaAdapter.getTrigger().getBukkitEntity();
            if (!(trigger instanceof Creature)) return "该怪物不具备威胁度功能";
            Creature mob = (Creature) trigger;
            return mobThreatManager
                    .getMobThreat(mob)
                    .getTopThreat()
                    .map(uuid -> {
                        Entity topEntity;
                        try {
                            topEntity = Bukkit.getScheduler()
                                    .callSyncMethod(InstanceUtil.getInstance(), () -> Bukkit.getEntity(uuid))
                                    .get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                        if (topEntity == null) return "无目标";
                        return topEntity.getName();
                    })
                    .orElseGet(() -> {
                        if (mob.getTarget() != null) {
                            return mob.getTarget().getName();
                        }
                        return "无目标";
                    });
        });
    }
}
