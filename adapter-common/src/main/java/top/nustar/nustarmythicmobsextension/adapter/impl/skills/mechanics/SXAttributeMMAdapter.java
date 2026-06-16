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

import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import team.idealstate.sugar.logging.Log;
import top.nustar.nustarmythicmobsextension.adapter.*;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarSkill;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderStringAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderStringHelper;
import top.nustar.nustarmythicmobsextension.utils.DamageUtil;

public class SXAttributeMMAdapter implements NuStarSkill {
    protected final PlaceholderStringAdapter<?> attrName;
    protected final boolean preventImmunity;
    protected final boolean preventKnockback;

    public SXAttributeMMAdapter(PlaceholderStringHelper<?> placeholderStringHelper, MythicLineConfigAdapter<?> mlc) {
        this.attrName = placeholderStringHelper.of(mlc.getString(new String[] {"attr", "a"}));
        this.preventImmunity = mlc.getBoolean(new String[] {"preventImmunity", "pi"}, false);
        this.preventKnockback = mlc.getBoolean(new String[] {"preventKnockback", "pk"}, false);
    }

    @Override
    public boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity) {
        try {
            LivingEntity entity =
                    (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
            List<String> attr =
                    Arrays.asList(attrName.get(skillMetadata, abstractEntity).split(","));
            SXAttributeData statsData = SXAttribute.getApi().getLoreData(entity, null, attr);
            if (statsData != null && statsData.isValid()) {
                SXAttribute.getApi().setEntityAPIData(SXAttributeMMAdapter.class, entity.getUniqueId(), statsData);
                SXAttribute.getApi().updateStats(entity);
            }
            DamageUtil.damage(skillMetadata, abstractEntity, preventImmunity, preventKnockback);
            SXAttribute.getApi().removeEntityAPIData(SXAttributeMMAdapter.class, entity.getUniqueId());
            SXAttribute.getApi().updateStats(entity);
            return true;
        } catch (Exception e) {
            Log.error(e);
            return false;
        }
    }
}
