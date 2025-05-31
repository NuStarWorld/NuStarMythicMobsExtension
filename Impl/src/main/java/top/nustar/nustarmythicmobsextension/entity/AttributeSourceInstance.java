package top.nustar.nustarmythicmobsextension.entity;

import lombok.Data;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.serverct.ersha.api.AttributeAPI;
import top.nustar.nustarmythicmobsextension.utils.InstanceUtil;

import java.util.HashMap;
import java.util.Map;

public class AttributeSourceInstance {
    private final LivingEntity entity;
    private final Map<String, Detail> detailMap = new HashMap<>();

    public AttributeSourceInstance(LivingEntity entity) {
        this.entity = entity;
    }
    public void addSource(String sourceName, int time) {
        detailMap.computeIfPresent(sourceName, (key,value) -> {
            if (value.isDied) {
                return new Detail(entity, sourceName, time);
            }
            value.setEndTime(System.currentTimeMillis() + time * 1000L);
            return value;
        });
        detailMap.computeIfAbsent( sourceName, key -> new Detail(entity, sourceName, System.currentTimeMillis() + time * 1000L));
    }
    public void removeDetails() {
        detailMap.forEach((key, value) -> {
            if (value != null && !value.task.isCancelled()) {
                value.task.cancel();
            }
        });
        detailMap.clear();
    }
    @Data
    static class Detail {
        private final String sourceName;
        private volatile long endTime;
        private volatile boolean isDied = false;
        private final LivingEntity entity;
        private BukkitRunnable task;
        public Detail(LivingEntity entity, String sourceName, long time) {
             this.sourceName = sourceName;
             this.endTime = time;
             this.entity = entity;
             task = new BukkitRunnable() {
                 @Override
                 public void run() {
                     if (isDied) {
                         cancel();
                     }
                     if (System.currentTimeMillis() >= endTime) {
                         AttributeAPI.takeSourceAttribute(AttributeAPI.getAttrData(entity), sourceName);
                         isDied = true;
                         cancel();
                     }
                 }
             };
             task.runTaskTimerAsynchronously( InstanceUtil.getInstance(Plugin.class), 0L, 20L);
        }
    }
}
