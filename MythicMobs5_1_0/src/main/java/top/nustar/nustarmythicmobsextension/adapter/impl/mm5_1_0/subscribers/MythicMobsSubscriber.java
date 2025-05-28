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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.subscribers;

import io.lumine.mythic.api.skills.targeters.ISkillTargeter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.idealstate.sugar.next.context.annotation.component.Subscriber;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.annotation.feature.DependsOn;
import team.idealstate.sugar.validate.annotation.NotNull;
import top.nustar.nustarmythicmobsextension.service.ConfigService;
import top.nustar.nustarmythicmobsextension.service.MechanicHelperService;
import top.nustar.nustarmythicmobsextension.service.TargetSelectorHelperService;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportMechanicType;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportTargetSelectorType;
import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;
import top.nustar.nustarmythicmobsextension.service.enums.MythicMobsVersion;
import top.nustar.nustarmythicmobsextension.service.enums.TargetSelectorType;

@Subscriber
@DependsOn(classes = "io.lumine.mythic.bukkit.utils.config.file.YamlConfiguration")
@SuppressWarnings({"unused"})
public class MythicMobsSubscriber implements Listener {
    private volatile ConfigService configService;
    private volatile Map<MechanicType, MechanicHelperService> mechanicHelperServiceMap;
    private volatile Map<TargetSelectorType, TargetSelectorHelperService> targetSelectorHelperServiceMap;

    @EventHandler
    public void onTargetLoad(MythicTargeterLoadEvent event) {
        TargetSelectorType targetSelectorType = TargetSelectorType.of(event.getTargeterName());
        if (targetSelectorType == null) return;
        SkillExecutor executor = MythicBukkit.inst().getSkillManager();
        event.register((ISkillTargeter)
                targetSelectorHelperServiceMap.get(targetSelectorType).findSelector(executor, event.getConfig()));
    }

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
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
        this.mechanicHelperServiceMap = mechanicHelperServices.stream()
                .filter(mechanicHelperService ->
                        mechanicHelperService.getClass().isAnnotationPresent(SupportMechanicType.class)
                                && mechanicHelperService
                                        .findMythicMobsVersionFromService(mechanicHelperService)
                                        .equals(MythicMobsVersion.MM5_1_0))
                .collect(Collectors.toMap(
                        mechanicHelperService ->
                                mechanicHelperService.findMechanicTypeFromService(mechanicHelperService),
                        Function.identity()));
        List<MechanicType> missingTypes = Arrays.stream(MechanicType.values())
                .filter(type -> !mechanicHelperServiceMap.containsKey(type))
                .collect(Collectors.toList());
        if (!missingTypes.isEmpty()) {
            throw new IllegalArgumentException("以下技能类型缺少对应的策略: " + missingTypes);
        }
    }

    @Autowired
    public void setTargetSelectorHelperServiceMap(List<TargetSelectorHelperService> targetSelectorHelperServices) {
        this.targetSelectorHelperServiceMap = targetSelectorHelperServices.stream()
                .filter(targetSelectorHelperService ->
                        targetSelectorHelperService.getClass().isAnnotationPresent(SupportTargetSelectorType.class)
                                && targetSelectorHelperService
                                        .findMythicMobsVersionFromService(targetSelectorHelperService)
                                        .equals(MythicMobsVersion.MM4_9_0))
                .collect(Collectors.toMap(
                        targetSelectorHelperService ->
                                targetSelectorHelperService.findSelectorTypeFromService(targetSelectorHelperService),
                        Function.identity()));
        List<TargetSelectorType> missingTypes = Arrays.stream(TargetSelectorType.values())
                .filter(type -> !targetSelectorHelperServiceMap.containsKey(type))
                .collect(Collectors.toList());
        if (!missingTypes.isEmpty()) {
            throw new IllegalArgumentException("以下选择器类型缺少对应的策略: " + missingTypes);
        }
    }
}
