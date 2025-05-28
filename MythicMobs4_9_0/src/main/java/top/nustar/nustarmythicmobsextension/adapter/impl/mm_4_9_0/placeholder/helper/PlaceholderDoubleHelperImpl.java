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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.placeholder.helper;

import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderDouble;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.placeholder.PlaceholderDoubleAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderDoubleAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderDoubleHelper;

public class PlaceholderDoubleHelperImpl implements PlaceholderDoubleHelper<PlaceholderDouble> {
    @Override
    public PlaceholderDoubleAdapter<PlaceholderDouble> of(String string) {
        return new PlaceholderDoubleAdapterImpl(PlaceholderDouble.of(string));
    }
}
