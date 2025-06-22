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

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import org.bukkit.entity.Entity;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;

public class AbstractEntityAdapterImpl extends AbstractEntityAdapter<AbstractEntity> {
    public AbstractEntityAdapterImpl(AbstractEntity object) {
        super(object);
    }

    @Override
    public Entity getBukkitEntity() {
        return getActualObject().getBukkitEntity();
    }

    @Override
    public void setMetadata(String key, Object value) {
        getActualObject().setMetadata(key, value);
    }

    @Override
    public void removeMetadata(String key) {
        getActualObject().removeMetadata(key);
    }

    @Override
    public double getHealth() {
        return getActualObject().getHealth();
    }

    @Override
    public double getMaxHealth() {
        return getActualObject().getMaxHealth();
    }
}
