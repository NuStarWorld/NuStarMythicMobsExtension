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

package top.nustar.nustarmythicmobsextension.utils;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.ActiveMobAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicInstance;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;

@Component
@Scope(Scope.SINGLETON)
@SuppressWarnings("unused")
@NativeObfuscation
public class DamageUtil {
    private static volatile MythicInstance mythicInstance;

    @Autowired
    public void setMythicInstance(MythicInstance mythicInstance) {
        DamageUtil.mythicInstance = mythicInstance;
    }

    @NativeObfuscation
    public static void damage(
            SkillMetadataAdapter<?> skillMetadata,
            AbstractEntityAdapter<?> abstractEntity,
            boolean preventImmunity,
            boolean preventsKnockback) {
        Entity source = skillMetadata.getCaster().getEntity().getBukkitEntity();
        Entity bukkitEntity = abstractEntity.getBukkitEntity();
        if (!(bukkitEntity instanceof LivingEntity)) return;
        LivingEntity target = (LivingEntity) bukkitEntity;
        skillMetadata.getCaster().setUsingDamageSkill(true);
        skillMetadata.getCaster().getEntity().setMetadata("doing-skill-damage", true);
        try {
            EntityDamageByEntityEvent damageByEntityEvent= buildDamageEvent(source, target);
            if (preventsKnockback) {
                target.damage(0.01);
                Bukkit.getPluginManager().callEvent(damageByEntityEvent);
            } else {
                target.damage(0.01, source);
            }
            ActiveMobAdapter<?> activeMobAdapter = mythicInstance.getMobManager().getMythicMobInstance(source);
            if (activeMobAdapter.getActualObject() != null && activeMobAdapter.getOwner() != null) {
                Entity parent = Bukkit.getEntity(activeMobAdapter.getOwner());
                if (!InstanceUtil.getVersion().contains("Spigot") && parent instanceof Player) {
                    target.setKiller((Player) parent);
                }
                target.setLastDamageCause(buildDamageEvent(parent, target));
            }
        } finally {
            skillMetadata.getCaster().getEntity().removeMetadata("doing-skill-damage");
            skillMetadata.getCaster().setUsingDamageSkill(false);
        }
        if (preventImmunity) {
            target.setNoDamageTicks(0);
        }
    }

    public static EntityDamageByEntityEvent buildDamageEvent(Entity source, Entity target) {
        return new EntityDamageByEntityEvent(
                source,
                target,
                EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK,
                new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, 0.01)),
                new EnumMap<>(
                        ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(-0.0))));
    }
}
