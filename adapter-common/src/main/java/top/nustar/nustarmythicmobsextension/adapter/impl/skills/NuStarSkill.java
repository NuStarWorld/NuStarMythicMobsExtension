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

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

    /**
     * 判断该生物此刻能否交给 AttributePlus 处理。
     *
     * <p>AttributePlus 不使用传入的实体对象，而是按 UUID 重新查一次：玩家查不到在线玩家会得到 null， 非玩家查不到实体会直接抛 NullPointerException，随后在
     * AttributeHandle 构造时以非空检查失败。 因此在交给它之前先确认实体此刻仍能按 UUID 取回。
     *
     * <p>典型取不到的情形：上一段伤害已把目标打死、玩家掉线或切服、实体被移除或所在区块已卸载。
     */
    static boolean isResolvable(LivingEntity entity) {
        if (entity == null || entity.isDead() || !entity.isValid()) {
            return false;
        }
        if (entity instanceof Player) {
            // 玩家按 UUID 取在线玩家；离线或切服中取不到。
            return Bukkit.getPlayer(entity.getUniqueId()) != null;
        }
        return Bukkit.getEntity(entity.getUniqueId()) != null;
    }
}
