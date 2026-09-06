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

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/** 只支持选择器约定调用；其他实体读取立即使测试失败，不提供默认兜底。 */
final class BukkitCylinderFixtures {
    final List<Entity> candidates = new ArrayList<>();
    final List<Entity> nearbyCandidates = new ArrayList<>();
    boolean strictAabb;
    int queries;
    Object[] queryArguments;
    final World world = (World) Proxy.newProxyInstance(
            World.class.getClassLoader(), new Class<?>[] {World.class}, (proxy, method, args) -> {
                switch (method.getName()) {
                    case "getNearbyEntities":
                        if (args.length != 4 || !(args[0] instanceof Location)) {
                            throw new AssertionError("必须使用 Location 和三个半径的粗筛重载");
                        }
                        queries++;
                        queryArguments = args;
                        if (!strictAabb) return candidates;
                        nearbyCandidates.clear();
                        for (Entity candidate : candidates) {
                            if (intersects(
                                    candidate.getLocation(),
                                    (Location) args[0],
                                    (double) args[1],
                                    (double) args[2],
                                    (double) args[3])) {
                                nearbyCandidates.add(candidate);
                            }
                        }
                        return nearbyCandidates;
                    case "equals":
                        return proxy == args[0];
                    case "hashCode":
                        return System.identityHashCode(proxy);
                    case "toString":
                        return "测试世界";
                    default:
                        throw new AssertionError("不允许的世界读取：" + method);
                }
            });

    /** 模拟普通生物宽 0.6、高 1.8、minY=脚部的严格 AABB 相交，不代替实服验证。 */
    private static boolean intersects(Location feet, Location center, double x, double y, double z) {
        return center.getX() - x < feet.getX() + 0.3
                && center.getX() + x > feet.getX() - 0.3
                && center.getY() - y < feet.getY() + 1.8
                && center.getY() + y > feet.getY()
                && center.getZ() - z < feet.getZ() + 0.3
                && center.getZ() + z > feet.getZ() - 0.3;
    }

    <T extends Entity> T entity(Class<T> type, Location location) {
        return entity(type, UUID.randomUUID(), location);
    }

    <T extends Entity> T entity(Class<T> type, UUID id, Location location) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (proxy, method, args) -> {
            switch (method.getName()) {
                case "getUniqueId":
                    return id;
                case "getLocation":
                    return location;
                case "equals":
                    return proxy == args[0];
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "toString":
                    return type.getSimpleName() + ":" + id;
                default:
                    throw new AssertionError("不允许的实体读取：" + method);
            }
        }));
    }

    Location position(double x, double y, double z) {
        return new Location(world, x, y, z);
    }
}
