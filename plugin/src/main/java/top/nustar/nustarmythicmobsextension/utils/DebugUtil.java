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

import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.service.ConfigService;

/**
 * @author : NuStar Date : 2025/6/25 23:27 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
@Component
@Scope(Scope.SINGLETON)
@SuppressWarnings({"unused"})
public class DebugUtil {
    private static volatile ConfigService configService;

    @Autowired
    public void setConfigService(ConfigService configService) {
        DebugUtil.configService = configService;
    }

    public static void debug(String msg) {
        if (configService.getMainConfiguration().isDebug()) {
            Log.info(String.format("[debug] %s", msg));
        }
    }
}
