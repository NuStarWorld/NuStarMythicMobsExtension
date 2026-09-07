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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import team.idealstate.sugar.next.calculate.Expression;

@DisplayName("FastAP Adapter 配置与上下文二进制契约（非 AP 运行时集成）")
class FastAPAdapterConfigContractTest {
    @Test
    void aliasesArePassedLongNameFirstWithoutDefaultOverload() throws IOException {
        MethodNode ctor = method(read(ADAPTER), "<init>");
        List<MethodInsnNode> reads = calls(ctor, ROOT + "adapter/MythicLineConfigAdapter", "getString");
        assertEquals(4, reads.size());
        for (int index = 0; index < 3; index++) {
            String[] expected = Arrays.asList(
                            new String[] {"attr", "a"},
                            new String[] {"baseAttributeMultiple", "bam"},
                            new String[] {"baseAttributeMultipleList", "baml"})
                    .get(index);
            MethodInsnNode read = reads.get(index);
            assertEquals("([Ljava/lang/String;)Ljava/lang/String;", read.desc);
            List<AbstractInsnNode> code = code(ctor);
            int end = code.indexOf(read);
            assertEquals(expected[0], ((LdcInsnNode) code.get(end - 6)).cst);
            assertEquals(expected[1], ((LdcInsnNode) code.get(end - 2)).cst);
            assertEquals(ICONST_0, code.get(end - 7).getOpcode());
            assertEquals(ICONST_1, code.get(end - 3).getOpcode());
        }
        before(ctor, reads.get(2), call(ctor, MULTIPLIER, "<init>"));
    }

    @Test
    void attrNullSkipsParserWhileLegacySplitAndSameRawKeyOverwriteRemain() throws IOException {
        MethodNode ctor = method(read(ADAPTER), "<init>");
        JumpInsnNode absent = (JumpInsnNode) code(ctor).stream()
                .filter(node -> node.getOpcode() == IFNULL)
                .findFirst()
                .orElseThrow(AssertionError::new);
        MethodInsnNode put = call(ctor, "java/util/Map", "put");
        before(ctor, absent, put);
        before(ctor, put, absent.label);
        before(ctor, absent.label, call(ctor, MULTIPLIER, "<init>"));
        assertEquals(2, calls(ctor, "java/lang/String", "split").size());
        for (MethodInsnNode split : calls(ctor, "java/lang/String", "split")) {
            assertEquals("(Ljava/lang/String;)[Ljava/lang/String;", split.desc, "旧 attr 必须丢弃末尾空段");
        }
        assertTrue(calls(ctor, "java/lang/String", "trim").isEmpty(), "不能把新 baml 的严格校验扩展到旧 attr");
        assertTrue(calls(ctor, "java/lang/String", "isEmpty").isEmpty());
        call(ctor, ROOT + "exception/NSMMEException", "<init>");
        assertTrue(calls(ctor, "java/util/Map", "putIfAbsent").isEmpty());
        assertTrue(calls(ctor, "java/util/Map", "containsKey").isEmpty(), "旧 attr 同名覆盖不能改为去重报错");
    }

    @ParameterizedTest
    @ValueSource(strings = {"物理伤害:10,", "物理伤害:10:", ","})
    void legacyAttrExamplesUseProductionBoundJdkSplitAndRealExpression(String source) throws Exception {
        MethodNode ctor = method(read(ADAPTER), "<init>");
        List<MethodInsnNode> splits = calls(ctor, "java/lang/String", "split");
        assertEquals(2, splits.size());
        for (MethodInsnNode split : splits) {
            assertEquals("(Ljava/lang/String;)[Ljava/lang/String;", split.desc);
        }
        String comma = (String) ((LdcInsnNode) previous(splits.get(0))).cst;
        String colon = (String) ((LdcInsnNode) previous(splits.get(1))).cst;
        assertEquals(",", comma);
        assertEquals(":", colon);
        // 用编译产物绑定的真实 JDK 方法做小探针，不加载依赖服务器的 Adapter/AP。
        java.lang.reflect.Method split = String.class.getMethod(splits.get(0).name, String.class);
        Map<String, Number> parsed = new HashMap<>();
        for (String entry : (String[]) split.invoke(source, comma)) {
            String[] pair = (String[]) split.invoke(entry, colon);
            assertEquals(2, pair.length);
            parsed.put(pair[0], new Expression(pair[1]).compile().calculate(Collections.emptyMap()));
        }
        assertEquals(source.equals(",") ? 0 : 1, parsed.size());
        if (!parsed.isEmpty()) assertEquals(10L, parsed.get("物理伤害").longValue());
    }

    @Test
    void bamlNormalizerUsesServerToDefaultThenDefaultToServer() throws IOException {
        MethodNode normalize = containing(read(ADAPTER), API, "getServerAttributeName");
        MethodInsnNode toDefault = call(normalize, API, "getDefaultAttributeName");
        MethodInsnNode toServer = call(normalize, API, "getServerAttributeName");
        before(normalize, toDefault, toServer);
        List<AbstractInsnNode> code = code(normalize);
        assertEquals(IFNONNULL, code.get(4).getOpcode());
        assertEquals(0, variable(code.get(5), ALOAD), "英文 default 名保留 raw");
        assertEquals(variable(next(toDefault), ASTORE), variable(previous(toServer), ALOAD));
    }

    @Test
    void onlyCompletedNonIdentitySnapshotSharesContextWithAttrLambda() throws IOException {
        ClassNode type = read(ADAPTER);
        MethodNode cast = method(type, "castAtEntity");
        List<AbstractInsnNode> instructions = code(cast);
        MethodInsnNode identity =
                calls(cast, MULTIPLIER + "$Values", "isIdentity").get(1);
        JumpInsnNode identitySkip = (JumpInsnNode) next(identity);
        assertEquals(IFNE, identitySkip.getOpcode());
        AbstractInsnNode handleNew = next(identitySkip.label);
        int scaledFlag = variable(previous(handleNew), ISTORE);
        assertEquals(ICONST_1, previous(previous(handleNew)).getOpcode());
        List<AbstractInsnNode> flagWrites = instructions.stream()
                .filter(node -> node.getOpcode() == ISTORE && ((VarInsnNode) node).var == scaledFlag)
                .collect(java.util.stream.Collectors.toList());
        assertEquals(2, flagWrites.size(), "只有初始 false 与完成快照后的 true 两次赋值");
        assertEquals(ICONST_0, previous(flagWrites.get(0)).getOpcode());
        JumpInsnNode unconfiguredSkip = (JumpInsnNode) next(next(flagWrites.get(0)));
        assertEquals(IFNULL, unconfiguredSkip.getOpcode());
        assertSame(identitySkip.label, unconfiguredSkip.label, "未配置和恒等均绕过置 true");
        before(cast, flagWrites.get(0), identity);
        before(cast, call(cast, CENTRAL, "setForceAttributeValue"), flagWrites.get(1));
        before(cast, flagWrites.get(1), identitySkip.label);
        assertEquals(NEW, handleNew.getOpcode());
        assertEquals(HANDLE, ((TypeInsnNode) handleNew).desc);

        // 追踪标志到三元表达式两分支：false 生成 null，true 只能取已求值的倍率 Map。
        List<AbstractInsnNode> flagReads = instructions.stream()
                .filter(node -> node.getOpcode() == ILOAD && ((VarInsnNode) node).var == scaledFlag)
                .collect(java.util.stream.Collectors.toList());
        assertEquals(1, flagReads.size());
        JumpInsnNode noShare = (JumpInsnNode) next(flagReads.get(0));
        assertEquals(IFEQ, noShare.getOpcode());
        int multiplierContext = variable(previous(call(cast, MULTIPLIER, "evaluate")), ALOAD);
        assertEquals(multiplierContext, variable(next(noShare), ALOAD));
        JumpInsnNode contextReady = (JumpInsnNode) next(next(noShare));
        assertEquals(GOTO, contextReady.getOpcode());
        assertEquals(ACONST_NULL, next(noShare.label).getOpcode());
        assertSame(next(contextReady.label), next(next(noShare.label)));
        int attrContext = variable(next(contextReady.label), ASTORE);
        before(cast, call(cast, API, "addSourceAttribute"), flagReads.get(0));
        before(cast, call(cast, HANDLE, "<init>"), flagReads.get(0));

        // 核实被捕获的确实是选出的 attrContext，而非仍捕获旧 multiplierContext。
        MethodInsnNode forEach = call(cast, "java/util/Map", "forEach");
        InvokeDynamicInsnNode binding = (InvokeDynamicInsnNode) previous(forEach);
        Type[] captures = Type.getArgumentTypes(binding.desc);
        int mapCapture = Arrays.asList(captures).indexOf(Type.getType(Map.class));
        assertTrue(mapCapture >= 0);
        assertEquals(
                attrContext,
                variable(instructions.get(instructions.indexOf(binding) - captures.length + mapCapture), ALOAD));
        MethodNode attr = containing(type, HANDLE, "updateTempAttributeValue");
        assertEquals(attr.name, ((Handle) binding.bsmArgs[1]).getName());
        Type[] arguments = Type.getArgumentTypes(attr.desc);
        int contextArgument = Arrays.asList(arguments).indexOf(Type.getType(Map.class));
        assertTrue(contextArgument >= 0);
        int contextSlot = (attr.access & ACC_STATIC) == 0 ? 1 : 0;
        for (int index = 0; index < contextArgument; index++) contextSlot += arguments[index].getSize();
        List<AbstractInsnNode> attrCode = code(attr);
        assertEquals(contextSlot, variable(attrCode.get(1), ALOAD));
        JumpInsnNode freshContext = (JumpInsnNode) attrCode.get(2);
        assertEquals(IFNULL, freshContext.getOpcode());
        assertEquals(contextSlot, variable(attrCode.get(3), ALOAD));
        JumpInsnNode sharedContext = (JumpInsnNode) attrCode.get(4);
        MethodInsnNode freshRead = call(attr, ADAPTER, "parseExpressionContext");
        before(attr, freshContext.label, freshRead);
        assertSame(next(freshRead), next(sharedContext.label));
        assertSame(
                call(attr, "team/idealstate/sugar/next/calculate/Expression", "calculate"), next(sharedContext.label));
        before(cast, forEach, call(cast, HANDLE, "handleAttackOrDefenseAttribute"));
    }

    @Test
    void nonIdentityWithAttrMaterializesBeforeClearOrBeforeRawOnlySnapshot() throws IOException {
        MethodNode cast = method(read(ADAPTER), "castAtEntity");
        List<MethodInsnNode> identities = calls(cast, MULTIPLIER + "$Values", "isIdentity");
        List<MethodInsnNode> materialize = calls(cast, "java/util/Map", "entrySet");
        List<MethodInsnNode> empty = calls(cast, "java/util/Map", "isEmpty");
        assertEquals(2, identities.size());
        assertEquals(2, materialize.size());
        assertEquals(2, empty.size());
        int lazySlot = variable(previous(call(cast, MULTIPLIER, "evaluate")), ALOAD);
        for (int index = 0; index < 2; index++) {
            JumpInsnNode identitySkip = (JumpInsnNode) next(identities.get(index));
            assertEquals(IFNE, identitySkip.getOpcode());
            before(cast, identities.get(index), materialize.get(index));
            before(cast, materialize.get(index), identitySkip.label);
            assertEquals("attrExpressionMap", ((FieldInsnNode) previous(empty.get(index))).name);
            JumpInsnNode noAttr = (JumpInsnNode) next(empty.get(index));
            assertEquals(IFNE, noAttr.getOpcode());
            assertSame(next(next(noAttr)), materialize.get(index), "有 attr 才能立即访问延迟 Map");
            assertEquals(lazySlot, variable(previous(materialize.get(index)), ALOAD));
            assertEquals(POP, next(materialize.get(index)).getOpcode());
            assertSame(next(noAttr.label), next(next(materialize.get(index))));
        }
        before(cast, materialize.get(0), call(cast, API, "addSourceAttribute"));
        before(cast, call(cast, CENTRAL, "getAttributes"), materialize.get(1));
        before(cast, materialize.get(1), calls(cast, DATA, "<init>").get(1));
    }

    @Test
    void configuredCastCreatesLazyContextOnceAndUnconfiguredSkipsIt() throws IOException {
        ClassNode type = read(ADAPTER);
        MethodNode cast = method(type, "castAtEntity");
        MethodInsnNode configured = call(cast, MULTIPLIER, "isConfigured");
        MethodInsnNode context = call(cast, MULTIPLIER, "lazyContext");
        assertTrue(calls(cast, ADAPTER, "parseExpressionContext").isEmpty(), "入口不能直接读取完整变量上下文");
        InvokeDynamicInsnNode supplierBinding = (InvokeDynamicInsnNode) previous(context);
        assertEquals(
                "Ljava/util/function/Supplier;",
                Type.getReturnType(supplierBinding.desc).getDescriptor());
        MethodNode supplier = method(type, ((Handle) supplierBinding.bsmArgs[1]).getName());
        assertEquals(Type.getType(Map.class), Type.getReturnType(supplier.desc));
        MethodInsnNode parse = call(supplier, ADAPTER, "parseExpressionContext");
        before(supplier, call(supplier, ROOT + "configuration/MainConfiguration", "getVariables"), parse);
        assertEquals(ARETURN, next(parse).getOpcode(), "Supplier 原样调用既有上下文规则");
        MethodInsnNode evaluate = call(cast, MULTIPLIER, "evaluate");
        JumpInsnNode noConfiguration = (JumpInsnNode) next(configured);
        assertEquals(IFEQ, noConfiguration.getOpcode());
        before(cast, context, noConfiguration.label);
        assertEquals(ACONST_NULL, next(noConfiguration.label).getOpcode());
        before(cast, context, evaluate);
        int contextSlot = variable(next(next(noConfiguration.label)), ASTORE);
        assertEquals(contextSlot, variable(previous(evaluate), ALOAD));
        assertTrue(cast.tryCatchBlocks.isEmpty(), "非法倍率不得降级吞错");
        MethodNode attr = containing(type, HANDLE, "updateTempAttributeValue");
        MethodInsnNode fallback = call(attr, ADAPTER, "parseExpressionContext");
        JumpInsnNode absent = (JumpInsnNode) code(attr).get(2);
        assertEquals(IFNULL, absent.getOpcode());
        before(attr, absent.label, fallback);
        JumpInsnNode reuse = (JumpInsnNode) code(attr).get(4);
        before(attr, fallback, reuse.label);
        assertSame(call(attr, "team/idealstate/sugar/next/calculate/Expression", "calculate"), next(reuse.label));
    }
}
