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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.targets.helper;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import team.idealstate.sugar.next.context.annotation.component.Component;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarTargerSelector;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.targets.EntitiesInTargetsSelector;
import top.nustar.nustarmythicmobsextension.service.TargetSelectorHelperService;
import top.nustar.nustarmythicmobsextension.service.annotations.MythicMobs4_9_0;
import top.nustar.nustarmythicmobsextension.service.enums.TargetSelectorType;

@Component
@MythicMobs4_9_0
@SuppressWarnings({"unused"})
public class EntitiesInTargetsSelectorHelper implements TargetSelectorHelperService {

    @Override
    public NuStarTargerSelector findSelector(Object... objects) {
        return new EntitiesInTargetsSelector((MythicLineConfig) objects[0]);
    }

    @Override
    public TargetSelectorType getType() {
        return TargetSelectorType.ENTITIES_IN_TARGETS;
    }
}
