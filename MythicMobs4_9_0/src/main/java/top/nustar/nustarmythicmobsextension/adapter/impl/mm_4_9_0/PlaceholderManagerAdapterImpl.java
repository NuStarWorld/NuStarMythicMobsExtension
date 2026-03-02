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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0;

import io.lumine.xikage.mythicmobs.skills.placeholders.Placeholder;
import io.lumine.xikage.mythicmobs.skills.placeholders.PlaceholderManager;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderAdapter;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderManagerAdapter;

/**
 * @author : NuStar Date : 2025/7/1 19:42 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
public class PlaceholderManagerAdapterImpl extends PlaceholderManagerAdapter<PlaceholderManager> {
    public PlaceholderManagerAdapterImpl(PlaceholderManager actualObject) {
        super(actualObject);
    }

    @Override
    public void register(String key, PlaceholderAdapter<?> transformer) {
        getActualObject().register(key, (Placeholder) transformer.getActualObject());
    }
}
