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

import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.UUID;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.ActiveMobAdapter;

/**
 * @author : NuStar Date : 2025/6/24 20:09 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
public class ActiveMobAdapterImpl extends ActiveMobAdapter<ActiveMob> {
    public ActiveMobAdapterImpl(ActiveMob actualObject) {
        super(actualObject);
    }

    @Override
    public String getMobType() {
        return getActualObject().getMobType();
    }

    @Override
    public AbstractEntityAdapter<?> getParent() {
        return new AbstractEntityAdapterImpl(getActualObject().getParent().getEntity());
    }

    @Override
    public UUID getOwner() {
        return getActualObject().getOwner().orElse(null);
    }

    @Override
    public void setLastDamageSkillAmount(double damage) {
        getActualObject().setLastDamageSkillAmount(damage);
    }
}
