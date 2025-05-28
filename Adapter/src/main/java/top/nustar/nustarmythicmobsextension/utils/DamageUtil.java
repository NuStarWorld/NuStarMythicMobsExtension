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
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;

@NativeObfuscation
public class DamageUtil {

    @NativeObfuscation
    public static void damage(
            SkillMetadataAdapter<?> skillMetadata,
            AbstractEntityAdapter<?> abstractEntity,
            boolean preventImmunity,
            boolean preventsKnockback) {
        LivingEntity source =
                (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
        LivingEntity target = (LivingEntity) abstractEntity.getBukkitEntity();
        skillMetadata.getCaster().setUsingDamageSkill(true);
        skillMetadata.getCaster().getEntity().setMetadata("doing-skill-damage", true);
        try {
            if (preventsKnockback) {
                target.damage(0.01);
                EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(
                        source,
                        target,
                        EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK,
                        new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, 0.01)),
                        new EnumMap<>(
                                ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(-0.0))));
                Bukkit.getPluginManager().callEvent(damageByEntityEvent);
            } else {
                target.damage(0.01, source);
            }
        } finally {
            skillMetadata.getCaster().getEntity().removeMetadata("doing-skill-damage");
            skillMetadata.getCaster().setUsingDamageSkill(false);
        }
        if (preventImmunity) {
            target.setNoDamageTicks(0);
        }
    }
}
