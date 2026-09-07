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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

/** 只读取编译产物，不加载 FastAPAdapter、AP 或 NMS；不是字节码执行器。 */
final class FastAPBytecode {
    static final String ROOT = "top/nustar/nustarmythicmobsextension/";
    static final String ADAPTER = ROOT + "adapter/impl/skills/mechanics/FastAPAdapter";
    static final String MULTIPLIER = ROOT + "adapter/impl/skills/mechanics/FastAPBaseMultiplier";
    static final String API = "org/serverct/ersha/api/AttributeAPI";
    static final String DATA = "org/serverct/ersha/attribute/data/AttributeData";
    static final String CENTRAL = "org/serverct/ersha/attribute/data/AttributeCentral";
    static final String HANDLE = "org/serverct/ersha/attribute/AttributeHandle";

    private FastAPBytecode() {}

    static ClassNode read(String name) throws IOException {
        try (InputStream input = FastAPBytecode.class.getClassLoader().getResourceAsStream(name + ".class")) {
            assertNotNull(input, "缺少编译产物：" + name);
            ClassNode type = new ClassNode();
            new ClassReader(input).accept(type, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return type;
        }
    }

    static MethodNode method(ClassNode type, String name) {
        return type.methods.stream()
                .filter(method -> name.equals(method.name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("缺少方法：" + name));
    }

    static List<AbstractInsnNode> code(MethodNode method) {
        return Arrays.stream(method.instructions.toArray())
                .filter(node -> node.getOpcode() >= 0)
                .collect(Collectors.toList());
    }

    static List<MethodInsnNode> calls(MethodNode method, String owner, String name) {
        return code(method).stream()
                .filter(node -> node instanceof MethodInsnNode)
                .map(node -> (MethodInsnNode) node)
                .filter(call -> owner.equals(call.owner) && name.equals(call.name))
                .collect(Collectors.toList());
    }

    static MethodInsnNode call(MethodNode method, String owner, String name) {
        List<MethodInsnNode> result = calls(method, owner, name);
        assertEquals(1, result.size(), "应只有一个调用位置：" + owner + "." + name);
        return result.get(0);
    }

    static MethodNode containing(ClassNode type, String owner, String name) {
        List<MethodNode> methods = type.methods.stream()
                .filter(method -> !calls(method, owner, name).isEmpty())
                .collect(Collectors.toList());
        assertEquals(1, methods.size(), "应只有一个包含方法：" + name);
        return methods.get(0);
    }

    static AbstractInsnNode next(AbstractInsnNode node) {
        do {
            node = node.getNext();
        } while (node != null && node.getOpcode() < 0);
        return node;
    }

    static AbstractInsnNode previous(AbstractInsnNode node) {
        do {
            node = node.getPrevious();
        } while (node != null && node.getOpcode() < 0);
        return node;
    }

    static int variable(AbstractInsnNode node, int opcode) {
        assertEquals(opcode, node.getOpcode());
        assertInstanceOf(VarInsnNode.class, node);
        return ((VarInsnNode) node).var;
    }

    static void before(MethodNode method, AbstractInsnNode first, AbstractInsnNode second) {
        assertTrue(method.instructions.indexOf(first) < method.instructions.indexOf(second), "调用顺序不得颠倒");
    }
}
