package top.nustar.nustarmythicmobsextension.api.service;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface MobThreatService {
    /**
     * 将所有仇恨值至目标实体
     * @param from 实体来源 UUID
     * @param target 目标实体
     */
    void transferMobThreat(UUID from, LivingEntity target);

    /**
     * 添加仇恨值
     * @param mob MythicMobs 生物
     * @param uuid 要添加仇恨值的实体 UUID
     * @param threat 仇恨值（可以为负数
     */
    void addThreat(Creature mob, UUID uuid, long threat);

    /**
     * 设置仇恨值
     * @param mob MythicMobs 生物
     * @param uuid 要设置仇恨值的实体 UUID
     * @param threat 仇恨值
     */
    void setThreat(Creature mob, UUID uuid, long threat);

    /**
     * 删除关于该 MM 怪物仇恨表中的某个实体
     * @param mob MythicMobs 生物
     * @param uuid 要删除的 UUID
     */
    void deleteThreat(Creature mob, UUID uuid);

    /**
     * 将该 UUID 设置为最高仇恨
     * 例如原最高仇恨为 100，则使用该方法后该 UUID 最高仇恨为 101，第二高的仇恨为 100 不变
     * @param mob MythicMobs 生物
     * @param uuid 要设置最高仇恨的 UUID
     */
    void topThreat(Creature mob, UUID uuid);

    /**
     * 获取该 MM 怪物仇恨表中仇恨值最高的 UUID
     * @param mob MythicMobs 生物
     * @return UUID
     */
    Optional<UUID> getTopThreat(Creature mob);

    /**
     * MM 怪物重新选择攻击目标，攻击目标为仇恨值最高的实体
     * @param mob MythicMobs 生物
     */
    void setTarget(Creature mob);

    /**
     * 清除关于该 UUID 的所有仇恨值记录
     * @param uuid UUID
     */
    void clearThreat(UUID uuid);

    /**
     * 删除该 MM 怪物的仇恨表
     * @param mobUid MythicMobs 生物 UUID
     */
    void removeMobThreat(UUID mobUid);

    /**
     * 获取该 MM 怪物的仇恨表
     * @param mob MythicMobs 生物
     * @return 仇恨表（克隆的副本，仅支持查操作
     */
    Map<UUID, Long> getMobThreatMap(Creature mob);
}

