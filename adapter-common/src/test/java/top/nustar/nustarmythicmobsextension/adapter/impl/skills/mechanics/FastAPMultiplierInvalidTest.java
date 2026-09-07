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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import team.idealstate.sugar.next.calculate.Expression;
import top.nustar.nustarmythicmobsextension.exception.NSMMEException;

@DisplayName("FastAP 非法倍率、非有限乘积与真实引擎 cause")
class FastAPMultiplierInvalidTest {
    @ParameterizedTest
    @ValueSource(
            strings = {"-1", "1.0/0.0", "0.0/0.0", "huge*huge", "nan", "inf", "negativeInf", "9223372036854775807+1"})
    void globalAndLocalRejectNegativeOrNonFiniteResults(String expression) {
        java.util.Map<String, Number> context = new java.util.HashMap<>();
        context.put("huge", Double.MAX_VALUE);
        context.put("nan", Double.NaN);
        context.put("inf", Double.POSITIVE_INFINITY);
        context.put("negativeInf", Double.NEGATIVE_INFINITY);
        NSMMEException global = assertThrows(
                NSMMEException.class, () -> new FastAPBaseMultiplier(expression, null, name -> name).evaluate(context));
        diagnostic(global, "baseAttributeMultiple(bam)", "非负有限数");
        NSMMEException local = assertThrows(
                NSMMEException.class,
                () -> new FastAPBaseMultiplier("1", "attack:" + expression, name -> name).evaluate(context));
        diagnostic(local, "baseAttributeMultipleList(baml)[attack]", "非负有限数");
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0.0/0.0", "1.0/0.0", "1/0", "missing"})
    void globalZeroStillValidatesEvenInapplicableListEntries(String invalid) {
        FastAPBaseMultiplier config = new FastAPBaseMultiplier("0", "attack:2,unused:" + invalid, name -> name);
        NSMMEException failure = assertThrows(NSMMEException.class, () -> config.evaluate(Collections.emptyMap()));
        diagnostic(failure, "baseAttributeMultipleList(baml)[unused]", "FastAP");
    }

    @Test
    void validZeroGlobalStillCalculatesEveryListedExpression() {
        java.util.Map<String, Integer> reads = new java.util.HashMap<>();
        java.util.Map<String, Number> context = new java.util.HashMap<String, Number>() {
            @Override
            public Number get(Object key) {
                reads.merge((String) key, 1, Integer::sum);
                return super.get(key);
            }
        };
        context.put("first", 2.0);
        context.put("last", 3.0);
        FastAPBaseMultiplier.Values values =
                new FastAPBaseMultiplier("0", "attack:first,unused:last", name -> name).evaluate(context);
        assertEquals(Integer.valueOf(1), reads.get("first"));
        assertEquals(Integer.valueOf(1), reads.get("last"));
        assertArrayEquals(new Number[] {0.0, 0.0}, values.scale("attack", new Number[] {100, 200}));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1/0", "missing", "("})
    void calculationExceptionsKeepTheirActualCause(String expression) {
        for (boolean listed : new boolean[] {false, true}) {
            FastAPBaseMultiplier config = new FastAPBaseMultiplier(
                    listed ? "1" : expression, listed ? "attack:" + expression : null, name -> name);
            NSMMEException failure = assertThrows(NSMMEException.class, () -> config.evaluate(Collections.emptyMap()));
            diagnostic(failure, listed ? "baml)[attack]" : "bam)", "求值失败");
            assertNotNull(failure.getCause());
            assertTrue(failure.getMessage().contains(failure.getCause().getMessage()));
            if ("1/0".equals(expression)) assertInstanceOf(ArithmeticException.class, failure.getCause());
        }
    }

    @Test
    void compilationExceptionsKeepCauseAndParameter() {
        for (boolean listed : new boolean[] {false, true}) {
            NSMMEException failure = assertThrows(
                    NSMMEException.class,
                    () -> new FastAPBaseMultiplier(listed ? "1" : "1+", listed ? "attack:1+" : null, name -> name));
            diagnostic(failure, listed ? "baml)[attack]" : "bam)", "编译失败");
            assertNotNull(failure.getCause());
            assertTrue(failure.getMessage().contains(failure.getCause().getMessage()));
        }
    }

    @Test
    void finiteGlobalTimesFiniteLocalOverflowIsExplicit() {
        NSMMEException failure =
                assertThrows(NSMMEException.class, () -> new FastAPBaseMultiplier("huge", "attack:2", name -> name)
                        .evaluate(Collections.singletonMap("huge", Double.MAX_VALUE)));
        diagnostic(failure, "baseAttributeMultiple(bam)", "baseAttributeMultipleList(baml)[attack]");
        assertTrue(failure.getMessage().contains("乘积非有限"), failure.getMessage());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void realBigDecimalNegativeUnderflowIsRejectedBeforeDoubleConversion(boolean listed) {
        String source = "-(n**400)";
        AtomicInteger reads = new AtomicInteger();
        Map<String, Number> context = new HashMap<String, Number>() {
            @Override
            public Number get(Object key) {
                reads.incrementAndGet();
                return super.get(key);
            }
        };
        context.put("n", new BigDecimal("0.1"));
        Number actual = new Expression(source).compile().calculate(context);
        assertInstanceOf(BigDecimal.class, actual);
        assertEquals(new BigDecimal("-1E-400"), actual);
        assertEquals(Double.doubleToRawLongBits(-0.0), Double.doubleToRawLongBits(actual.doubleValue()));
        reads.set(0);
        FastAPBaseMultiplier config =
                new FastAPBaseMultiplier(listed ? "1" : source, listed ? "attack:" + source : null, name -> name);
        NSMMEException failure = assertThrows(NSMMEException.class, () -> config.evaluate(context));
        diagnostic(failure, listed ? "baseAttributeMultipleList(baml)[attack]" : "baseAttributeMultiple(bam)", "非负有限数");
        assertTrue(failure.getMessage().contains("-1E-400"));
        assertNull(failure.getCause(), "数值非法并非引擎抛错，不伪造 cause");
        assertEquals(1, reads.get(), "校验不能重新执行 Expression");
    }

    @ParameterizedTest
    @ValueSource(strings = {"n-n", "n**400"})
    void realBigDecimalZeroAndTinyPositiveRemainAllowed(String source) {
        Map<String, Number> context = Collections.singletonMap("n", new BigDecimal("0.1"));
        BigDecimal actual = assertInstanceOf(
                BigDecimal.class, new Expression(source).compile().calculate(context));
        assertEquals(source.equals("n-n") ? 0 : 1, actual.signum());
        for (boolean listed : new boolean[] {false, true}) {
            FastAPBaseMultiplier.Values values = new FastAPBaseMultiplier(
                            listed ? "1" : source, listed ? "attack:" + source : null, name -> name)
                    .evaluate(context);
            assertArrayEquals(new Number[] {0.0, 0.0}, values.scale("attack", new Number[] {10, 20}));
        }
    }

    private static void diagnostic(NSMMEException failure, String parameter, String reason) {
        assertEquals(NSMMEException.class, failure.getClass());
        assertTrue(failure.getMessage().contains(parameter), failure.getMessage());
        assertTrue(failure.getMessage().contains(reason), failure.getMessage());
    }
}
