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

package top.nustar.nustarmythicmobsextension.service.enums;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("圆柱扇形别名与旧选择器注册")
class TargetSelectorTypeTest {
    @ParameterizedTest(name = "别名 {0} 忽略大小写")
    @ValueSource(strings = {"NuStarCylinderCone", "NSCylinderCone", "TailedBeastCylinderCone", "PTBCylinderCone"})
    void cylinderAliases(String alias) {
        assertSame(TargetSelectorType.CYLINDER_CONE, TargetSelectorType.of(alias));
        assertSame(TargetSelectorType.CYLINDER_CONE, TargetSelectorType.of(alias.toUpperCase(Locale.ROOT)));
        assertSame(TargetSelectorType.CYLINDER_CONE, TargetSelectorType.of(alias.toLowerCase(Locale.ROOT)));
    }

    @Test
    @DisplayName("EntitiesInTargets 与 SuperForward 旧注册保持不变")
    void legacyNames() {
        assertSame(TargetSelectorType.ENTITIES_IN_TARGETS, TargetSelectorType.of("eNtItIeSiNtArGeTs"));
        assertSame(TargetSelectorType.SUPER_FORWARD_TARGETER, TargetSelectorType.of("sUpErFoRwArD"));
    }

    @ParameterizedTest(name = "未声明别名 [{0}] 不注册")
    @NullAndEmptySource
    @ValueSource(strings = {"EIC", "eic", "CylinderCone", "NuStarCylinderCone ", "unknown"})
    void undeclaredNames(String name) {
        assertNull(TargetSelectorType.of(name));
    }
}
