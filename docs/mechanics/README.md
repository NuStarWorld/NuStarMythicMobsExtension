[返回文档首页](../README.md)

# 技能说明

技能决定“这一行做什么”。本扩展提供以下 6 个技能，点击名称可查看完整参数表和示例。

| 技能 | 用途 | 额外准备 |
| --- | --- | --- |
| [apmm](apmm.md) | 给施法者临时加 AP 属性，再攻击目标 | AttributePlus |
| [apsource](apsource.md) | 给选中的目标添加一组 AP 属性，可设置到期时间 | AttributePlus；规划来源名称 |
| [removeapsource](removeapsource.md) | 按来源名移除 AP 属性 | AttributePlus；与添加时使用相同名称 |
| [fastap](fastap.md) | 使用 AP 属性攻击，支持本次基础属性倍率和额外加值 | AttributePlus |
| [sxmm](sxmm.md) | 给施法者临时加 SX 属性，再攻击目标 | SX-Attribute |
| [nustarthreat](nustarthreat.md) | 增加、设置、删除、置顶或转移威胁分数 | 阅读目标与分数方向，无需 AP/SX |

## 怎么选

- 要做持续增益：用 `apsource`，搭配 `removeapsource` 提前结束。
- 要按倍率修改一次攻击的属性：看 `fastap`。它与 `apmm` 的参数不完全一样，不能只改技能名。
- 已经使用 SX 属性系统：看 `sxmm`，不要直接照抄 AP 属性文本。
- 调整怪物关注谁：看 `nustarthreat`，它维护自己的记录，不等于 MM 自带的威胁表。

名称不区分大小写，但这里没有注册 `DamageAp`、`damage-ap` 等第三方名称。参数简写以每页表格为准，不要按其他插件教程猜。

## 当前容易踩的坑

- `apsource` 的 `percentage` 暂不可用；重复使用带时间的同名来源时，请按示例先清理再添加。
- `apmm`、`sxmm` 的 `pk` 当前不能取消击退；`fastap` 的行为不同。
- `nustarthreat` 的 `multiple` 目前不产生倍率效果，使用 `mode` 完整名称避免简写冲突。
- 带非 1 实际倍率的 FastAP 不能用于自伤；属性输入不等于最终伤害。

这些是当前实现的限制，本次文档没有修改它们。使用前看对应页的“常见问题”，不要一口气把所有示例装到正式服。

[目标选择器](../targeters/README.md) · [计算公式](../expressions.md) · [完整示例](../examples/README.md)
