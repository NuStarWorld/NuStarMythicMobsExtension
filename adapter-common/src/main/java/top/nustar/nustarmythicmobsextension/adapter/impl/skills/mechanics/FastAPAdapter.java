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
    private static final String SCALED_SELF_ERROR =
            "FastAP 参数 baseAttributeMultiple(bam) / baseAttributeMultipleList(baml)："
                    + "非恒等有效倍率不支持施法者与目标 UUID 相同，AP 会覆盖攻击端快照";
    private final MainConfiguration mainConfiguration;
    private final Map<String, Expression> attrExpressionMap = new HashMap<>();
    private final FastAPBaseMultiplier baseMultiplier;
    protected final boolean clear;
    protected final boolean preventImmunity;
    protected final boolean preventKnockback;
    protected final boolean sendMessage;
    protected final NextDamageSource nextDamageSource;

    public FastAPAdapter(MythicLineConfigAdapter<?> mlc, MainConfiguration mainConfiguration) {
        this.mainConfiguration = mainConfiguration;
        String attrString = mlc.getString(new String[] {"attr", "a"});
        // 仅增加省略 attr；分段、表达式解析及同名覆盖继续沿用旧行为。
        if (attrString != null) {
            for (String attr : attrString.split(",")) {
                String[] attrLine = attr.split(":");
                if (attrLine.length != 2) {
                    throw new NSMMEException("FastAP 参数 attr 格式错误：" + attr);
                }
                attrExpressionMap.put(attrLine[0], new Expression(attrLine[1]).compile());
            }
        }
        this.baseMultiplier = new FastAPBaseMultiplier(
                mlc.getString(new String[] {"baseAttributeMultiple", "bam"}),
                mlc.getString(new String[] {"baseAttributeMultipleList", "baml"}),
                raw -> {
                    String defaultName = AttributeAPI.getDefaultAttributeName(raw);
                    return AttributeAPI.getServerAttributeName(defaultName == null ? raw : defaultName);
                });
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

        // 常量倍率不提前读取 PAPI；动态倍率首次取变量时才固定本目标上下文。
        Map<String, Number> multiplierContext = baseMultiplier.isConfigured()
                ? FastAPBaseMultiplier.lazyContext(() -> parseExpressionContext(
                        skillMetadata,
                        abstractEntity,
                        mainConfiguration.getVariables().values()))
                : null;
        FastAPBaseMultiplier.Values multipliers =
                multiplierContext == null ? null : baseMultiplier.evaluate(multiplierContext);
        // AP 返回 live values，先复制，且不以基础值为 0 冒充倍率恒等。
        Set<String> baseAttributeNames = multipliers == null ? null : new LinkedHashSet<>(AttributeAPI.allServerKey());
        if (multipliers != null && !multipliers.isIdentity(baseAttributeNames)) {
            if (caster.getUniqueId().equals(victim.getUniqueId())) {
                // 在 clear 的既有白名单事件之前拒绝注册属性上的非恒等自伤。
                throw new NSMMEException(SCALED_SELF_ERROR);
            }
            if (!attrExpressionMap.isEmpty()) {
                // 非恒等且有 attr 时，在 clear 事件前固定共享上下文；无 attr 不做多余读取。
                multiplierContext.entrySet();
            }
        }

        AttributeData casterAttrData = AttributeAPI.getAttrData(caster);
        AttributeData attackData;
        if (clear) {
            attackData = new AttributeData();
            attackData.setSourceEntity(caster);
            // 给予白名单属性
            AttributeAPI.addSourceAttribute(
                    attackData,
                    "APMM_WhiteList",
                    AttributeUtils.getWhiteAttributeList(casterAttrData, mainConfiguration.getWhiteAttrList()));
        } else {
            attackData = casterAttrData;
        }

        boolean scaledSnapshot = false;
        if (multipliers != null) {
            // 注册名覆盖仅 force 的属性；来源名补上 raw 属性，不改 AP 返回的源集合。
            baseAttributeNames.addAll(attackData
                    .getCentral()
                    .getAttributes(attackData
                            .getCentral()
                            .getAttributeSources(null, false)
                            .values()));
            if (!multipliers.isIdentity(baseAttributeNames)) {
                if (caster.getUniqueId().equals(victim.getUniqueId())) {
                    throw new NSMMEException(SCALED_SELF_ERROR);
                }
                if (!attrExpressionMap.isEmpty()) {
                    // 仅 raw 来源扩名才显露的非恒等在此固定；已固定的 Map 不会再次读变量。
                    multiplierContext.entrySet();
                }
                AttributeData scaledData = new AttributeData();
                scaledData.setSourceEntity(caster);
                scaledData.setLastAttackMillis(attackData.getLastAttackMillis());
                scaledData.setLastDefenseMillis(attackData.getLastDefenseMillis());
                // 只隔离本次 handle 的有效数值；不复制回指原 data 的变量、持久源或计数器。
                for (String serverName : baseAttributeNames) {
                    // 百分比已物化在来源内，按名读取一次；不可再次刷新或应用系数。
                    Number[] scaled = multipliers.scale(serverName, attackData.getAttributeValue(serverName));
                    // force 仅写新中心，避免源事件、corrector 和第二次 Central cap。
                    // AP 后续 SubAttribute cap、概率与防御仍由原 handle 执行。
                    scaledData
                            .getCentral()
                            .setForceAttributeValue(serverName, scaled[0].doubleValue(), scaled[1].doubleValue());
                }
                attackData = scaledData;
                scaledSnapshot = true;
            }
        }

        AttributeHandle attributeHandle = new AttributeHandle(attackData, AttributeAPI.getAttrData(victim));
        // 恒等/未配置时仍在 clear 和 handle 构造后逐 attr 读取，保留动态变量时机。
        Map<String, Number> attrContext = scaledSnapshot ? multiplierContext : null;
        attrExpressionMap.forEach((key, value) -> {
            Number calculate = value.calculate(
                    attrContext != null
                            ? attrContext
                            : parseExpressionContext(
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
