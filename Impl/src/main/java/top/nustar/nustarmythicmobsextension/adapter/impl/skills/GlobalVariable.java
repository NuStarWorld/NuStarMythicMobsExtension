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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration.Variable;

/**
 * @author : NuStar Date : 2025/6/25 22:53 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
public interface GlobalVariable {
    default Map<String, Number> parseExpressionContext(
            SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity, List<Variable> variables) {
        LivingEntity caster =
                (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
        Map<String, Number> context = new HashMap<>(variables.size());
        if (caster instanceof Player) {
            for (Variable variable : variables) {
                Player player = (Player) caster;
                context.put(variable.getName(), variable.asBigDecimal(player));
            }
        }
        context.put("caster_level", skillMetadata.getCaster().getLevel());
        context.put("caster_hp", skillMetadata.getCaster().getEntity().getHealth());
        context.put("caster_mhp", skillMetadata.getCaster().getEntity().getMaxHealth());
        context.put("target_hp", abstractEntity.getHealth());
        context.put("target_mhp", abstractEntity.getMaxHealth());
        return context;
    }
}
