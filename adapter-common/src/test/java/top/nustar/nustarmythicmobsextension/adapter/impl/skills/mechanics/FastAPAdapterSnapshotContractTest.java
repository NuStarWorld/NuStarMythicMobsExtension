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
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.*;

@DisplayName("FastAP clear 后快照二进制契约（百分比/cap 仅结构证据）")
class FastAPAdapterSnapshotContractTest {
    @Test
    void clearSelectsWhitelistBasisAndDoesNotRestoreOriginalCasterForScaling() throws IOException {
        MethodNode cast = method(read(ADAPTER), "castAtEntity");
        List<AbstractInsnNode> code = code(cast);
        int original = variable(next(calls(cast, API, "getAttrData").get(0)), ASTORE);
        MethodInsnNode read = call(cast, DATA, "getAttributeValue");
        int basis = variable(previous(previous(read)), ALOAD);
        MethodInsnNode clearCtor = calls(cast, DATA, "<init>").get(0);
        assertEquals(basis, variable(next(clearCtor), ASTORE));
        MethodInsnNode whitelist = call(cast, ROOT + "utils/AttributeUtils", "getWhiteAttributeList");
        MethodInsnNode add = call(cast, API, "addSourceAttribute");
        assertEquals(original, variable(code.get(code.indexOf(whitelist) - 4), ALOAD));
        assertEquals(basis, variable(code.get(code.indexOf(whitelist) - 6), ALOAD));
        JumpInsnNode clearDone = (JumpInsnNode) next(add);
        assertEquals(GOTO, clearDone.getOpcode());
        assertEquals(original, variable(next(clearDone), ALOAD));
        assertEquals(basis, variable(next(next(clearDone)), ASTORE));
        before(cast, add, read);
        // clear 分支已汇合后，不允许重新从 casterAttrData 读基础或源。
        assertFalse(code.subList(code.indexOf(next(clearDone.label)), code.indexOf(read)).stream()
                .anyMatch(node -> node instanceof VarInsnNode
                        && node.getOpcode() == ALOAD
                        && ((VarInsnNode) node).var == original));
        MethodNode white = method(read(ROOT + "utils/AttributeUtils"), "getWhiteAttributeList");
        MethodInsnNode whiteRead = call(white, DATA, "getAttributeValue");
        assertEquals(ICONST_0, next(whiteRead).getOpcode(), "保留旧白名单取低端值语义");
        assertEquals(AALOAD, next(next(whiteRead)).getOpcode());
    }

    @Test
    void registeredNamesAreCopiedAndUnionedWithPostClearSourceNames() throws IOException {
        MethodNode cast = method(read(ADAPTER), "castAtEntity");
        MethodInsnNode all = call(cast, API, "allServerKey");
        assertSame(call(cast, "java/util/LinkedHashSet", "<init>"), next(all));
        MethodInsnNode sources = call(cast, CENTRAL, "getAttributeSources");
        assertEquals(ICONST_0, previous(sources).getOpcode());
        assertEquals(ACONST_NULL, previous(previous(sources)).getOpcode());
        MethodInsnNode names = call(cast, CENTRAL, "getAttributes");
        before(cast, call(cast, API, "addSourceAttribute"), sources);
        before(cast, sources, names);
        assertSame(call(cast, "java/util/Set", "addAll"), next(names));
        before(cast, names, call(cast, DATA, "getAttributeValue"));
        assertTrue(calls(cast, CENTRAL, "getAttributeValues").isEmpty(), "不得用漏 force-only 的名字枚举");
    }

    @Test
    void rangeIsReadOnceThenScaledAndForcedOnlyIntoNewData() throws IOException {
        MethodNode cast = method(read(ADAPTER), "castAtEntity");
        List<AbstractInsnNode> code = code(cast);
        MethodInsnNode read = call(cast, DATA, "getAttributeValue");
        MethodInsnNode scale = call(cast, MULTIPLIER + "$Values", "scale");
        assertSame(scale, next(read));
        assertEquals("(Ljava/lang/String;)[Ljava/lang/Number;", read.desc);
        int basis = variable(previous(previous(read)), ALOAD);
        int scaledData = variable(next(calls(cast, DATA, "<init>").get(1)), ASTORE);
        assertNotEquals(basis, scaledData);
        int scaledArray = variable(next(scale), ASTORE);
        MethodInsnNode force = call(cast, CENTRAL, "setForceAttributeValue");
        int end = code.indexOf(force);
        assertEquals(scaledData, variable(code.get(end - 11), ALOAD));
        assertEquals("getCentral", ((MethodInsnNode) code.get(end - 10)).name);
        assertEquals(scaledArray, variable(code.get(end - 8), ALOAD));
        assertEquals(scaledArray, variable(code.get(end - 4), ALOAD));
        assertEquals(ICONST_0, code.get(end - 7).getOpcode());
        assertEquals(ICONST_1, code.get(end - 3).getOpcode());
        assertEquals("(Ljava/lang/String;DD)V", force.desc);
        before(cast, scale, force);
        assertFalse(code.stream().anyMatch(node -> node.getOpcode() == AASTORE), "不就地写 AP 返回数组");
    }

    @Test
    void identitySkipsNewSnapshotAndNonIdentityReplacesOnlyAttackLocal() throws IOException {
        MethodNode cast = method(read(ADAPTER), "castAtEntity");
        MethodInsnNode identity =
                calls(cast, MULTIPLIER + "$Values", "isIdentity").get(1);
        JumpInsnNode skip = (JumpInsnNode) next(identity);
        assertEquals(IFNE, skip.getOpcode());
        TypeInsnNode handleNew = (TypeInsnNode) next(skip.label);
        assertEquals(NEW, handleNew.getOpcode());
        assertEquals(HANDLE, handleNew.desc);
        MethodInsnNode snapshot = calls(cast, DATA, "<init>").get(1);
        before(cast, identity, snapshot);
        before(cast, snapshot, skip.label);
        int newSlot = variable(next(snapshot), ASTORE);
        AbstractInsnNode markScaled = previous(handleNew);
        assertEquals(ISTORE, markScaled.getOpcode());
        assertEquals(ICONST_1, previous(markScaled).getOpcode());
        AbstractInsnNode replaceAttack = previous(previous(markScaled));
        assertEquals(newSlot, variable(previous(replaceAttack), ALOAD));
        int basis = variable(previous(previous(call(cast, DATA, "getAttributeValue"))), ALOAD);
        assertEquals(basis, variable(replaceAttack, ASTORE));
    }

    @Test
    void snapshotDoesNotRefreshReapplyPercentageOrRebuildOrdinarySources() throws IOException {
        MethodNode cast = method(read(ADAPTER), "castAtEntity");
        MethodInsnNode snapshot = calls(cast, DATA, "<init>").get(1);
        MethodInsnNode handle = call(cast, HANDLE, "<init>");
        List<AbstractInsnNode> code = code(cast);
        List<String> allowed = Arrays.asList(
                "<init>",
                "setSourceEntity",
                "getLastAttackMillis",
                "setLastAttackMillis",
                "getLastDefenseMillis",
                "setLastDefenseMillis",
                "getAttributeValue",
                "getCentral",
                "setForceAttributeValue");
        for (AbstractInsnNode node : code.subList(code.indexOf(snapshot), code.indexOf(handle))) {
            if (!(node instanceof MethodInsnNode)) continue;
            MethodInsnNode method = (MethodInsnNode) node;
            if (method.owner.equals(DATA) || method.owner.equals(CENTRAL)) {
                assertTrue(allowed.contains(method.name), "快照不得增加 AP 刷新/来源/系数调用：" + method.name);
            }
            assertFalse(method.owner.equals(API) && method.name.equals("addSourceAttribute"));
        }
        assertTrue(calls(cast, DATA, "getRandomValue").isEmpty());
        assertEquals(1, calls(cast, DATA, "getLastAttackMillis").size());
        assertEquals(1, calls(cast, DATA, "getLastDefenseMillis").size());
        // 已物化百分比、force 跳过二次 Central cap 的真实 AP 语义由独立 API 审计提供。
    }
}
