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

/** 一次施法的已校验数值快照；纯几何计算不访问 Bukkit 或占位符。 */
public final class CylinderConeGeometry {
    private static final double ANGLE_EPSILON = 1.0E-12;
    private final double radius;
    private final double halfAngle;
    private final double cosine;
    private final double rotation;
    private final double down;
    private final double up;
    private final double close;

    CylinderConeGeometry(double radius, double halfAngle, double rotation, double down, double up, double close) {
        this.radius = radius;
        this.halfAngle = halfAngle;
        this.cosine = Math.cos(Math.toRadians(halfAngle));
        // 先取模再换算，避免合法巨大有限旋转在度数转换中失去精度。
        this.rotation = Math.toRadians(rotation % 360.0);
        this.down = down;
        this.up = up;
        this.close = close;
    }

    /** 粗筛使用已包含 padding 的水平范围。 */
    double horizontalRange() {
        return radius;
    }

    /** 对称方盒覆盖不对称高度，最终上下边界仍由 contains 精判。 */
    double verticalSearchRange() {
        return Math.max(down, up);
    }

    /** 返回归一化水平朝向，正角向施法者右侧旋转。 */
    double[] direction(double x, double z, double yaw) {
        double length = Math.hypot(x, z);
        // 等价于水平长度平方小于 1e-6，垂直朝向仍保留施法者 yaw。
        if (length < 1.0E-3) {
            double radians = Math.toRadians(yaw % 360.0);
            x = -Math.sin(radians);
            z = Math.cos(radians);
        } else {
            x /= length;
            z /= length;
        }
        double sin = Math.sin(rotation);
        double cos = Math.cos(rotation);
        return new double[] {x * cos - z * sin, x * sin + z * cos};
    }

    boolean contains(double dx, double dy, double dz, double forwardX, double forwardZ) {
        // 不平方大数，也不对半径/高度加容差，确保实体位置点的边界语义。
        double distance = Math.hypot(dx, dz);
        if (!(distance <= radius && dy >= -down && dy <= up)) {
            return false;
        }
        // close 不额外加 padding；重叠点避免后面的零除，二者均不越过圆柱范围。
        if (distance == 0 || distance <= close || halfAngle == 180) {
            return true;
        }
        // 保留点积符号，使 90..180 度不会像 cos² 比较那样折返。
        double dot = (dx / distance) * forwardX + (dz / distance) * forwardZ;
        return dot + ANGLE_EPSILON >= cosine;
    }
}
