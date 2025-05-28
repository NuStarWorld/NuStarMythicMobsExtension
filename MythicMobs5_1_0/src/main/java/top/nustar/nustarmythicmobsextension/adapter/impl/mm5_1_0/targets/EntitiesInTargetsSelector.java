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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.targets;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import java.util.HashSet;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarTargerSelector;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.targets.EntitiesInTargetsSelectorAdapter;

public class EntitiesInTargetsSelector extends IEntitySelector implements NuStarTargerSelector {
    private final EntitiesInTargetsSelectorAdapter entitiesInTargetsSelectorAdapter;

    public EntitiesInTargetsSelector(SkillExecutor executor, MythicLineConfig mlc) {
        super(executor, mlc);
        this.entitiesInTargetsSelectorAdapter = new EntitiesInTargetsSelectorAdapter();
    }

    @Override
    public HashSet<AbstractEntity> getEntities(SkillMetadata skillMetadata) {
        HashSet<AbstractEntityAdapter<?>> entities =
                entitiesInTargetsSelectorAdapter.getEntities(new SkillMetadataAdapterImpl(skillMetadata));
        HashSet<AbstractEntity> abstractEntities = new HashSet<>();
        for (AbstractEntityAdapter<?> entity : entities) {
            abstractEntities.add((AbstractEntity) entity.getActualObject());
        }
        return abstractEntities;
    }
}
