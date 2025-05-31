package top.nustar.nustarmythicmobsextension.manager;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.entity.AttributeSourceInstance;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(Scope.SINGLETON)
public class AttributeSourceManager {
    private final Map<UUID, AttributeSourceInstance> attributeSourceInstanceMap = new ConcurrentHashMap<>();

    public AttributeSourceManager() {
        setAttributeSourceManager(this);
    }

    public void addAttributeSourceInstance(LivingEntity entity, String sourceName, int time) {
        attributeSourceInstanceMap.computeIfPresent(entity.getUniqueId(), (key, value) -> {
            value.addSource(sourceName, time);
            return value;
        });
        attributeSourceInstanceMap.computeIfAbsent(entity.getUniqueId(), key -> {
            AttributeSourceInstance attributeSourceInstance = new AttributeSourceInstance(entity);
            attributeSourceInstance.addSource(sourceName, time);
            return attributeSourceInstance;
        });
    }

    public void removeAttributeSourceInstance(UUID uuid) {
        attributeSourceInstanceMap.computeIfPresent(uuid, (key, value) -> {
            value.removeDetails();
            return value;
        });
        attributeSourceInstanceMap.remove(uuid);
    }

    @Getter
    private static volatile AttributeSourceManager attributeSourceManager;

    public void setAttributeSourceManager(AttributeSourceManager attributeSourceManager) {
        AttributeSourceManager.attributeSourceManager = attributeSourceManager;
    }

}
