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

package top.nustar.nustarmythicmobsextension.controller;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.next.command.Command;
import team.idealstate.sugar.next.command.CommandResult;
import team.idealstate.sugar.next.command.annotation.CommandHandler;
import team.idealstate.sugar.next.context.Bean;
import team.idealstate.sugar.next.context.Context;
import team.idealstate.sugar.next.context.annotation.component.Controller;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.aware.ContextAware;
import team.idealstate.sugar.next.context.lifecycle.Initializable;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;
import top.nustar.nustarmythicmobsextension.NuStarMythicMobsExtension;
import top.nustar.nustarmythicmobsextension.api.ReloadedEvent;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.service.ConfigService;

@Controller(name = "nsme")
@SuppressWarnings({"unused"})
public class MMEController implements Command, ContextAware, Initializable {
    private volatile Context context;
    private volatile ConfigService configService;

    @CommandHandler
    @NotNull
    public CommandResult reload() {
        try {
            Bean<MainConfiguration> bean = context.getBean(MainConfiguration.class);
            Validation.notNull(bean, "未能获取到配置 Bean。");
            assert bean != null;
            configService.setMainConfiguration(bean.getInstance());
            Bukkit.getPluginManager().callEvent(new ReloadedEvent());
        } catch (Throwable e) {
            Log.error(e);
            return CommandResult.failure("未能完成配置重载，错误信息请查看日志输出。");
        }
        return CommandResult.success("已完成配置重载");
    }

    @Override
    public void initialize() {
        Metrics metrics = new Metrics(((NuStarMythicMobsExtension)context.getHolder()), 21687);
        if (((NuStarMythicMobsExtension)context.getHolder()).getServer().getPluginManager().isPluginEnabled("AttributePlus")) {
            metrics.addCustomChart(new SimplePie("attributeplugin", () -> "AttributePlus3"));
        }
        if (((NuStarMythicMobsExtension)context.getHolder()).getServer().getPluginManager().isPluginEnabled("SX-Attribute")) {
            metrics.addCustomChart(new SimplePie("attributeplugin", () -> "SX-Attribute2"));
        }
        Bukkit.getPluginManager().callEvent(new ReloadedEvent());
    }

    @Override
    public void setContext(@NotNull Context context) {
        this.context = context;
    }

    @Autowired
    public void setConfigService(@NotNull ConfigService configService) {
        this.configService = configService;
    }
}
