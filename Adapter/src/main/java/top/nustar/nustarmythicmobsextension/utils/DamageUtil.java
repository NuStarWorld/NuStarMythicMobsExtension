package top.nustar.nustarmythicmobsextension.utils;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;

import java.util.EnumMap;

@NativeObfuscation
public class DamageUtil {

    @NativeObfuscation
    public static void damage(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity, boolean preventImmunity, boolean preventsKnockback) {
        LivingEntity source = (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
        LivingEntity target = (LivingEntity) abstractEntity.getBukkitEntity();
        skillMetadata.getCaster().setUsingDamageSkill(true);
        skillMetadata.getCaster().getEntity().setMetadata("doing-skill-damage", true);
        try {
            if (preventsKnockback) {
                target.damage(0.01);
                EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(source, target,
                        EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK,
                        new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, 0.01)),
                        new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE,
                                Functions.constant(-0.0))));
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
