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

package top.nustar.nustarmythicmobsextension.adapter.impl.skills.mechanics;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import team.idealstate.sugar.next.calculate.Expression;
import top.nustar.nustarmythicmobsextension.exception.NSMMEException;

/** 仅处理倍率配置和数值，不读取或修改 AP 实体数据。 */
final class FastAPBaseMultiplier {
    private static final String BAM = "baseAttributeMultiple(bam)";
    private static final String BAML = "baseAttributeMultipleList(baml)";
    private final Expression globalExpression;
    private final Map<String, Expression> listedExpressions = new LinkedHashMap<>();

    FastAPBaseMultiplier(String global, String list, Function<String, String> normalize) {
        // 空列表没有倍率效果；完全未配置时也不引入变量上下文读取。
        boolean hasList = list != null && !list.isEmpty();
        globalExpression = global != null || hasList ? compile(BAM, global == null ? "1" : global) : null;
        if (hasList) {
            for (String entry : list.split(",", -1)) {
                String[] pair = entry.split(":", -1);
                if (pair.length != 2
                        || pair[0].trim().isEmpty()
                        || pair[1].trim().isEmpty()) {
                    throw new NSMMEException("FastAP 参数 " + BAML + " 格式错误：" + entry + "，应为 属性名:表达式");
                }
                String serverName = normalize.apply(pair[0].trim());
                if (serverName == null) {
                    throw new NSMMEException("FastAP 参数 " + BAML + " 包含未知属性：" + pair[0]);
                }
                if (listedExpressions.containsKey(serverName)) {
                    throw new NSMMEException("FastAP 参数 " + BAML + " 属性归一化后重复：" + entry + " -> " + serverName);
                }
                listedExpressions.put(serverName, compile(BAML + "[" + serverName + "]", pair[1]));
            }
        }
    }

    /** 每目标单线程使用；真实 Expression 只在变量 token 上 get，常量不触发变量读取。 */
    static Map<String, Number> lazyContext(Supplier<Map<String, Number>> supplier) {
        return new AbstractMap<String, Number>() {
            private Map<String, Number> context;

            private Map<String, Number> materialize() {
                if (context == null) {
                    context = supplier.get();
                }
                return context;
            }

            @Override
            public Number get(Object key) {
                return materialize().get(key);
            }

            @Override
            public Set<Entry<String, Number>> entrySet() {
                return materialize().entrySet();
            }
        };
    }

    boolean isConfigured() {
        return globalExpression != null;
    }

    Values evaluate(Map<String, Number> context) {
        double global = calculate(BAM, globalExpression, context);
        Map<String, Double> effective = new LinkedHashMap<>();
        // 即使全局为 0，也逐项求值并校验；恒等判定与后续缩放只复用此次结果。
        for (Map.Entry<String, Expression> entry : listedExpressions.entrySet()) {
            String parameter = BAML + "[" + entry.getKey() + "]";
            double local = calculate(parameter, entry.getValue(), context);
            double multiple = global * local;
            if (!Double.isFinite(multiple)) {
                throw new NSMMEException("FastAP 参数 " + BAM + " * " + parameter + " 乘积非有限：" + global + " * " + local);
            }
            effective.put(entry.getKey(), multiple);
        }
        return new Values(global, effective);
    }

    private static Expression compile(String parameter, String source) {
        if (source.trim().isEmpty()) {
            throw new NSMMEException("FastAP 参数 " + parameter + " 表达式不能为空");
        }
        try {
            return new Expression(source).compile();
        } catch (RuntimeException ex) {
            throw expressionFailure(parameter, "编译", ex);
        }
    }

    private static double calculate(String parameter, Expression expression, Map<String, Number> context) {
        Number result;
        try {
            result = expression.calculate(context);
        } catch (RuntimeException ex) {
            throw expressionFailure(parameter, "求值", ex);
        }
        // BigDecimal 的极小负数转 double 会下溢为 -0.0，必须在转换前检查符号。
        if (result instanceof BigDecimal && ((BigDecimal) result).signum() < 0) {
            throw new NSMMEException("FastAP 参数 " + parameter + " 必须是非负有限数，实际为：" + result);
        }
        double value = result.doubleValue();
        if (!Double.isFinite(value) || value < 0) {
            throw new NSMMEException("FastAP 参数 " + parameter + " 必须是非负有限数，实际为：" + value);
        }
        return value;
    }

    private static NSMMEException expressionFailure(String parameter, String action, RuntimeException cause) {
        NSMMEException failure =
                new NSMMEException("FastAP 参数 " + parameter + " 表达式" + action + "失败：" + cause.getMessage());
        failure.initCause(cause);
        return failure;
    }

    /** 每个目标独立的已求值倍率；不持有实体或原属性数组。 */
    static final class Values {
        private final double global;
        private final Map<String, Double> effective;

        private Values(double global, Map<String, Double> effective) {
            this.global = global;
            this.effective = effective;
        }

        private double multiple(String serverName) {
            return effective.getOrDefault(serverName, global);
        }

        boolean isIdentity(Collection<String> serverNames) {
            for (String serverName : serverNames) {
                if (multiple(serverName) != 1.0) {
                    return false;
                }
            }
            return true;
        }

        Number[] scale(String serverName, Number[] basis) {
            double multiple = multiple(serverName);
            double low = basis[0].doubleValue() * multiple;
            double high = basis[1].doubleValue() * multiple;
            if (!Double.isFinite(low) || !Double.isFinite(high)) {
                throw new NSMMEException("FastAP 参数 " + BAM + " / " + BAML + "[" + serverName + "] 缩放基础区间后非有限：["
                        + basis[0] + ", " + basis[1] + "] * " + multiple);
            }
            // AP 的 force 读取可能直接返回内部数组，绝不在原数组上乘倍率。
            return new Number[] {low, high};
        }
    }
}
