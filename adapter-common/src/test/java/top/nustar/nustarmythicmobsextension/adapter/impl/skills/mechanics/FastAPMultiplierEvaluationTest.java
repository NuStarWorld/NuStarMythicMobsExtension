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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.idealstate.sugar.next.calculate.Expression;

@DisplayName("FastAP 真实 Expression：逐目标求值，不替换引擎")
class FastAPMultiplierEvaluationTest {
    @Test
    void eachExpressionReadsItsVariableOnceAndValuesAreReused() {
        CountingContext context = new CountingContext();
        context.put("global", 2.0);
        context.put("local", 3.0);
        context.put("other", 4.0);
        FastAPBaseMultiplier config = new FastAPBaseMultiplier("global", "attack:local,defense:other", name -> name);
        FastAPBaseMultiplier.Values first = config.evaluate(context);
        assertFalse(first.isIdentity(Arrays.asList("attack", "defense")));
        first.scale("attack", new Number[] {1, 2});
        first.scale("defense", new Number[] {1, 2});
        first.scale("attack", new Number[] {5, 6});
        assertEquals(3, context.reads.size());
        context.reads.values().forEach(count -> assertEquals(1, count.intValue()));
        context.put("global", 5.0);
        FastAPBaseMultiplier.Values second = config.evaluate(context);
        context.reads.values().forEach(count -> assertEquals(2, count.intValue()));
        assertArrayEquals(new Number[] {6.0, 12.0}, first.scale("attack", new Number[] {1, 2}));
        assertArrayEquals(new Number[] {15.0, 30.0}, second.scale("attack", new Number[] {1, 2}));
    }

    @Test
    void realDoubleTargetContextChangesWithoutResultCaching() {
        Map<String, Number> context = new HashMap<>();
        context.put("caster_level", 2.0);
        context.put("caster_hp", 80.0);
        context.put("caster_mhp", 100.0);
        context.put("target_hp", 50.0);
        context.put("target_mhp", 100.0);
        FastAPBaseMultiplier config = new FastAPBaseMultiplier(
                "caster_level*target_hp/target_mhp", "attack:caster_hp/caster_mhp", name -> name);
        FastAPBaseMultiplier.Values first = config.evaluate(context);
        assertEquals(80.0, first.scale("attack", new Number[] {100, 100})[0].doubleValue());
        context.put("target_hp", 25.0);
        assertEquals(
                40.0,
                config.evaluate(context)
                        .scale("attack", new Number[] {100, 100})[0]
                        .doubleValue());
        assertEquals(80.0, first.scale("attack", new Number[] {100, 100})[0].doubleValue());
    }

    @Test
    void identityDependsOnApplicableNamesAndEffectiveProduct() {
        FastAPBaseMultiplier.Values cancelled =
                new FastAPBaseMultiplier("2", "attack:0.5", name -> name).evaluate(Collections.emptyMap());
        assertTrue(cancelled.isIdentity(Collections.singleton("attack")));
        assertFalse(cancelled.isIdentity(Arrays.asList("attack", "defense")));
        FastAPBaseMultiplier.Values irrelevant =
                new FastAPBaseMultiplier("1", "unused:7", name -> name).evaluate(Collections.emptyMap());
        assertTrue(irrelevant.isIdentity(Arrays.asList("attack", "defense")));
        assertFalse(irrelevant.isIdentity(Collections.singleton("unused")));
        assertTrue(cancelled.isIdentity(Collections.emptySet()));
    }

    @Test
    void integerDivisionRemainsAnEngineLimitation() {
        FastAPBaseMultiplier config = new FastAPBaseMultiplier("target_hp/100", null, name -> name);
        assertEquals(
                0.0,
                config.evaluate(Collections.singletonMap("target_hp", 50L))
                        .scale("attack", new Number[] {100, 100})[0]
                        .doubleValue());
        assertEquals(
                50.0,
                config.evaluate(Collections.singletonMap("target_hp", 50.0))
                        .scale("attack", new Number[] {100, 100})[0]
                        .doubleValue());
    }

    @Test
    @DisplayName("Long 正溢出回绕为 1 是真实引擎限制，不宣称检测所有 overflow")
    void positiveLongWraparoundIsDocumentedRatherThanClaimedChecked() {
        String source = "9223372036854775807*9223372036854775807";
        Number engineResult = new Expression(source).compile().calculate(Collections.emptyMap());
        assertEquals(Long.class, engineResult.getClass());
        assertEquals(1L, engineResult.longValue());
        assertTrue(new FastAPBaseMultiplier(source, null, name -> name)
                .evaluate(Collections.emptyMap())
                .isIdentity(Collections.singleton("attack")));
    }

    private static final class CountingContext extends HashMap<String, Number> {
        private final Map<Object, Integer> reads = new HashMap<>();

        @Override
        public Number get(Object key) {
            reads.merge(key, 1, Integer::sum);
            return super.get(key);
        }
    }
}
