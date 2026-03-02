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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills.mechanics.helper;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills.mechanics.NuStarThreat;
import top.nustar.nustarmythicmobsextension.api.service.MobThreatService;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.service.MechanicHelperService;
import top.nustar.nustarmythicmobsextension.service.annotations.MythicMobs4_9_0;
import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;

/**
 * @author : NuStar Date : 2025/6/25 23:17 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
@Component
@MythicMobs4_9_0
@SuppressWarnings({"unused"})
public class NuStarThreatHelper implements MechanicHelperService {
    private volatile MobThreatService mobThreatService;

    @Autowired
    public void setMobThreatService(MobThreatService mobThreatService) {
        this.mobThreatService = mobThreatService;
    }

    @Override
    public NuStarMechanic findMechanic(Object... objects) {
        return new NuStarThreat(
                "nustarthreat", (MythicLineConfig) objects[0], (MainConfiguration) objects[1], mobThreatService);
    }

    @Override
    public MechanicType getType() {
        return MechanicType.NUSTAR_THREAT;
    }
}
