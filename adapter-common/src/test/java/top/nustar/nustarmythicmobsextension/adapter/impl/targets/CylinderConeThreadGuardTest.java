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

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ResourceLock("Bukkit.server")
@DisplayName("圆柱扇形同步线程守卫")
class CylinderConeThreadGuardTest {
    @ParameterizedTest(name = "Bukkit 主线程状态：{0}")
    @ValueSource(booleans = {true, false})
    void rejectsAsyncWithoutScheduling(boolean primary) throws ReflectiveOperationException {
        Field field = Bukkit.class.getDeclaredField("server");
        field.setAccessible(true);
        Object previous = field.get(null);
        AtomicInteger checks = new AtomicInteger();
        Server server = (Server) Proxy.newProxyInstance(
                Server.class.getClassLoader(), new Class<?>[] {Server.class}, (proxy, method, args) -> {
                    if ("isPrimaryThread".equals(method.getName())) {
                        checks.incrementAndGet();
                        return primary;
                    }
                    throw new AssertionError("不得调度、等待或访问其他 Bukkit 状态：" + method);
                });
        field.set(null, server);
        try {
            if (primary) {
                assertDoesNotThrow(CylinderConeSelectorAdapter::requirePrimaryThread);
            } else {
                IllegalStateException error =
                        assertThrows(IllegalStateException.class, CylinderConeSelectorAdapter::requirePrimaryThread);
                assertEquals(IllegalStateException.class, error.getClass());
                assertTrue(error.getMessage().contains("主线程"));
            }
            assertEquals(1, checks.get());
        } finally {
            field.set(null, previous);
        }
    }
}
