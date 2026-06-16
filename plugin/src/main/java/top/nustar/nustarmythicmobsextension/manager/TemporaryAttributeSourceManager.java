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
public class TemporaryAttributeSourceManager {
    private final Map<UUID, AttributeSourceInstance> attributeSourceInstanceMap = new ConcurrentHashMap<>();

    public TemporaryAttributeSourceManager() {
        setTemporaryAttributeSourceManager(this);
    }

    /**
     * 给实体新增一个新的临时属性源
     * @param entity 实体
     * @param sourceName 属性源名称
     * @param time 持续时间
     */
    public void addAttributeSourceInstance(LivingEntity entity, String sourceName, int time) {
        attributeSourceInstanceMap.compute(entity.getUniqueId(), (key, value) -> {
            if (value == null) {
                AttributeSourceInstance attributeSourceInstance = new AttributeSourceInstance(entity);
                attributeSourceInstance.addSource(sourceName, time);
                return attributeSourceInstance;
            } else {
                value.addSource(sourceName, time);
                return value;
            }
        });
    }

    /**
     * 移除一个临时属性源
     * @param entityId 实体 UUID
     * @param sourceName 属性源名称
     * @param isStartWith 是否以...开头匹配
     */
    public void removeAttributeSourceInstance(UUID entityId, String sourceName, boolean isStartWith) {
        attributeSourceInstanceMap.computeIfPresent(entityId, (key, value) -> {
            value.removeDetail(sourceName, isStartWith);
            return value;
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
    private static volatile TemporaryAttributeSourceManager temporaryAttributeSourceManager;

    public void setTemporaryAttributeSourceManager(TemporaryAttributeSourceManager temporaryAttributeSourceManager) {
        TemporaryAttributeSourceManager.temporaryAttributeSourceManager = temporaryAttributeSourceManager;
    }
}
