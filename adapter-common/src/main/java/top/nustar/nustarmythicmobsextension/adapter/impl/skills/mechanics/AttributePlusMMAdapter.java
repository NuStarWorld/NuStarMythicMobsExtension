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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.serverct.ersha.AttributePlus;
import org.serverct.ersha.api.AttributeAPI;
import org.serverct.ersha.attribute.data.AttributeData;
import team.idealstate.sugar.logging.Log;
import top.nustar.nustarmythicmobsextension.adapter.*;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarSkill;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderStringAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderStringHelper;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.utils.AttributeUtils;
import top.nustar.nustarmythicmobsextension.utils.DamageUtil;

public class AttributePlusMMAdapter implements NuStarSkill {
    private final MainConfiguration mainConfiguration;
    protected final PlaceholderStringAdapter<?> attrName;
    protected final boolean clear;
    protected final boolean preventImmunity;
    protected final boolean preventKnockback;

    public AttributePlusMMAdapter(
            PlaceholderStringHelper<?> placeholderStringHelper,
            MythicLineConfigAdapter<?> mlc,
            MainConfiguration mainConfiguration) {
        this.attrName = placeholderStringHelper.of(mlc.getString(new String[] {"attr", "a"}));
        this.clear = mlc.getBoolean(new String[] {"clear", "c"}, false);
        this.preventImmunity = mlc.getBoolean(new String[] {"preventImmunity", "pi"}, false);
        this.preventKnockback = mlc.getBoolean(new String[] {"preventKnockback", "pk"}, false);
        this.mainConfiguration = mainConfiguration;
    }

    @Override
    public boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity) {
        try {
            LivingEntity entity =
                    (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
            List<String> attr =
                    Arrays.asList(attrName.get(skillMetadata, abstractEntity).split(","));
            AttributeData data = AttributePlus.INSTANCE.getAttributeManager().getAttributeData(entity);
            AttributeAPI.addSourceAttribute(data, "APMM_XULI", Collections.singletonList("蓄力加成:100"));
            if (clear) {
                AttributeData newData = AttributeData.Companion.create(entity);
                AttributeAPI.addSourceAttribute(newData, "APMM_XULI", Collections.singletonList("蓄力加成:100"));
                AttributeAPI.addSourceAttribute(
                        newData,
                        "APMM_WhiteList",
                        AttributeUtils.getWhiteAttributeList(data, mainConfiguration.getWhiteAttrList()));
                AttributeAPI.addSourceAttribute(newData, "APMM", attr);
                AttributePlus.INSTANCE.getAttributeManager().entityAttributeData.put(entity.getUniqueId(), newData);
                DamageUtil.damage(skillMetadata, abstractEntity, preventImmunity, preventKnockback);
                AttributePlus.INSTANCE.getAttributeManager().entityAttributeData.put(entity.getUniqueId(), data);
            } else {
                AttributeAPI.addSourceAttribute(data, "APMM", attr);
                DamageUtil.damage(skillMetadata, abstractEntity, preventImmunity, preventKnockback);
                AttributeAPI.takeSourceAttribute(data, "APMM");
            }
            AttributeAPI.takeSourceAttribute(data, "APMM_XULI");
            return true;
        } catch (Exception e) {
            Log.error(e);
            return false;
        }
    }
}
