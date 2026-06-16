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

package top.nustar.nustarmythicmobsextension.subcribers;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import top.nustar.nustarmythicmobsextension.adapter.ActiveMobAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicInstance;
import top.nustar.nustarmythicmobsextension.api.service.MobThreatService;
import top.nustar.nustarmythicmobsextension.utils.DamageUtil;

/**
 * @author : NuStar Date : 2025/6/24 19:35 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
@Component
@SuppressWarnings("unused")
public class MobDamageListener implements Listener {
    private volatile MythicInstance mythicInstance;
    private volatile MobThreatService mobThreatService;

    @Autowired
    public void setMythicInstance(MythicInstance mythicInstance) {
        this.mythicInstance = mythicInstance;
    }

    @Autowired
    public void setMobThreatService(MobThreatService mobThreatService) {
        this.mobThreatService = mobThreatService;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSummoned(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        EntityDamageEvent lastDamageCause = entity.getLastDamageCause();
        if (!(lastDamageCause instanceof EntityDamageByEntityEvent)) return;
        if (entity.getKiller() != null) {
            entity.setLastDamageCause(DamageUtil.buildDamageEvent(entity.getKiller(), entity));
        }
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            mobThreatService.transferMobThreat(player.getUniqueId(), killer);
        }
        mobThreatService.clearThreat(player.getUniqueId());
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Creature)) return;
        mobThreatService.removeMobThreat(entity.getUniqueId());
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        if (target == null) return;
        Entity entity = event.getEntity();
        if (!validateMM(entity)) return;
        mobThreatService.getTopThreat((Creature) entity).ifPresent(topThreat -> {
            if (topThreat.equals(target.getUniqueId())) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (entity instanceof Player) return;
        if (!validateMM(entity)) return;
        long damage = (long) event.getFinalDamage();
        Creature creature = (Creature) entity;
        mobThreatService.addThreat(creature, damager.getUniqueId(), damage);
        mobThreatService.setTarget(creature);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean validateMM(Entity entity) {
        if (!(entity instanceof Creature)) return false;
        ActiveMobAdapter<?> activeMobAdapter = mythicInstance.getMobManager().getMythicMobInstance(entity);
        return activeMobAdapter.getActualObject() != null;
    }
}
