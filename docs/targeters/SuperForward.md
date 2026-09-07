[返回目标选择器列表](README.md)

# SuperForward：取施法者前方的一个位置

## 用途

`@SuperForward` 给出一个**位置目标**，适合放粒子、声音等位置效果。它不寻找前方实体，不判定命中，也不检查位置是否被墙挡住。

## 准备

**当前只为旧版 MM 提供这项功能，5.x 不要使用。** 项目的 4.9.0 版本接入有实现，5.1.0、5.6.0 接入没有；使用前仍需确认你的服务器版本，不能靠改名称解决不支持的问题。

使用支持位置目标的技能，例如原生粒子效果；不要把返回位置当成可直接伤害的实体。没有额外 MM 威胁表设置要求。先在隔离测试服观察，本文未做实服验证。

## 参数

| 参数 | 简写或其他写法 | 不填写时 | 填写说明与效果 |
| --- | --- | --- | --- |
| `forward` | `f`、`amount`、`a` | `5` | 填数字，单位方块；沿计算出的视线方向前进多远，负数反向。未锁俯仰时是斜线长度，不是水平距离。 |
| `rotate` | `rot` | `0` | 填数字，单位度；绕世界竖直轴旋转移动方向，正数向右、负数向左，不是角度范围。 |
| `useeyelocation` | `uel` | `false` | 填 `true/false`；`true` 从眼睛位置起算，`false` 从施法者位置起算。 |
| `lockpitch` | 无 | `false` | 填 `true/false`；`true` 先把用于计算的位置俯仰角设为 0，前进方向保持水平；不取消后面的 `yoffset`。 |
| `yoffset` | `y` | `0` | 填数字，单位方块；前进完后沿世界 Y 轴偏移，正数向上、负数向下。 |
| `xoffset` | `x` | `0` | 填数字，单位方块；前进完后沿世界 X 轴偏移，**不是相对视线的左右偏移**。 |
| `zoffset` | `z` | `0` | 填数字，单位方块；前进完后沿世界 Z 轴偏移，**不是相对视线的前后偏移**。 |

五个数字参数走 MM 4.9.0 的数值占位符解析，以本次技能上下文求值；本插件没有另加算式处理或非负范围检查。先用普通数字验证，不要套用[本插件算式](../expressions.md)或圆柱扇形的解析规则。

## 简短例子

放进 MM 的 `Skills` 文件：

```yaml
NSDocsForwardEntry:
  Skills:
    - effect:particles{p=flame;amount=10;hS=0;vS=0;speed=0} @SuperForward{forward=5;rotate=0;useeyelocation=false;lockpitch=true;yoffset=1;xoffset=0;zoffset=0}
```

在 4.9.0 适配环境的 MM 生物实体配置 `Skills` 中挂接：

```yaml
# 右键生物触发；朝向取生物，不取右键玩家。
- skill{s=NSDocsForwardEntry} @self ~onInteract
```

选择的位置在生物水平前方 5 方块，再沿世界 Y 轴上移 1 方块。粒子显示仍由 MM 决定。完整示例见 [super-forward.yml](../examples/super-forward.yml)。它已与普通选择器示例分开，**5.x 不要加载这份文件，仅仅不调用技能也可能在加载时出错。**

## 常见问题

- **5.x 识别不到？** 当前项目没有相应实现，不能靠改大小写解决。
- **看向上方时位置升高？** 默认跟随俯仰角。设置 `lockpitch=true` 才会水平前进。
- **`x=1` 为什么转身后不在右侧？** X/Y/Z 是固定世界坐标轴，不跟着施法者转。
- **为什么位置在墙里或空中？** 本选择器只做坐标计算，没有碰撞、落地或可站立位置检查。
- **`rotate` 会让生物转头吗？** 不会发出转头操作，它只旋转用于计算位移的向量，也不会把结果位置的 yaw 改成旋转后的朝向。

<details><summary>更多说明（可选阅读）</summary>

计算顺序是：选脚下/眼睛位置 → 如需要，将该计算位置的 pitch 设为 0 → 取方向并绕 Y 轴旋转 → 归一化后乘 `forward` 并加到位置 → 加世界坐标偏移 → 返回只含这个位置的集合。

旋转使用依赖中的 `AbstractVector.rotate(float)`。已静态核对本地 MM 4.9.0 依赖字节码：参数先转弧度，`x' = x cosθ - z sinθ`、`z' = x sinθ + z cosθ`，Y 分量不变。因此正角从面向南（+Z）转向西（-X），即向右。未锁俯仰时，旋转保留上下分量；锁定俯仰不改变选用眼睛还是脚下作为起点。

源码只对取得的位置对象做计算，不调用实体传送或设置实体朝向的 API；返回位置保留原 yaw，只有开启 `lockpitch` 时将计算位置的 pitch 置零。不能把 `rotate` 当成设置生成实体朝向的通用接口。

依据：4.9.0 模块 `targets/SuperForwardTargeter.java:44-77` 与 `targets/helper/SuperForwardTargeterHelper.java:34-45`，以及注册枚举 `TargetSelectorType`。三版 helper 核对结果为：仅 4.9.0 存在对应 helper。

</details>

[更多示例](../examples/README.md)
