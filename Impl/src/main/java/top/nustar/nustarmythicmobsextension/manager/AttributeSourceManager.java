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

package top.nustar.nustarmythicmobsextension.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.entity.AttributeSourceInstance;

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
