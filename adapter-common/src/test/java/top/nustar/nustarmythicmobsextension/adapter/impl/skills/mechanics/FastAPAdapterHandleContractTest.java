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

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.*;
import static top.nustar.nustarmythicmobsextension.adapter.impl.skills.mechanics.FastAPBytecode.*;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.*;

@DisplayName("FastAP handle/伤害二进制契约（非 AP 事件或伤害集成）")
class FastAPAdapterHandleContractTest {
    @Test
    void nonIdentitySelfTargetFailsBeforeClearAndBeforeSnapshot() throws IOException {
        MethodNode cast = method(read(ADAPTER), "castAtEntity");
        List<MethodInsnNode> sameUuid = calls(cast, "java/util/UUID", "equals");
        List<MethodInsnNode> errors = calls(cast, ROOT + "exception/NSMMEException", "<init>");
        assertEquals(2, sameUuid.size());
        assertEquals(2, errors.size());
        List<MethodInsnNode> identity = calls(cast, MULTIPLIER + "$Values", "isIdentity");
        before(cast, identity.get(0), sameUuid.get(0));
        before(cast, sameUuid.get(0), errors.get(0));
        before(cast, errors.get(0), call(cast, API, "addSourceAttribute"));
        before(cast, identity.get(1), sameUuid.get(1));
        before(cast, sameUuid.get(1), errors.get(1));
        before(cast, errors.get(1), calls(cast, DATA, "<init>").get(1));
        for (MethodInsnNode error : errors) {
            assertEquals(ATHROW, next(error).getOpcode());
            String diagnostic = (String) ((LdcInsnNode) previous(error)).cst;
            assertTrue(diagnostic.contains("bam") && diagnostic.contains("baml") && diagnostic.contains("UUID"));
        }
        for (MethodInsnNode comparison : sameUuid) {
            JumpInsnNode different = (JumpInsnNode) next(comparison);
            assertEquals(IFEQ, different.getOpcode());
            before(cast, comparison, different.label);
        }
    }

    @Test
    void handleUsesSelectedAttackDataAndOriginalVictimData() throws IOException {
        MethodNode cast = method(read(ADAPTER), "castAtEntity");
        MethodInsnNode handle = call(cast, HANDLE, "<init>");
        MethodInsnNode victimData = calls(cast, API, "getAttrData").get(1);
        assertSame(victimData, previous(handle));
        int victim = variable(previous(victimData), ALOAD);
        // 目标实体经 NuStarSkill.livingTarget 读取，仍须是同一个存入 victim 的生物。
        MethodInsnNode livingTarget = call(cast, SKILL, "livingTarget");
        assertEquals(victim, variable(next(livingTarget), ASTORE));
        int basis = variable(previous(previous(call(cast, DATA, "getAttributeValue"))), ALOAD);
        assertEquals(basis, variable(previous(previous(victimData)), ALOAD));
        before(cast, call(cast, CENTRAL, "setForceAttributeValue"), handle);
    }

    @Test
    @DisplayName("非生物目标在读取属性前跳过，不抛 ClassCastException")
    void nonLivingCasterOrTargetIsSkippedBeforeAnyAttributeAccess() throws IOException {
        MethodNode cast = method(read(ADAPTER), "castAtEntity");
        MethodInsnNode livingCaster = call(cast, SKILL, "livingCaster");
        MethodInsnNode livingTarget = call(cast, SKILL, "livingTarget");
        // 两次读取都不得直接强转实体类型。
        assertTrue(
                code(cast).stream()
                        .filter(node -> node instanceof TypeInsnNode && node.getOpcode() == CHECKCAST)
                        .noneMatch(node -> "org/bukkit/entity/LivingEntity".equals(((TypeInsnNode) node).desc)),
                "不得强转 LivingEntity");
        before(cast, livingCaster, livingTarget);
        // 两次读取都完成后才判空：任一为 null 就 return false，早于 AP 属性访问与倍率求值。
        List<AbstractInsnNode> code = code(cast);
        JumpInsnNode casterNull = jumpAfter(cast, livingTarget);
        JumpInsnNode targetNull = jumpAfter(cast, casterNull);
        assertEquals(IFNULL, casterNull.getOpcode(), "施法者为 null 时跳向跳过分支");
        assertEquals(IFNONNULL, targetNull.getOpcode(), "目标非 null 时越过跳过分支");
        AbstractInsnNode skip = next(targetNull);
        assertEquals(ICONST_0, skip.getOpcode());
        assertEquals(IRETURN, next(skip).getOpcode());
        assertSame(skip, next(casterNull.label), "施法者为 null 汇合到同一个跳过分支");
        assertTrue(
                code.indexOf(skip)
                        < code.indexOf(calls(cast, API, "getAttrData").get(0)),
                "跳过分支须早于读取 AP 属性");
        assertTrue(code.indexOf(skip) < code.indexOf(call(cast, MULTIPLIER, "isConfigured")), "跳过分支须早于倍率求值");
    }

    @Test
    void attrUsesUnscaledCalculateAndAdditiveTempBeforeHandle() throws IOException {
        ClassNode type = read(ADAPTER);
        MethodNode cast = method(type, "castAtEntity");
        MethodNode attr = containing(type, HANDLE, "updateTempAttributeValue");
        MethodInsnNode update = call(attr, HANDLE, "updateTempAttributeValue");
        assertEquals(ICONST_0, previous(update).getOpcode(), "false 保持 temp 加法");
        MethodInsnNode calculate = call(attr, "team/idealstate/sugar/next/calculate/Expression", "calculate");
        assertEquals(variable(next(calculate), ASTORE), variable(previous(previous(update)), ALOAD));
        assertTrue(calls(attr, MULTIPLIER + "$Values", "scale").isEmpty());
        MethodInsnNode toDefault = call(attr, API, "getDefaultAttributeName");
        List<AbstractInsnNode> attrCode = code(attr);
        int index = attrCode.indexOf(toDefault);
        JumpInsnNode known = (JumpInsnNode) attrCode.get(index + 3);
        assertEquals(IFNONNULL, known.getOpcode());
        assertEquals(variable(previous(toDefault), ALOAD), variable(next(known), ALOAD), "旧未知 attr 回退原名");
        MethodInsnNode forEach = call(cast, "java/util/Map", "forEach");
        InvokeDynamicInsnNode binding = (InvokeDynamicInsnNode) previous(forEach);
        assertEquals(attr.name, ((Handle) binding.bsmArgs[1]).getName());
        before(cast, call(cast, HANDLE, "<init>"), forEach);
        before(cast, forEach, call(cast, HANDLE, "handleAttackOrDefenseAttribute"));
    }

    @Test
    void reentryCancellationDamageAndOptionalEffectsRemainGuarded() throws IOException {
        MethodNode cast = method(read(ADAPTER), "castAtEntity");
        List<AbstractInsnNode> code = code(cast);
        assertEquals("IN_FASTAP_DAMAGE", ((FieldInsnNode) code.get(0)).name);
        assertEquals("get", ((MethodInsnNode) code.get(1)).name);
        assertEquals(IFEQ, code.get(4).getOpcode());
        assertEquals(ICONST_1, code.get(5).getOpcode());
        assertEquals(IRETURN, code.get(6).getOpcode());
        MethodInsnNode cancelled = call(cast, HANDLE, "isCancelled");
        assertEquals(IFEQ, next(cancelled).getOpcode());
        assertEquals(ICONST_0, next(next(cancelled)).getOpcode());
        assertEquals(IRETURN, next(next(next(cancelled))).getOpcode());
        MethodInsnNode damage = call(cast, HANDLE, "getDamage");
        MethodInsnNode nms = call(cast, ROOT + "utils/DamageUtil", "nmsDamage");
        assertEquals(variable(next(damage), DSTORE), variable(code.get(code.indexOf(nms) - 4), DLOAD));
        assertEquals("IN_FASTAP_DAMAGE", ((FieldInsnNode) previous(nms)).name);
        assertFalse(code.stream().anyMatch(node -> node.getOpcode() == DMUL), "倍率不得后乘最终伤害");
        for (String[] guard : new String[][] {
            {"preventImmunity", "setNoDamageTicks"},
            {"preventKnockback", "setVelocity"},
            {"sendMessage", "sendAttributeMessage"}
        }) {
            FieldInsnNode field = (FieldInsnNode) code.stream()
                    .filter(node -> node instanceof FieldInsnNode && guard[0].equals(((FieldInsnNode) node).name))
                    .findFirst()
                    .orElseThrow(AssertionError::new);
            JumpInsnNode skip = (JumpInsnNode) next(field);
            MethodInsnNode effect =
                    call(cast, guard[0].equals("sendMessage") ? HANDLE : "org/bukkit/entity/LivingEntity", guard[1]);
            assertEquals(IFEQ, skip.getOpcode());
            before(cast, skip, effect);
            before(cast, effect, skip.label);
            before(cast, cancelled, effect);
        }
        before(cast, damage, nms);
        before(cast, nms, call(cast, HANDLE, "sendAttributeMessage"));
    }
}
