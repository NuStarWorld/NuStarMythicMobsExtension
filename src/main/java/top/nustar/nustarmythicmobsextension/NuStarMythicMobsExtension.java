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

package top.nustar.nustarmythicmobsextension;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import team.idealstate.minecraft.next.spigot.api.SpigotPlugin;
import team.idealstate.sugar.next.boot.jackson.annotation.EnableJacksonYaml;
import team.idealstate.sugar.next.context.Context;
import team.idealstate.sugar.validate.annotation.NotNull;
import top.nustar.minecraft.next.spigot.nms.api.annotation.EnableNms;


@EnableJacksonYaml
@EnableNms
public class NuStarMythicMobsExtension extends SpigotPlugin {

    @Override
    public void onInitialize(@NotNull Context context) {}

    @Override
    public void onInitialized(@NotNull Context context) {}

    @Override
    public void onLoad(@NotNull Context context) {
        Plugin attributePlus = Bukkit.getPluginManager().getPlugin("AttributePlus");
        String version = attributePlus.getDescription().getVersion();
        context.registerProperty("plugin-version:attribute-plus", version);
        context.registerProperty("server-version:bukkit", Bukkit.getBukkitVersion());
    }

    @Override
    public void onLoaded(@NotNull Context context) {}

    @Override
    public void onEnable(@NotNull Context context) {}

    @Override
    public void onEnabled(@NotNull Context context) {}

    @Override
    public void onDisable(@NotNull Context context) {}

    @Override
    public void onDisabled(@NotNull Context context) {}

    @Override
    public void onDestroy(@NotNull Context context) {}

    @Override
    public void onDestroyed(@NotNull Context context) {}
}
