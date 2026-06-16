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

package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.targets;

import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCaster;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderDouble;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderFloat;
import io.lumine.xikage.mythicmobs.skills.targeters.ILocationSelector;
import java.util.HashSet;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarTargerSelector;

/**
 * @author : NuStar Date : 2025/6/20 21:38 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
public class SuperForwardTargeter extends ILocationSelector implements NuStarTargerSelector {
    protected PlaceholderDouble forward;
    protected PlaceholderFloat rotate;
    protected boolean useEyeLocation;
    protected boolean lockPitch;
    protected PlaceholderDouble yOffset;
    protected PlaceholderDouble xOffset;
    protected PlaceholderDouble zOffset;

    public SuperForwardTargeter(MythicLineConfig mlc) {
        super(mlc);
        this.forward = mlc.getPlaceholderDouble(new String[] {"forward", "f", "amount", "a"}, 5.0D);
        this.rotate = mlc.getPlaceholderFloat(new String[] {"rotate", "rot"}, 0.0F);
        this.useEyeLocation = mlc.getBoolean(new String[] {"useeyelocation", "uel"}, false);
        this.lockPitch = mlc.getBoolean(new String[] {"lockpitch"}, false);
        this.yOffset = mlc.getPlaceholderDouble(new String[] {"yoffset", "y"}, 0.0D);
        this.xOffset = mlc.getPlaceholderDouble(new String[] {"xoffset", "x"}, 0.0D);
        this.zOffset = mlc.getPlaceholderDouble(new String[] {"zoffset", "z"}, 0.0D);
    }

    @Override
    public HashSet<AbstractLocation> getLocations(SkillMetadata skillMetadata) {
        SkillCaster caster = skillMetadata.getCaster();
        AbstractLocation location;
        double forward = this.forward.get(skillMetadata);
        float rotate = this.rotate.get(skillMetadata);
        HashSet<AbstractLocation> locations = new HashSet<>();
        if (this.useEyeLocation) {
            location = caster.getEntity().getEyeLocation();
        } else {
            location = caster.getLocation();
        }
        if (this.lockPitch) {
            location.setPitch(0.0F);
        }
        if (rotate != 0.0F) {
            location.add(location.getDirection().rotate(rotate).normalize().multiply(forward));
        } else {
            location.add(location.getDirection().normalize().multiply(forward));
        }
        location.add(this.xOffset.get(skillMetadata), this.yOffset.get(skillMetadata), this.zOffset.get(skillMetadata));
        locations.add(location);
        return locations;
    }
}
