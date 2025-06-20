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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.skills.helper;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.DependsOn;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.skills.AttributePlusSource;
import top.nustar.nustarmythicmobsextension.service.MechanicHelperService;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportMechanicType;
import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;
import top.nustar.nustarmythicmobsextension.service.enums.MythicMobsVersion;

@Component
@SupportMechanicType(type = MechanicType.ATTRIBUTE_PLUS_SOURCE, version = MythicMobsVersion.MM5_6_0)
@DependsOn(classes = "io.lumine.mythic.core.config.MythicConfigImpl")
@SuppressWarnings({"unused"})
public class AttributePlusSourceHelper implements MechanicHelperService {
    @Override
    public NuStarMechanic findMechanic(Object... objects) {
        return new AttributePlusSource("apsource", (SkillExecutor) objects[0], (MythicLineConfig) objects[1]);
    }
}
