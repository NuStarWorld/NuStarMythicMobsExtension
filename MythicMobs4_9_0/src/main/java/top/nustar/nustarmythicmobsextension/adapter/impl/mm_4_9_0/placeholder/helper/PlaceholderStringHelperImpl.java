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

import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.placeholder.PlaceholderStringAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderStringAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderStringHelper;

public class PlaceholderStringHelperImpl implements PlaceholderStringHelper<PlaceholderString> {
    @Override
    public PlaceholderStringAdapter<PlaceholderString> of(String string) {
        PlaceholderStringAdapter<PlaceholderString> placeholderStringAdapter = new PlaceholderStringAdapterImpl(PlaceholderString.of(string));
        if (placeholderStringAdapter.getActualObject() == null) {
            return null;
        }
        return placeholderStringAdapter;
    }
}
