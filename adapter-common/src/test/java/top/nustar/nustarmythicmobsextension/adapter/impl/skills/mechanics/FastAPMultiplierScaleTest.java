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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import top.nustar.nustarmythicmobsextension.exception.NSMMEException;

@DisplayName("FastAP 有效倍率缩放区间：只测 helper，不冒充 AP 集成")
class FastAPMultiplierScaleTest {
    @Test
    void bothEndpointsAreCopiedWithoutMutatingOriginalNumbers() {
        Number low = Long.valueOf(100);
        Number high = Long.valueOf(200);
        Number[] basis = {low, high};
        Number[] scaled = values("2", "attack:1.5").scale("attack", basis);
        assertArrayEquals(new Number[] {300.0, 600.0}, scaled);
        assertNotSame(basis, scaled);
        assertSame(low, basis[0]);
        assertSame(high, basis[1]);
        assertArrayEquals(new Number[] {100L, 200L}, basis);
        scaled[0] = -999;
        assertEquals(100L, basis[0]);
    }

    @Test
    @DisplayName("base100..200*M3 得300..600；外部再加attr50才是350..650")
    void formulaExampleKeepsAttrOutsideHelper() {
        Number[] scaled = values("3", null).scale("attack", new Number[] {100, 200});
        assertArrayEquals(new Number[] {300.0, 600.0}, scaled);
        // 仅算术示例；真实 Adapter 的 updateTemp(false) 顺序由 ASM 契约另行核对。
        assertEquals(350.0, scaled[0].doubleValue() + 50);
        assertEquals(650.0, scaled[1].doubleValue() + 50);
        assertArrayEquals(new Number[] {300.0, 300.0}, values("3", null).scale("attack", new Number[] {100, 100}));
    }

    @Test
    void targetsAlwaysScaleTheirOriginalBasisWithoutAccumulation() {
        FastAPBaseMultiplier config = new FastAPBaseMultiplier("target_hp/100", null, name -> name);
        Number[] original = {100, 200};
        Number[] first =
                config.evaluate(Collections.singletonMap("target_hp", 200.0)).scale("attack", original);
        Number[] second =
                config.evaluate(Collections.singletonMap("target_hp", 300.0)).scale("attack", original);
        assertArrayEquals(new Number[] {200.0, 400.0}, first);
        assertArrayEquals(new Number[] {300.0, 600.0}, second);
        assertArrayEquals(new Number[] {100, 200}, original);
        assertNotSame(first, second);
    }

    @ParameterizedTest
    @CsvSource({"0,0", "0.25,25", "1,100", "2.5,250"})
    void unlistedAttributesUseGlobalAndZeroBasisDoesNotImplyIdentity(String multiple, double expected) {
        FastAPBaseMultiplier.Values values = values(multiple, "unrelated:7");
        assertArrayEquals(new Number[] {expected, expected * 2}, values.scale("attack", new Number[] {100, 200}));
        assertArrayEquals(new Number[] {0.0, 0.0}, values.scale("attack", new Number[] {0, 0}));
        assertEquals("1".equals(multiple), values.isIdentity(Collections.singleton("attack")));
    }

    @Test
    void negativeFiniteBasisIsNotRejectedAsANegativeMultiplier() {
        assertArrayEquals(new Number[] {-40.0, -20.0}, values("2", null).scale("attack", new Number[] {-20, -10}));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void eitherEndpointOverflowNamesTheAttributeAndParameters(int endpoint) {
        Number[] basis = {1, 2};
        basis[endpoint] = Double.MAX_VALUE;
        NSMMEException failure =
                assertThrows(NSMMEException.class, () -> values("2", null).scale("攻击力", basis));
        assertTrue(failure.getMessage().contains("baseAttributeMultiple(bam)"), failure.getMessage());
        assertTrue(failure.getMessage().contains("baseAttributeMultipleList(baml)[攻击力]"), failure.getMessage());
        assertTrue(failure.getMessage().contains("缩放基础区间后非有限"), failure.getMessage());
        assertEquals(Double.MAX_VALUE, basis[endpoint].doubleValue());
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void nonFiniteBasisCannotBeHiddenByZeroMultiplier(double invalid) {
        assertThrows(NSMMEException.class, () -> values("0", null).scale("attack", new Number[] {invalid, 1}));
        assertThrows(NSMMEException.class, () -> values("0", null).scale("attack", new Number[] {1, invalid}));
    }

    private static FastAPBaseMultiplier.Values values(String global, String list) {
        return new FastAPBaseMultiplier(global, list, name -> name).evaluate(Collections.emptyMap());
    }
}
