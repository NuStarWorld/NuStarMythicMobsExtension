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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import top.nustar.nustarmythicmobsextension.exception.NSMMEException;

@DisplayName("FastAP 倍率配置：缺省、真实表达式与规范属性名")
class FastAPMultiplierConfigTest {
    @ParameterizedTest
    @NullAndEmptySource
    void absentGlobalAndEmptyListKeepUnconfiguredPath(String list) {
        FastAPBaseMultiplier config = new FastAPBaseMultiplier(null, list, name -> {
            throw new AssertionError("空列表不得读取 AP 名称映射");
        });
        assertFalse(config.isConfigured());
        // Adapter 不对未配置的 helper 调用 evaluate；默认 1 由旧路径保持。
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "1.0", "2/2"})
    void explicitIdentityStillEvaluates(String global) {
        FastAPBaseMultiplier config = new FastAPBaseMultiplier(global, "", name -> name);
        assertTrue(config.isConfigured());
        assertTrue(config.evaluate(Collections.emptyMap()).isIdentity(Collections.singleton("攻击力")));
    }

    @Test
    void listOnlyUsesGlobalOneAndTrimsNames() {
        Map<String, String> registered = new HashMap<>();
        registered.put("attack", "攻击力");
        registered.put("攻击力", "攻击力");
        registered.put("CustomPower", "自定义强度");
        FastAPBaseMultiplier.Values values = new FastAPBaseMultiplier(null, " attack :2,CustomPower:3", registered::get)
                .evaluate(Collections.emptyMap());
        assertArrayEquals(new Number[] {20.0, 40.0}, values.scale("攻击力", new Number[] {10, 20}));
        assertArrayEquals(new Number[] {30.0, 60.0}, values.scale("自定义强度", new Number[] {10, 20}));
        assertArrayEquals(new Number[] {10.0, 20.0}, values.scale("防御力", new Number[] {10, 20}));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                " ",
                ":2",
                " :2",
                "attack:",
                "attack: ",
                "attack",
                "attack:2:3",
                ",attack:2",
                "attack:2,",
                "attack:2,,defense:3"
            })
    void malformedListIsNotTreatedAsAbsent(String list) {
        NSMMEException failure =
                assertThrows(NSMMEException.class, () -> new FastAPBaseMultiplier(null, list, name -> name));
        assertTrue(failure.getMessage().contains("baseAttributeMultipleList(baml)"), failure.getMessage());
        assertTrue(failure.getMessage().contains("格式错误"), failure.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"missing:2", "不存在属性:2", "Attack:2"})
    void unknownNamesAreNotSilentlyAccepted(String list) {
        NSMMEException failure =
                assertThrows(NSMMEException.class, () -> new FastAPBaseMultiplier(null, list, name -> null));
        assertTrue(failure.getMessage().contains("baml"), failure.getMessage());
        assertTrue(failure.getMessage().contains(list.substring(0, list.indexOf(':'))), failure.getMessage());
        assertTrue(failure.getMessage().contains("未知属性"), failure.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"attack:2,攻击力:3", "攻击力:2,attack:3", "attack:2, attack :3"})
    void duplicateNormalizedChineseAndEnglishNamesFail(String list) {
        Map<String, String> registered = new HashMap<>();
        registered.put("attack", "攻击力");
        registered.put("攻击力", "攻击力");
        NSMMEException failure =
                assertThrows(NSMMEException.class, () -> new FastAPBaseMultiplier(null, list, registered::get));
        assertTrue(failure.getMessage().contains("baml"), failure.getMessage());
        assertTrue(failure.getMessage().contains("归一化后重复"), failure.getMessage());
        assertTrue(failure.getMessage().contains("攻击力"), failure.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    void explicitlyEmptyGlobalDoesNotFallBackToOne(String global) {
        NSMMEException failure =
                assertThrows(NSMMEException.class, () -> new FastAPBaseMultiplier(global, null, name -> name));
        assertTrue(failure.getMessage().contains("baseAttributeMultiple(bam)"), failure.getMessage());
        assertTrue(failure.getMessage().contains("不能为空"), failure.getMessage());
    }
}
