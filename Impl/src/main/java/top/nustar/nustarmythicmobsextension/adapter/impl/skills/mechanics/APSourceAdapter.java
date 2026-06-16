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

import java.util.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.serverct.ersha.AttributePlus;
import org.serverct.ersha.api.AttributeAPI;
import org.serverct.ersha.attribute.data.AttributeData;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicLineConfigAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarSkill;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderDoubleAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderStringAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderDoubleHelper;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderStringHelper;
import top.nustar.nustarmythicmobsextension.manager.TemporaryAttributeSourceManager;
import top.nustar.nustarmythicmobsextension.utils.AttributeUtils;

@NativeObfuscation
public class APSourceAdapter implements NuStarSkill {
    protected final PlaceholderStringAdapter<?> attrName;
    protected final Map<String, Integer> percentageAttr;
    protected final PlaceholderStringAdapter<?> sourceName;
    protected final PlaceholderDoubleAdapter<?> time;
    private final TemporaryAttributeSourceManager temporaryAttributeSourceManager = TemporaryAttributeSourceManager.getTemporaryAttributeSourceManager();

    public APSourceAdapter(
            PlaceholderStringHelper<?> placeholderStringHelper,
            PlaceholderDoubleHelper<?> placeholderDoubleHelper,
            MythicLineConfigAdapter<?> mlc) {
        this.attrName = placeholderStringHelper.of(mlc.getString(new String[] {"attr", "a"}));
        String percentageAttr = mlc.getString(new String[] {"percentage", "pa"});
        if (percentageAttr != null) {
            this.percentageAttr = AttributeUtils.getPercentageAttr(Arrays.asList(percentageAttr.split(",")));
        } else {
            this.percentageAttr = null;
        }
        this.time = placeholderDoubleHelper.of(mlc.getString(new String[] {"time", "t"}, "-1"));
        this.sourceName = placeholderStringHelper.of(mlc.getString(new String[] {"sourceName", "s"}));
    }

    @Override
    @NativeObfuscation
    public boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity) {
        LivingEntity entity = (LivingEntity) abstractEntity.getBukkitEntity();
        AttributeData data = AttributePlus.INSTANCE.getAttributeManager().getAttributeData(entity);
        List<String> attr =
                Arrays.asList(attrName.get(skillMetadata, abstractEntity).split(","));
        String defaultSource = (sourceName == null || sourceName.get(skillMetadata, abstractEntity) == null
                        ? "NSMME-APSource-" + UUID.randomUUID()
                        : sourceName.get(skillMetadata, abstractEntity));
        int sourceTime = (int) time.get(skillMetadata, abstractEntity);

        // 百分比属性
        if (percentageAttr != null) {
            List<String> percentageAttrList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : this.percentageAttr.entrySet()) {
                percentageAttrList.add(entry.getKey() + ":"
                        + (data.getRandomValue(entry.getKey()).doubleValue() * entry.getValue() / 100));
            }
            attr.addAll(percentageAttrList);
        }
        AttributeAPI.addSourceAttribute(data, defaultSource, attr);
        if (sourceTime > 0) {
            temporaryAttributeSourceManager.addAttributeSourceInstance(entity, defaultSource, sourceTime);
        }
        if (entity instanceof Player) {
            AttributeAPI.updateAttribute(entity);
        }
        return true;
    }
}
