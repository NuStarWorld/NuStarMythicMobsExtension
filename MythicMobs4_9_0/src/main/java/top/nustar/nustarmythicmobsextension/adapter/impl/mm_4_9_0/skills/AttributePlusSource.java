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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.AbstractEntityAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.MythicLineConfigAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.placeholder.helper.PlaceholderDoubleHelperImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.placeholder.helper.PlaceholderStringHelperImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.AttributePlusSourceAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarSkill;

public class AttributePlusSource extends SkillMechanic implements ITargetedEntitySkill, NuStarMechanic {
    private final NuStarSkill attributePlusSourceSkill;

    public AttributePlusSource(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        this.attributePlusSourceSkill = new AttributePlusSourceAdapter(
                new PlaceholderStringHelperImpl(),
                new PlaceholderDoubleHelperImpl(),
                new MythicLineConfigAdapterImpl(mlc));
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        return attributePlusSourceSkill.castAtEntity(
                new SkillMetadataAdapterImpl(skillMetadata), new AbstractEntityAdapterImpl(abstractEntity));
    }
}
