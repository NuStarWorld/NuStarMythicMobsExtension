[返回项目首页](../README.md)

# 使用文档

这套说明面向服务器运营和技能配置人员。先选想实现的效果，再打开对应页面看参数表，不需要理解插件代码。

## 按想做的效果找功能

| 我想…… | 功能入口 | 先知道这一点 |
| --- | --- | --- |
| 临时加一些 AP 属性再打一次 | [apmm](mechanics/apmm.md) | 不会给受击目标添加增益 |
| 给自己或目标加一组限时 AP 属性 | [apsource](mechanics/apsource.md) | 时间是秒；先取好来源名，暂不要填 `percentage` |
| 提前移除那组 AP 属性 | [removeapsource](mechanics/removeapsource.md) | 用同一个来源名精确移除 |
| 调整本次 AP 攻击使用的属性倍率 | [fastap](mechanics/fastap.md) | 属性乘几倍不等于最终伤害乘几倍 |
| 用 SX 属性进行攻击 | [sxmm](mechanics/sxmm.md) | 需要 SX-Attribute，不是 AP 技能 |
| 给怪物记录谁更值得关注、转移分数 | [nustarthreat](mechanics/nustarthreat.md) | 分数第一名不保证立即成为攻击目标 |
| 沿用前面技能选中的那批实体 | [EntitiesInTargets](targeters/EntitiesInTargets.md) | 不会重新寻找附近实体 |
| 在前方一个位置播放效果 | [SuperForward](targeters/SuperForward.md) | 仅有旧版接入，5.x 不要使用 |
| 打到前方扇形或四周圆柱内的实体 | [NuStarCylinderCone](targeters/NuStarCylinderCone.md) | 复制入口和主体两段，不要只抄伤害行 |
| 在消息里显示威胁第一名 | [nustar.threat.top](placeholders/nustar-threat-top.md) | **当前有卡服风险，暂不启用** |

分类总览：[技能](mechanics/README.md) · [选择器](targeters/README.md) · [占位符](placeholders/README.md)

## 第一次使用的顺序

1. 确认本扩展、MM 和 `minecraft-next` 已正常加载；按选用功能安装 AP 或 SX。
2. 阅读功能页的“准备”和参数表。示例中“攻击力”等名字要换成自己服务器有效的属性名。
3. 到[完整示例](examples/README.md)选一组，放入测试服的 MM 技能文件，再按说明给怪物添加调用行。
4. 重载 MM，先观察日志，再验证范围、目标和效果。正常后才考虑迁移到正式服。

本仓库里的 `docs/examples` 只是示例存放位置，插件不会自动把它们安装到服务器。

## 看配置时的几个词

| 写法或叫法 | 通俗解释 |
| --- | --- |
| 技能 | 这一行要做什么，如伤害或添加属性 |
| `@` 后的选择器 | 这一行对谁生效，或者在哪个位置生效 |
| 施法者 | 执行这组技能的生物或玩家；右键怪物触发技能时，不一定是右键玩家 |
| 目标 | 当前技能行选中的对象，可以与施法者不同 |
| 触发者 | 引发本次事件的对象，例如受伤事件中的攻击者；不能保证每种调用都有 |
| 占位符 | 执行时换成实际名字或数字的写法，具体可填位置要看功能说明 |
| 变量 | 给数值起一个名字，用在公式里，如 `caster_hp` 代表施法者当前血量 |

## 公共设置与查错

- [插件配置](configuration.md)：`debug`、AP 白名单、自定义变量，以及修改后如何生效。
- [计算公式与变量](expressions.md)：按血量和等级计算，避免混用 MM、PAPI 和本插件公式写法。
- 日志出现 `Config Error for Targeter line`：先核对 `@` 后的名称和版本。圆柱选择器可写 `@NSCylinderCone`，不能写 `@CYLINDER_CONE` 或旧脚本名 `@PTBCylinder`。
- 没效果不一定是数值太小：还要检查技能有没有被调用、目标有没有选中、前置插件是否就绪。

使用说明以当前项目为准。示例未实服验证，不承诺任意 MM、属性插件和服务端组合都能直接运行。想自行打包时再看[构建说明](building.md)。
