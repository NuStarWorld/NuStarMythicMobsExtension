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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import java.util.HashSet;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillCasterAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;

public class SkillMetadataAdapterImpl extends SkillMetadataAdapter<SkillMetadata> {

    public SkillMetadataAdapterImpl(SkillMetadata metadata) {
        super(metadata);
    }

    @Override
    public SkillCasterAdapter<?> getCaster() {
        return new SkillCasterAdapterImpl(getActualObject().getCaster());
    }

    @Override
    public HashSet<AbstractEntityAdapter<?>> getEntityTargets() {
        HashSet<AbstractEntityAdapter<?>> adapters = new HashSet<>();
        for (AbstractEntity abstractEntity : getActualObject().getEntityTargets()) {
            adapters.add(new AbstractEntityAdapterImpl(abstractEntity));
        }
        return adapters;
    }
}
