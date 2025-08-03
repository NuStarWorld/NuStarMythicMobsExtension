package top.nustar.nustarmythicmobsextension.entity;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import top.nustar.nustarmythicmobsextension.utils.DebugUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author : NuStar
 * Date : 2025/6/23 22:27
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Data
public class MobThreat {
    private final Map<UUID, Long> entityThreatMap = new HashMap<>();

    private final Creature mob;

    public MobThreat(Creature mob) {
        this.mob = mob;
    }

    /**
     * 添加实体威胁度
     * @param uuid 实体UUID
     * @param thread 威胁值
     */
    public void addEntityThreat(UUID uuid, long thread) {
        entityThreatMap.compute(uuid, (key, value) -> {
            long newValue = Math.max(0, thread);
            if (value == null) {
                return newValue;
            }
            return value + newValue;
        });
        DebugUtil.debug(this.toString());
    }

    /**
     * 设置实体威胁度
     * @param uuid 实体UUID
     * @param thread 威胁值
     */
    public void setEntityThreat(UUID uuid, long thread) {
        long newValue = Math.max(0, thread);
        entityThreatMap.put(uuid, newValue);
        DebugUtil.debug(this.toString());
    }

    public void removeEntityThreat(UUID uuid) {
        entityThreatMap.remove(uuid);
        DebugUtil.debug(this.toString());
    }

    /**
     * 获取当前最高威胁度的UUID
     * @return UUID
     */
    public Optional<UUID> getTopThreat() {
        return entityThreatMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    /**
     * 设置当前最高威胁度的UUID
     * @param uuid UUID
     */
    public void setTopThreat(UUID uuid) {
        long topThreat = entityThreatMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getValue)
                .orElse(0L);
        entityThreatMap.put(uuid, topThreat + 1);
        DebugUtil.debug(this.toString());
    }

    /**
     * 将一个实体的威胁度转移给另一个实体
     * @param from 实体
     * @param to 目标实体
     */
    public void transferThreat(UUID from, UUID to) {
        long threat = entityThreatMap.getOrDefault(from, 0L) + entityThreatMap.getOrDefault(to, 0L);
        entityThreatMap.put(from, 0L);
        entityThreatMap.put(to, threat);
        DebugUtil.debug(this.toString());
    }

    public void setTarget() {
        getTopThreat().ifPresent(topThreat -> {
            Entity entity = Bukkit.getEntity(topThreat);
            if (!(entity instanceof LivingEntity)) return;
            mob.setTarget((LivingEntity) entity);
        });
    }
}
