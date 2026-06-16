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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.serverct.ersha.api.AttributeAPI;
import org.serverct.ersha.attribute.data.AttributeData;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicLineConfigAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarSkill;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderStringAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderStringHelper;
import top.nustar.nustarmythicmobsextension.manager.TemporaryAttributeSourceManager;

public class RemoveAPSourceAdapter implements NuStarSkill {

    private final PlaceholderStringAdapter<?> sourceName;
    private final boolean isStartWith;
    private final TemporaryAttributeSourceManager temporaryAttributeSourceManager =
            TemporaryAttributeSourceManager.getTemporaryAttributeSourceManager();

    public RemoveAPSourceAdapter(PlaceholderStringHelper<?> placeholderStringHelper, MythicLineConfigAdapter<?> mlc) {
        this.sourceName = placeholderStringHelper.of(mlc.getString(new String[] {"sourceName", "s"}));
        this.isStartWith = mlc.getBoolean(new String[] {"isStartWith", "i"}, false);
    }

    @Override
    public boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity) {
        String sourceName = this.sourceName.get(skillMetadata, abstractEntity);
        if (sourceName == null || sourceName.isEmpty()) {
            return true;
        }
        Entity bukkitEntity = abstractEntity.getBukkitEntity();
        if (!(bukkitEntity instanceof LivingEntity)) {
            return true;
        }
        AttributeData attrData = AttributeAPI.getAttrData((LivingEntity) bukkitEntity);

        List<String> removeSourceList = new ArrayList<>();
        if (isStartWith) {
            Set<String> apiSources = attrData.getCentral().getAPISources().keySet();
            for (String apiSource : apiSources) {
                if (apiSource.startsWith(sourceName)) {
                    removeSourceList.add(apiSource);
                }
            }
        } else {
            removeSourceList.add(sourceName);
        }
        for (String removeSource : removeSourceList) {
            AttributeAPI.takeSourceAttribute(attrData, removeSource);
        }

        temporaryAttributeSourceManager.removeAttributeSourceInstance(
                bukkitEntity.getUniqueId(), sourceName, isStartWith);

        return true;
    }
}
