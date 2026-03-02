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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.AbstractEntityAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.MythicLineConfigAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.placeholder.helper.PlaceholderDoubleHelperImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarSkill;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.mechanics.NuStarThreatAdapter;
import top.nustar.nustarmythicmobsextension.api.service.MobThreatService;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;

/**
 * @author : NuStar Date : 2025/6/25 23:12 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
public class NuStarThreat extends SkillMechanic implements NuStarMechanic, ITargetedEntitySkill {
    private final NuStarSkill nuStarThreatSkill;

    public NuStarThreat(
            String skill,
            SkillExecutor executor,
            MythicLineConfig mlc,
            MainConfiguration mainConfiguration,
            MobThreatService mobThreatService) {
        super(executor, skill, mlc);
        this.nuStarThreatSkill = new NuStarThreatAdapter(
                new PlaceholderDoubleHelperImpl(),
                new MythicLineConfigAdapterImpl(mlc),
                mainConfiguration,
                mobThreatService);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        boolean result = nuStarThreatSkill.castAtEntity(
                new SkillMetadataAdapterImpl(skillMetadata), new AbstractEntityAdapterImpl(abstractEntity));
        return result ? SkillResult.SUCCESS : SkillResult.ERROR;
    }
}
