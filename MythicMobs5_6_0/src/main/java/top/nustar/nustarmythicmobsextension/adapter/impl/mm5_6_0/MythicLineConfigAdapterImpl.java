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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0;

import io.lumine.mythic.api.config.MythicLineConfig;
import lombok.NonNull;
import top.nustar.nustarmythicmobsextension.adapter.MythicLineConfigAdapter;

public class MythicLineConfigAdapterImpl extends MythicLineConfigAdapter<MythicLineConfig> {
    public MythicLineConfigAdapterImpl(@NonNull MythicLineConfig actualObject) {
        super(actualObject);
    }

    @Override
    public String getString(String[] key) {
        return getActualObject().getString(key);
    }

    @Override
    public boolean getBoolean(String[] key, boolean def) {
        return getActualObject().getBoolean(key, def);
    }

    @Override
    public double getDouble(String[] key) {
        return getActualObject().getDouble(key, 1.0);
    }
}
