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

package top.nustar.nustarmythicmobsextension.adapter.impl.targets;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("圆柱扇形非法配置不得静默退回 1 或空集")
class CylinderConeConfigInvalidTest {
    @ParameterizedTest(name = "非法展开值 [{0}] 保留参数诊断")
    @NullAndEmptySource
    @ValueSource(
            strings = {
                "abc",
                "10junk",
                "<caster.var.missing>",
                "1+2",
                "1to10",
                "1-10",
                "random(1,10)",
                "NaN",
                "Infinity",
                "-Infinity",
                "1e309"
            })
    void invalidExpandedNumber(String expanded) {
        CylinderConeConfig<Object> config = new CylinderConeConfig<>(
                (key, fallback) -> "r".equals(key) ? "<caster.var.radius>" : fallback,
                raw -> metadata -> "<caster.var.radius>".equals(raw) ? expanded : raw);
        assertDiagnostic(
                assertThrows(IllegalStateException.class, () -> config.evaluate(null)),
                "r",
                "<caster.var.radius>",
                String.valueOf(expanded));
    }

    @Test
    @DisplayName("空短名也优先于合法长名，不当作缺省值")
    void emptyShortNameDoesNotFallBack() {
        Map<String, String> values = new HashMap<>();
        values.put("r", "");
        values.put("range", "10");
        assertDiagnostic(
                assertThrows(IllegalStateException.class, () -> literal(values).evaluate(null)), "r", "", "");
    }

    @ParameterizedTest(name = "非法尺寸或半角 {0}={1}")
    @CsvSource({
        "r,-1",
        "a,-1",
        "a,180.000001",
        "down,-1",
        "up,-1",
        "padding,-1",
        "close,-1",
        "r,NaN",
        "a,Infinity",
        "rot,NaN",
        "rot,Infinity",
        "down,1e309",
        "up,Infinity",
        "padding,NaN",
        "close,Infinity"
    })
    void invalidParameterRanges(String key, String value) {
        Map<String, String> values = new HashMap<>();
        values.put(key, value);
        assertDiagnostic(
                assertThrows(IllegalStateException.class, () -> literal(values).evaluate(null)), key, value, value);
    }

    @ParameterizedTest(name = "{0}+padding 溢出仍是显式失败")
    @ValueSource(strings = {"r", "down", "up"})
    void derivedOverflow(String key) {
        Map<String, String> values = new HashMap<>();
        values.put(key, "1.7e308");
        values.put("padding", "1.7e308");
        assertDiagnostic(
                assertThrows(IllegalStateException.class, () -> literal(values).evaluate(null)),
                key + "+padding",
                "1.7e308 + 1.7e308",
                "1.7e308 + 1.7e308");
    }

    @ParameterizedTest(name = "创建失败 {0} 不阻断注册且不重试")
    @ValueSource(booleans = {true, false})
    void factoryFailureKeepsCause(boolean substringFailure) {
        RuntimeException cause = substringFailure
                ? new StringIndexOutOfBoundsException("eval substring(5)")
                : new IllegalArgumentException("解析器拒绝");
        AtomicInteger factories = new AtomicInteger();
        AtomicInteger failedFactories = new AtomicInteger();
        CylinderConeConfig<Object> config = assertDoesNotThrow(
                () -> new CylinderConeConfig<>((key, fallback) -> "r".equals(key) ? "eval" : fallback, raw -> {
                    factories.incrementAndGet();
                    if ("eval".equals(raw)) {
                        failedFactories.incrementAndGet();
                        throw cause;
                    }
                    return metadata -> raw;
                }));
        assertEquals(7, factories.get());
        assertEquals(1, failedFactories.get());
        for (int invocation = 0; invocation < 2; invocation++) {
            IllegalStateException error = assertThrows(IllegalStateException.class, () -> config.evaluate(null));
            assertSame(cause, error.getCause());
            assertDiagnostic(error, "r", "eval", "<展开失败>");
            assertTrue(error.getMessage().contains("占位符创建失败"));
        }
        assertEquals(7, factories.get());
        assertEquals(1, failedFactories.get());
    }

    @Test
    @DisplayName("占位符运行异常转换为 IllegalStateException 并保留原因")
    void expansionFailureKeepsCause() {
        IllegalArgumentException cause = new IllegalArgumentException("元数据求值失败");
        CylinderConeConfig<Object> config = new CylinderConeConfig<>((key, fallback) -> fallback, raw -> metadata -> {
            throw cause;
        });
        IllegalStateException error = assertThrows(IllegalStateException.class, () -> config.evaluate(null));
        assertSame(cause, error.getCause());
        assertDiagnostic(error, "r", "10", "<展开失败>");
    }

    private static CylinderConeConfig<Object> literal(Map<String, String> values) {
        return new CylinderConeConfig<>(values::getOrDefault, raw -> metadata -> raw);
    }

    private static void assertDiagnostic(IllegalStateException error, String parameter, String raw, String expanded) {
        assertEquals(IllegalStateException.class, error.getClass());
        assertTrue(error.getMessage().contains("参数 " + parameter + ":"), error.getMessage());
        assertTrue(error.getMessage().contains("raw=[" + raw + "]"), error.getMessage());
        assertTrue(error.getMessage().contains("expanded=[" + expanded + "]"), error.getMessage());
    }
}
