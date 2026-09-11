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

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;

/**
 * @author : NuStar Date : 2025/6/25 21:32 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
public interface NuStarSkill {
    boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity);

    /**
     * 把 Bukkit 实体当作生物读取；不是生物时返回 null。
     *
     * <p>MythicMobs 的目标可能包含展示实体等没有血量的对象（如 ItemDisplay）。 直接强转会抛 ClassCastException 并中断整个技能，因此由各机制自行判断并跳过。
     */
    static LivingEntity asLivingEntity(Entity entity) {
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }

    /** 读取目标实体并要求它是生物；不是生物时返回 null。 */
    static LivingEntity livingTarget(AbstractEntityAdapter<?> abstractEntity) {
        return asLivingEntity(abstractEntity.getBukkitEntity());
    }

    /** 读取施法者并要求它是生物；不是生物时返回 null。 */
    static LivingEntity livingCaster(SkillMetadataAdapter<?> skillMetadata) {
        return asLivingEntity(skillMetadata.getCaster().getEntity().getBukkitEntity());
    }
}
