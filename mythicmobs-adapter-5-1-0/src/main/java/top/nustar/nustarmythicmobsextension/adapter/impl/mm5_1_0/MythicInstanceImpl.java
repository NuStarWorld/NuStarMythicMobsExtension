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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.core.mobs.MobExecutor;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.adapter.MobManagerAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicInstance;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderManagerAdapter;
import top.nustar.nustarmythicmobsextension.service.annotations.MythicMobs5_1_0;

/**
 * @author : NuStar Date : 2025/6/24 20:13 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
@Component
@Scope(Scope.SINGLETON)
@MythicMobs5_1_0
@SuppressWarnings("unused")
public class MythicInstanceImpl implements MythicInstance {
    private final MythicPlugin instance;

    public MythicInstanceImpl() {
        this.instance = MythicProvider.get();
    }

    @Override
    public MobManagerAdapter<?> getMobManager() {
        return new MobManagerAdapterImpl((MobExecutor) instance.getMobManager());
    }

    @Override
    public PlaceholderManagerAdapter<?> getPlaceholderManager() {
        return new PlaceholderManagerAdapterImpl(instance.getPlaceholderManager());
    }
}
