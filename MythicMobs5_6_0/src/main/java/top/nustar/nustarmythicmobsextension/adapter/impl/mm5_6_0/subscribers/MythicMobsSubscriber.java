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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.subscribers;

import io.lumine.mythic.api.skills.targeters.ISkillTargeter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.idealstate.sugar.next.context.annotation.component.Subscriber;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.validate.annotation.NotNull;
import top.nustar.nustarmythicmobsextension.adapter.MythicInstance;
import top.nustar.nustarmythicmobsextension.manager.AttributeSourceManager;
import top.nustar.nustarmythicmobsextension.service.*;
import top.nustar.nustarmythicmobsextension.service.annotations.*;
import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;
import top.nustar.nustarmythicmobsextension.service.enums.PlaceholderType;
import top.nustar.nustarmythicmobsextension.service.enums.TargetSelectorType;

import java.util.List;
import java.util.Map;

@Subscriber
@MythicMobs5_6_0
@SuppressWarnings({"unused"})
public class MythicMobsSubscriber implements Listener {
    private volatile ConfigService configService;
    private volatile MythicInstance mythicInstance;
    private volatile Map<MechanicType, MechanicHelperService> mechanicHelperServiceMap;
    private volatile Map<TargetSelectorType, TargetSelectorHelperService> targetSelectorHelperServiceMap;
    private volatile Map<PlaceholderType, PlaceholderService> placeholderServiceMap;
    private final AttributeSourceManager attributeSourceManager = AttributeSourceManager.getAttributeSourceManager();

    @EventHandler
    public void on(MythicMobDeathEvent event) {
        attributeSourceManager.removeAttributeSourceInstance(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void on(MythicTargeterLoadEvent event) {
        TargetSelectorType targetSelectorType = TargetSelectorType.of(event.getTargeterName());
        if (targetSelectorType == null) return;
        SkillExecutor executor = MythicBukkit.inst().getSkillManager();
        event.register((ISkillTargeter)
                targetSelectorHelperServiceMap.get(targetSelectorType).findSelector(executor, event.getConfig()));
    }

    @EventHandler
    public void on(MythicMechanicLoadEvent event) {
        for (Map.Entry<PlaceholderType, PlaceholderService> entry : placeholderServiceMap.entrySet()) {
            mythicInstance.getPlaceholderManager().register(entry.getKey().getName(), entry.getValue().getPlaceholderAdapter());
        }
        MechanicType mechanicType = MechanicType.of(event.getMechanicName());
        if (mechanicType == null) return;
        SkillExecutor executor = MythicBukkit.inst().getSkillManager();
        event.register((SkillMechanic) mechanicHelperServiceMap
                .get(mechanicType)
                .findMechanic(executor, event.getConfig(), configService.getMainConfiguration()));
    }

    @Autowired
    public void setConfigService(@NotNull ConfigService configService) {
        this.configService = configService;
    }

    @Autowired
    public void setMechanicHelperServiceMap(List<MechanicHelperService> mechanicHelperServices) {
        this.mechanicHelperServiceMap = TypeService.buildServiceMap(mechanicHelperServices);
    }

    @Autowired
    public void setTargetSelectorHelperServiceMap(List<TargetSelectorHelperService> targetSelectorHelperServices) {
        this.targetSelectorHelperServiceMap = TypeService.buildServiceMap(targetSelectorHelperServices);
    }

    @Autowired
    public void setPlaceholderServiceMap(List<PlaceholderService> placeholderServices) {
        this.placeholderServiceMap = TypeService.buildServiceMap(placeholderServices);
    }

    @Autowired
    public void setMythicInstance(MythicInstance mythicInstance) {
        this.mythicInstance = mythicInstance;
    }
}
