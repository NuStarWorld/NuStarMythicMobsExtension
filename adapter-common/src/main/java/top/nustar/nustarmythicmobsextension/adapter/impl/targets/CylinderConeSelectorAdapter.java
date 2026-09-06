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

package top.nustar.nustarmythicmobsextension.adapter.impl.targets;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import team.idealstate.sugar.logging.Log;

/** 三版共用的 Bukkit 候选查询；版本层只负责元数据及实体转换。 */
public final class CylinderConeSelectorAdapter {
    private CylinderConeSelectorAdapter() {}

    /** 必须作为各版本 getEntities 的第一条语句，早于元数据和占位符读取。 */
    public static void requirePrimaryThread() {
        if (!Bukkit.isPrimaryThread()) {
            IllegalStateException error = new IllegalStateException("NuStarCylinderCone 必须在 Bukkit 主线程执行，禁止异步读取实体或占位符");
            Log.error(error);
            throw error;
        }
    }

    /** 调用方先检查线程；传入的位置只读，绝不改变 origin 或施法者位置。 */
    public static <T> HashSet<T> select(
            CylinderConeConfig<?> config,
            CylinderConeGeometry geometry,
            Entity caster,
            Location casterLocation,
            Location center,
            Function<Entity, T> adapter) {
        Vector facing = casterLocation.getDirection();
        double[] forward = geometry.direction(facing.getX(), facing.getZ(), casterLocation.getYaw());
        double x = center.getX();
        double y = center.getY();
        double z = center.getZ();
        UUID casterId = caster.getUniqueId();
        HashSet<T> result = new HashSet<>();
        // Bukkit 方盒严格相交，粗筛略越过闭边界；contains 仍只使用原始半径和高度。
        double radius = geometry.horizontalRange();
        double searchX = searchRange("x", x, radius);
        double searchY = searchRange("y", y, geometry.verticalSearchRange());
        double searchZ = searchRange("z", z, radius);
        for (Entity candidate : center.getWorld().getNearbyEntities(center, searchX, searchY, searchZ)) {
            if (!(candidate instanceof LivingEntity)
                    || casterId.equals(candidate.getUniqueId())
                    || (config.ignoresPlayers() && candidate instanceof Player)
                    || (config.ignoresArmorStands() && candidate instanceof ArmorStand)) {
                continue;
            }
            Location position = candidate.getLocation();
            if (geometry.contains(
                    position.getX() - x, position.getY() - y, position.getZ() - z, forward[0], forward[1])) {
                result.add(adapter.apply(candidate));
            }
        }
        // MM 随后的自动 filter 会修改集合，因此返回可变、去重的 HashSet。
        return result;
    }

    /** 从绝对坐标边界向外取一个浮点数，零范围也能越过中心坐标的 ULP。 */
    static double searchRange(String axis, double center, double range) {
        double min = center - range;
        double max = center + range;
        double expanded = Math.max(range, Math.max(center - Math.nextDown(min), Math.nextUp(max) - center));
        // 反算半尺寸和重建端点可能各舍入一次，只再向外取一步，不循环放大范围。
        if (!(center - expanded < min && center + expanded > max)) {
            expanded = Math.nextUp(expanded);
        }
        double queryMin = center - expanded;
        double queryMax = center + expanded;
        if (!Double.isFinite(expanded)
                || !Double.isFinite(queryMin)
                || !Double.isFinite(queryMax)
                || !(queryMin < min && queryMax > max)) {
            IllegalStateException error = new IllegalStateException("NuStarCylinderCone 派生查询范围 " + axis + ": center=["
                    + center + "], range=[" + range + "], expanded=[" + expanded + "]; 无法构造有限且严格覆盖闭边界的方盒");
            Log.error(error);
            throw error;
        }
        return expanded;
    }
}
