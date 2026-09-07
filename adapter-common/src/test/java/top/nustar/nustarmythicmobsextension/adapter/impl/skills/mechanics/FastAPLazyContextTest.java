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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import team.idealstate.sugar.next.calculate.Expression;
import top.nustar.nustarmythicmobsextension.exception.NSMMEException;

@DisplayName("FastAP 生产延迟 Map 与真实 Expression（不模拟 PAPI/服务器）")
class FastAPLazyContextTest {
    @ParameterizedTest
    @CsvSource(
            value = {"1|", "2/2|", "2|attack:0.5,defense:0.5", "|attack:1,defense:1"},
            delimiter = '|')
    void constantIdentityNeverReadsSupplierAndPreservesLegacyAttrSequence(String global, String list) {
        AtomicInteger reads = new AtomicInteger();
        Supplier<Map<String, Number>> supplier = () -> Collections.singletonMap("n", reads.incrementAndGet());
        Map<String, Number> lazy = FastAPBaseMultiplier.lazyContext(supplier);
        FastAPBaseMultiplier.Values values = new FastAPBaseMultiplier(global, list, name -> name).evaluate(lazy);
        assertTrue(values.isIdentity(Arrays.asList("attack", "defense")));
        assertEquals(0, reads.get(), "判断常量恒等不能消费一次动态上下文");
        Expression first = new Expression("n").compile();
        Expression second = new Expression("n+10").compile();
        // identity 的旧逐 attr 路径使用独立上下文；Adapter 实际选择由 ASM 数据流契约绑定。
        Number[] identity = {first.calculate(supplier.get()), second.calculate(supplier.get())};
        assertEquals(2, reads.get());
        reads.set(0);
        Number[] unconfigured = {first.calculate(supplier.get()), second.calculate(supplier.get())};
        assertArrayEquals(unconfigured, identity);
        assertEquals(1, identity[0].intValue());
        assertEquals(12, identity[1].intValue());
        assertEquals(2, reads.get());
    }

    @Test
    void allVariableExpressionsShareTheFirstSupplierResultAndAreNotRecalculated() {
        AtomicInteger suppliers = new AtomicInteger();
        Map<Object, Integer> gets = new HashMap<>();
        Map<String, Number> backing = new HashMap<String, Number>() {
            @Override
            public Number get(Object key) {
                gets.merge(key, 1, Integer::sum);
                return super.get(key);
            }
        };
        backing.put("global", 2.0);
        backing.put("local", 3.0);
        backing.put("other", 4.0);
        Map<String, Number> lazy = FastAPBaseMultiplier.lazyContext(() -> {
            suppliers.incrementAndGet();
            return backing;
        });
        assertEquals(0, suppliers.get());
        FastAPBaseMultiplier.Values values =
                new FastAPBaseMultiplier("global", "attack:local,defense:other", name -> name).evaluate(lazy);
        assertEquals(1, suppliers.get());
        assertEquals(3, gets.size());
        gets.values().forEach(count -> assertEquals(1, count.intValue()));
        values.isIdentity(Arrays.asList("attack", "defense"));
        assertArrayEquals(new Number[] {6.0, 12.0}, values.scale("attack", new Number[] {1, 2}));
        gets.values().forEach(count -> assertEquals(1, count.intValue()));
        assertEquals(
                5.0, new Expression("global+local").compile().calculate(lazy).doubleValue());
        lazy.entrySet();
        assertEquals(1, suppliers.get());
    }

    @Test
    void dynamicIdentityNecessarilyReadsOnceButIdentityAndScaleNeverRecalculate() {
        AtomicInteger reads = new AtomicInteger();
        AtomicInteger gets = new AtomicInteger();
        Map<String, Number> backing = new HashMap<String, Number>() {
            @Override
            public Number get(Object key) {
                gets.incrementAndGet();
                return super.get(key);
            }
        };
        Map<String, Number> lazy = FastAPBaseMultiplier.lazyContext(() -> {
            backing.put("n", reads.incrementAndGet());
            return backing;
        });
        FastAPBaseMultiplier.Values values = new FastAPBaseMultiplier("n", "attack:1", name -> name).evaluate(lazy);
        assertEquals(1, reads.get(), "动态恒等仍需读取上下文才能判断");
        assertTrue(values.isIdentity(Collections.singleton("attack")));
        assertTrue(values.isIdentity(Collections.singleton("defense")));
        assertArrayEquals(new Number[] {10.0, 20.0}, values.scale("attack", new Number[] {10, 20}));
        assertEquals(1, reads.get());
        assertEquals(1, gets.get(), "缓存上下文不等于允许重新求值表达式");
    }

    @ParameterizedTest
    @ValueSource(ints = {2, -1})
    void zeroGlobalStillReadsAndValidatesListVariable(int local) {
        AtomicInteger reads = new AtomicInteger();
        Map<String, Number> lazy = FastAPBaseMultiplier.lazyContext(() -> {
            reads.incrementAndGet();
            return Collections.singletonMap("n", local);
        });
        FastAPBaseMultiplier config = new FastAPBaseMultiplier("0", "attack:n", name -> name);
        if (local < 0) {
            NSMMEException failure = assertThrows(NSMMEException.class, () -> config.evaluate(lazy));
            assertTrue(failure.getMessage().contains("baml)[attack]"));
        } else {
            assertArrayEquals(new Number[] {0.0, 0.0}, config.evaluate(lazy).scale("attack", new Number[] {10, 20}));
        }
        assertEquals(1, reads.get());
    }

    @Test
    void entrySetFixesOneSnapshotForLaterAttrWithoutRereading() {
        AtomicInteger stageValue = new AtomicInteger(10);
        AtomicInteger reads = new AtomicInteger();
        Map<String, Number> lazy = FastAPBaseMultiplier.lazyContext(() -> {
            reads.incrementAndGet();
            return Collections.singletonMap("target_hp", stageValue.get());
        });
        FastAPBaseMultiplier.Values values = new FastAPBaseMultiplier("2", null, name -> name).evaluate(lazy);
        assertFalse(values.isIdentity(Collections.singleton("attack")));
        assertEquals(0, reads.get());
        lazy.entrySet();
        assertEquals(1, reads.get());
        stageValue.set(99);
        assertEquals(10, new Expression("target_hp").compile().calculate(lazy).intValue());
        lazy.entrySet();
        assertEquals(10, lazy.get("target_hp").intValue());
        assertEquals(1, reads.get());
    }

    @Test
    void constantNonIdentityWithoutAttrDoesNotNeedVariables() {
        Map<String, Number> lazy = FastAPBaseMultiplier.lazyContext(() -> {
            throw new AssertionError("常量倍率且没有 attr 不应读取上下文");
        });
        FastAPBaseMultiplier.Values values = new FastAPBaseMultiplier("2", null, name -> name).evaluate(lazy);
        assertFalse(values.isIdentity(Collections.singleton("attack")));
        assertArrayEquals(new Number[] {20.0, 40.0}, values.scale("attack", new Number[] {10, 20}));
    }
}
