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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.targets;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarTargerSelector;
import top.nustar.nustarmythicmobsextension.adapter.impl.targets.CylinderConeConfig;
import top.nustar.nustarmythicmobsextension.adapter.impl.targets.CylinderConeGeometry;
import top.nustar.nustarmythicmobsextension.adapter.impl.targets.CylinderConeSelectorAdapter;

public class CylinderConeSelector extends IEntitySelector implements NuStarTargerSelector {
    private final CylinderConeConfig<SkillMetadata> config;

    public CylinderConeSelector(SkillExecutor executor, MythicLineConfig mlc) {
        super(executor, mlc);
        // 单元数据重载不传目标实体；不能用原生 PlaceholderDouble 的非法默认值。
        config = new CylinderConeConfig<>(
                (key, fallback) -> mlc.getString(key, fallback), raw -> PlaceholderString.of(raw)::get);
    }

    @Override
    public HashSet<AbstractEntity> getEntities(SkillMetadata metadata) {
        CylinderConeSelectorAdapter.requirePrimaryThread();
        CylinderConeGeometry geometry = config.evaluate(metadata);
        Entity caster = metadata.getCaster().getEntity().getBukkitEntity();
        Location casterLocation = caster.getLocation();
        Location center = casterLocation;
        if (config.usesOrigin()) {
            AbstractLocation origin = metadata.getOrigin();
            if (origin != null) {
                center = BukkitAdapter.adapt(origin);
            }
        }
        return CylinderConeSelectorAdapter.select(
                config, geometry, caster, casterLocation, center, BukkitAdapter::adapt);
    }
}
