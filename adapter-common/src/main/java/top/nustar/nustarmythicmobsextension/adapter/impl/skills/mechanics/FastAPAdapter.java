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

package top.nustar.nustarmythicmobsextension.adapter.impl.skills.mechanics;

import java.util.*;
import org.bukkit.entity.LivingEntity;
import org.serverct.ersha.api.AttributeAPI;
import org.serverct.ersha.attribute.AttributeHandle;
import org.serverct.ersha.attribute.data.AttributeData;
import team.idealstate.sugar.next.calculate.Expression;
import top.nustar.minecraft.next.spigot.nms.common.adapter.NextDamageSource;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicLineConfigAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.GlobalVariable;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarSkill;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.exception.NSMMEException;
import top.nustar.nustarmythicmobsextension.utils.AttributeUtils;
import top.nustar.nustarmythicmobsextension.utils.DamageUtil;

public class FastAPAdapter implements NuStarSkill, GlobalVariable {
    private static final ThreadLocal<Boolean> IN_FASTAP_DAMAGE = ThreadLocal.withInitial(() -> false);
    private final MainConfiguration mainConfiguration;
    private final Map<String, Expression> attrExpressionMap = new HashMap<>();
    protected final boolean clear;
    protected final boolean preventImmunity;
    protected final boolean preventKnockback;
    protected final boolean sendMessage;
    protected final NextDamageSource nextDamageSource;

    public FastAPAdapter(MythicLineConfigAdapter<?> mlc, MainConfiguration mainConfiguration) {
        this.mainConfiguration = mainConfiguration;
        String attrString = mlc.getString(new String[] {"attr", "a"});
        String[] attrSplit = attrString.split(",");
        for (String attr : attrSplit) {
            String[] attrLine = attr.split(":");
            if (attrLine.length != 2) {
                throw new NSMMEException("Invalid attribute format:" + attr);
            }
            attrExpressionMap.put(attrLine[0], new Expression(attrLine[1]).compile());
        }
        this.clear = mlc.getBoolean(new String[] {"clear", "c"}, false);
        this.sendMessage = mlc.getBoolean(new String[] {"sendMessage", "sm"}, true);
        this.preventImmunity = mlc.getBoolean(new String[] {"preventImmunity", "pi"}, false);
        this.preventKnockback = mlc.getBoolean(new String[] {"preventKnockback", "pk"}, false);
        this.nextDamageSource =
                NextDamageSource.valueOf(mlc.getString(new String[] {"damagecause", "cause", "dc"}, "GENERIC"));
    }

    public boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity) {
        if (IN_FASTAP_DAMAGE.get()) {
            return true;
        }
        LivingEntity caster =
                (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
        LivingEntity victim = (LivingEntity) abstractEntity.getBukkitEntity();

        AttributeData casterAttrData = AttributeAPI.getAttrData(caster);
        AttributeData attackData;
        if (clear) {
            attackData = AttributeData.Companion.create(caster);
            // 给予白名单属性
            AttributeAPI.addSourceAttribute(
                    attackData,
                    "APMM_WhiteList",
                    AttributeUtils.getWhiteAttributeList(casterAttrData, mainConfiguration.getWhiteAttrList()));
        } else {
            attackData = casterAttrData;
        }

        AttributeHandle attributeHandle = new AttributeHandle(attackData, AttributeAPI.getAttrData(victim));
        // 写入属性
        attrExpressionMap.forEach((key, value) -> {
            Number calculate = value.calculate(parseExpressionContext(
                    skillMetadata,
                    abstractEntity,
                    mainConfiguration.getVariables().values()));
            String defaultAttributeName = AttributeAPI.getDefaultAttributeName(key);
            if (defaultAttributeName == null) {
                defaultAttributeName = key;
            }
            attributeHandle.updateTempAttributeValue(caster, defaultAttributeName, calculate, false);
        });
        // 运行 handle
        attributeHandle.handleAttackOrDefenseAttribute();
        // 被阻止触发
        if (attributeHandle.isCancelled()) {
            return false;
        }

        // 无视无敌帧
        if (preventImmunity) {
            victim.setNoDamageTicks(0);
        }

        // 造成伤害
        double finalDamage = attributeHandle.getDamage(caster);

        DamageUtil.nmsDamage(skillMetadata, abstractEntity, finalDamage, nextDamageSource, IN_FASTAP_DAMAGE);

        // 取消击退
        if (preventKnockback) {
            victim.setVelocity(victim.getVelocity().zero());
        }

        // 发送消息
        if (sendMessage) {
            attributeHandle.sendAttributeMessage();
        }

        return true;
    }
}
