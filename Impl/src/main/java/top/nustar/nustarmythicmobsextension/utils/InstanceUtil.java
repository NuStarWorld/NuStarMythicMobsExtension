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

package top.nustar.nustarmythicmobsextension.utils;

import org.bukkit.plugin.Plugin;
import team.idealstate.sugar.next.context.ContextHolder;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import team.idealstate.sugar.next.context.aware.ContextHolderAware;
import team.idealstate.sugar.validate.annotation.NotNull;

@Component
@Scope(Scope.SINGLETON)
public class InstanceUtil implements ContextHolderAware {
    private static volatile ContextHolder contextHolder;

    @Override
    public void setContextHolder(@NotNull ContextHolder contextHolder) {
        InstanceUtil.contextHolder = contextHolder;
    }

    public static <T> T getInstance(Class<T> clazz) {
        return clazz.cast(contextHolder);
    }

    public static String getVersion() {
        return ((Plugin)contextHolder).getServer().getVersion();
    }

}
