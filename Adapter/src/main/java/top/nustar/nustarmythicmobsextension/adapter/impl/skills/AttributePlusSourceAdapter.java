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

package top.nustar.nustarmythicmobsextension.adapter.impl.skills;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.serverct.ersha.AttributePlus;
import org.serverct.ersha.api.AttributeAPI;
import org.serverct.ersha.attribute.data.AttributeData;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import top.nustar.nustarmythicmobsextension.adapter.*;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderDoubleAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderStringAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderDoubleHelper;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderStringHelper;
import top.nustar.nustarmythicmobsextension.utils.InstanceUtil;

@NativeObfuscation
public class AttributePlusSourceAdapter {
    protected final PlaceholderStringAdapter<?> attrName;
    protected final boolean persistent;
    protected final PlaceholderDoubleAdapter<?> time;

    public AttributePlusSourceAdapter(
            PlaceholderStringHelper<?> placeholderStringHelper,
            PlaceholderDoubleHelper<?> placeholderDoubleHelper,
            MythicLineConfigAdapter<?> mlc) {
        this.attrName = placeholderStringHelper.of(mlc.getString(new String[] {"attr", "a"}));
        this.persistent = mlc.getBoolean(new String[] {"persistent", "p"}, false);
        this.time = placeholderDoubleHelper.of(mlc.getString(new String[] {"time", "t"}));
    }

    @NativeObfuscation
    public boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity) {
        LivingEntity entity =
                (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
        List<String> attr =
                Arrays.asList(attrName.get(skillMetadata, abstractEntity).split(","));
        AttributeData data = AttributePlus.INSTANCE.getAttributeManager().getAttributeData(entity);
        String source = "APSource" + UUID.randomUUID();
        AttributeAPI.addSourceAttribute(data, source, attr);
        AttributeAPI.updateAttribute(entity);
        if (persistent) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    AttributeAPI.takeSourceAttribute(data, source);
                }
            }.runTaskLaterAsynchronously(InstanceUtil.getInstance(Plugin.class), (long) (time.get(skillMetadata, abstractEntity) * 20));
        }
        return true;
    }
}
