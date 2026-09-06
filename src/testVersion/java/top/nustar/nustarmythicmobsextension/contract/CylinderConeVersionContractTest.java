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

package top.nustar.nustarmythicmobsextension.contract;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/** 仅检查当前模块字节码，不加载 MythicMobs 单例，也不冒充原生占位符集成测试。 */
@DisplayName("三版圆柱扇形二进制接入契约")
class CylinderConeVersionContractTest {
    private static final String ROOT = "top/nustar/nustarmythicmobsextension/";
    private static final String COMMON = ROOT + "adapter/impl/targets/";
    private final String version = System.getProperty("cylinder.version");
    private final boolean legacy = "4_9_0".equals(version);
    private final String selector =
            ROOT + "adapter/impl/" + (legacy ? "mm_" : "mm") + version + "/targets/CylinderConeSelector";
    private final String metadata =
            legacy ? "io/lumine/xikage/mythicmobs/skills/SkillMetadata" : "io/lumine/mythic/api/skills/SkillMetadata";
    private final String lineConfig =
            legacy ? "io/lumine/xikage/mythicmobs/io/MythicLineConfig" : "io/lumine/mythic/api/config/MythicLineConfig";
    private final String executor = "io/lumine/mythic/core/skills/SkillExecutor";
    private final String constructor = legacy ? "(L" + lineConfig + ";)V" : "(L" + executor + ";L" + lineConfig + ";)V";

    @Test
    @DisplayName("getEntities 第一条有效指令必须先检查线程，然后每次只求值一次")
    void threadGuardPrecedesAllReads() throws IOException {
        MethodNode method = method(read(selector), "getEntities");
        List<AbstractInsnNode> code = instructions(method);
        assertEquals(Opcodes.INVOKESTATIC, code.get(0).getOpcode());
        MethodInsnNode guard = (MethodInsnNode) code.get(0);
        assertEquals(COMMON + "CylinderConeSelectorAdapter", guard.owner);
        assertEquals("requirePrimaryThread", guard.name);
        assertEquals("()V", guard.desc);
        List<MethodInsnNode> calls = calls(method);
        assertEquals("evaluate", calls.get(1).name);
        assertEquals(COMMON + "CylinderConeConfig", calls.get(1).owner);
        assertEquals("getCaster", calls.get(2).name);
        assertEquals(
                1, calls.stream().filter(call -> "evaluate".equals(call.name)).count());
        assertEquals(
                1, calls.stream().filter(call -> "select".equals(call.name)).count());
        assertEquals("select", calls.get(calls.size() - 1).name);
        assertTrue(method.tryCatchBlocks.isEmpty(), "不得把异常转为空集");
        assertTrue(code.stream().anyMatch(node -> node.getOpcode() == Opcodes.IFNULL), "origin为空保留施法者中心");
    }

    @Test
    @DisplayName("构造器分别为 4.x config 与 5.x executor+config 并透传父构造")
    void nativeConstructorSignature() throws IOException {
        ClassNode type = read(selector);
        assertTrue(type.interfaces.contains(ROOT + "adapter/impl/NuStarTargerSelector"));
        MethodNode ctor = method(type, "<init>");
        assertEquals(constructor, ctor.desc);
        MethodInsnNode parent = calls(ctor).get(0);
        assertEquals(type.superName, parent.owner);
        assertEquals("<init>", parent.name);
        assertEquals(constructor, parent.desc);
    }

    @Test
    @DisplayName("helper 组件、版本注解、类型与原生参数位置完全匹配")
    void helperRegistrationAndArguments() throws IOException {
        ClassNode helper = read(selector.replace("/CylinderConeSelector", "/helper/CylinderConeSelectorHelper"));
        List<String> annotations = Stream.concat(
                        helper.visibleAnnotations == null
                                ? Stream.<AnnotationNode>empty()
                                : helper.visibleAnnotations.stream(),
                        helper.invisibleAnnotations == null
                                ? Stream.<AnnotationNode>empty()
                                : helper.invisibleAnnotations.stream())
                .map(annotation -> annotation.desc)
                .collect(Collectors.toList());
        assertTrue(annotations.contains("Lteam/idealstate/sugar/next/context/annotation/component/Component;"));
        assertTrue(annotations.contains("L" + ROOT + "service/annotations/MythicMobs" + version + ";"));
        FieldInsnNode type =
                (FieldInsnNode) instructions(method(helper, "getType")).get(0);
        assertEquals(ROOT + "service/enums/TargetSelectorType", type.owner);
        assertEquals("CYLINDER_CONE", type.name);
        MethodNode finder = method(helper, "findSelector");
        List<MethodInsnNode> calls = calls(finder);
        assertEquals(1, calls.size());
        assertEquals(selector, calls.get(0).owner);
        assertEquals("<init>", calls.get(0).name);
        assertEquals(constructor, calls.get(0).desc);
        List<AbstractInsnNode> code = instructions(finder);
        List<String> casts = code.stream()
                .filter(node -> node.getOpcode() == Opcodes.CHECKCAST)
                .map(node -> ((TypeInsnNode) node).desc)
                .collect(Collectors.toList());
        assertEquals(legacy ? Arrays.asList(lineConfig) : Arrays.asList(executor, lineConfig), casts);
        List<Integer> indices = code.stream()
                .filter(node -> node.getOpcode() == Opcodes.ICONST_0 || node.getOpcode() == Opcodes.ICONST_1)
                .map(AbstractInsnNode::getOpcode)
                .collect(Collectors.toList());
        assertEquals(
                legacy ? Arrays.asList(Opcodes.ICONST_0) : Arrays.asList(Opcodes.ICONST_0, Opcodes.ICONST_1), indices);
    }

    @Test
    @DisplayName("原生 PlaceholderString.get 仅接收 metadata，不接收候选实体")
    void nativePlaceholderUsesMetadataOnly() throws IOException {
        List<InvokeDynamicInsnNode> bindings = read(selector).methods.stream()
                .flatMap(method -> instructions(method).stream())
                .filter(node -> node instanceof InvokeDynamicInsnNode)
                .map(node -> (InvokeDynamicInsnNode) node)
                .filter(node -> Arrays.stream(node.bsmArgs)
                        .filter(argument -> argument instanceof Handle)
                        .map(argument -> (Handle) argument)
                        .anyMatch(handle ->
                                handle.getOwner().endsWith("/PlaceholderString") && "get".equals(handle.getName())))
                .collect(Collectors.toList());
        assertEquals(1, bindings.size());
        InvokeDynamicInsnNode binding = bindings.get(0);
        Handle target = (Handle) binding.bsmArgs[1];
        String placeholderMeta = legacy
                ? "io/lumine/xikage/mythicmobs/skills/placeholders/PlaceholderMeta"
                : "io/lumine/mythic/core/skills/placeholders/PlaceholderMeta";
        assertEquals("(L" + placeholderMeta + ";)Ljava/lang/String;", target.getDesc());
        assertEquals("(L" + metadata + ";)Ljava/lang/String;", binding.bsmArgs[2].toString());
        assertFalse(read(selector).methods.stream()
                .flatMap(method -> calls(method).stream())
                .anyMatch(call -> call.owner.contains("PlaceholderDouble")));
    }

    private static ClassNode read(String name) throws IOException {
        try (InputStream input =
                CylinderConeVersionContractTest.class.getClassLoader().getResourceAsStream(name + ".class")) {
            assertNotNull(input, "当前模块必须提供目标字节码：" + name);
            ClassNode type = new ClassNode();
            new ClassReader(input).accept(type, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return type;
        }
    }

    private static MethodNode method(ClassNode type, String name) {
        return type.methods.stream()
                .filter(method -> name.equals(method.name) && (method.access & Opcodes.ACC_BRIDGE) == 0)
                .findFirst()
                .orElseThrow(() -> new AssertionError("缺少方法：" + name));
    }

    private static List<AbstractInsnNode> instructions(MethodNode method) {
        return Arrays.stream(method.instructions.toArray())
                .filter(node -> node.getOpcode() >= 0)
                .collect(Collectors.toList());
    }

    private static List<MethodInsnNode> calls(MethodNode method) {
        return instructions(method).stream()
                .filter(node -> node instanceof MethodInsnNode)
                .map(node -> (MethodInsnNode) node)
                .collect(Collectors.toList());
    }
}
