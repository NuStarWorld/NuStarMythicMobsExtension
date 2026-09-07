[返回文档首页](README.md)

# 算式：让数值随血量与等级变化

## 用途

例如按目标当前血量给威胁分数，或按等级调整一次 AP 攻击的属性。这里讲的是本扩展的算式，不是“所有 MM 参数都能这样写”。

## 准备

- 安装本扩展、`minecraft-next` 与对应 MythicMobs；FastAP 另需 AttributePlus，威胁例子不需要 AP/SX。
- 施法者与本次目标都要有生命值；[NuStarThreat](mechanics/nustarthreat.md) 的 `add/set/delete/top` 目标只适用于僵尸、牛、村民这类生物，玩家、盔甲架等不适用。
- 先用数字和内置变量调通，再考虑[主配置变量](configuration.md)。PAPI 变量须额外安装 PAPI 与对应扩展，并由玩家作为施法者。

## 哪些地方可以写

| 参数 | 简写或其他写法 | 不填写时 | 填写说明与效果 |
| --- | --- | --- | --- |
| FastAP `attr` | `a` | 不追加属性 | `属性名:算式`，多项用逗号隔开。属性名以本服 AP 为准。 |
| FastAP `baseAttributeMultiple` | `bam` | `1` | 全局基础属性倍率；可以写本页算式，结果不能为负数、无限大或无效数字。 |
| FastAP `baseAttributeMultipleList` | `baml` | 空列表 | `属性名:算式`，多项逗号隔开；结果不能为负数、无限大或无效数字。不是 `attr` 的倍率。 |
| NuStarThreat `amount` | `a` | `0` | 威胁分数算式，结果转为长整数，小数部分截去；所有模式都会先计算，实际数值只供 `add/set` 使用。 |

圆柱扇形选择器的数字参数使用[NuStarCylinderCone 自己的数字规则](targeters/NuStarCylinderCone.md)，不适用本页规则；不要把本页算式、自定义变量或整数除法结论直接套过去。

## 内置变量

下列五个名字在 **FastAP 的上述算式以及 NuStarThreat 的 `amount` 中都提供**，玩家和怪物施法都可以用，不需要写进 `variables`。

| 名称 | 值 | 说明 |
| --- | --- | --- |
| `caster_level` | MM 提供的施法者等级 | 不等于“玩家经验等级”的固定别名。 |
| `caster_hp` | 施法者当前生命值 | 实际生命数值，不是百分比。 |
| `caster_mhp` | 施法者最大生命值 | 不是当前生命值。 |
| `target_hp` | 本次实体目标当前生命值 | 每个目标分别取值，不是触发者的固定别名。 |
| `target_mhp` | 本次实体目标最大生命值 | 做除数前应保证大于 0。 |
| 主配置中的自定义名字 | 配置数值或施法玩家的 PAPI 数值 | **仅玩家施法时提供**；非玩家不能使用配置中的常量名字。 |

在公式中直接写名字，例如 `target_hp*0.5`。不要写 `<target_hp>`，也不要写 `%target_hp%`。

### 算术与两套占位符

- 起步使用 `+`、`-`、`*`、`/` 和圆括号，乘除先于加减，用括号明确计算顺序。
- **整数除法会丢掉小数：`1/2` 是 `0`，要一半写 `1.0/2`。** 不要假设所有数值都会自动变成小数；也不要除以 0 或使用极端大数。
- PAPI 的 `%player_level%` 先在主配置中映射为 `player_level`，公式再写 `player_level*2`。公式中不直接塞 `%player_level%`。
- MM 的 `<P>`（例如 `<caster.hp>`）与 `%PAPI%` 不同：主配置的 `variables` 右边不能填 `<caster.hp>`，在技能算式里改用 `caster_hp`。其他血量与等级需求使用上面的五个内置名字。
- `variables` 右侧不是第二个公式编辑区。常量写 `2`，不要写 `1+1`；显示用的文字占位符也不能作为数字。

## 完整可复制例子

这是一个不需要 PAPI/AP 的例子：MM 村民受到普通僵尸近战攻击后，把“僵尸表中村民的威胁分数”设为僵尸当前生命值的一半，截去小数。它不保证僵尸立即转头。

先放入 `plugins/MythicMobs/Skills/nsdocs-expression.yml`：

```yaml
NSDocsExpressionThreat:
  Skills:
    # @trigger 在入口由攻击事件提供；这里明确选择攻击者僵尸。
    - nustarthreat{mode=set;amount=target_hp*0.5} @trigger
```

再放入 `plugins/MythicMobs/Mobs/nsdocs-expression.yml`：

```yaml
NSDocsExpressionVillager:
  Type: VILLAGER
  Display: '算式测试村民'
  Health: 200
  Skills:
    # 仅在隔离测试服让普通僵尸近战攻击，不要用箭或玩家代替。
    - skill{s=NSDocsExpressionThreat} @trigger ~onDamaged
```

重载 MM 后召唤 `NSDocsExpressionVillager`，在遮阳处或夜间放一只普通僵尸接近村民，避免其他攻击者干扰。若僵尸当时生命值为 20，则设置 10 分。这里 `caster_hp` 指村民，`target_hp` 指僵尸；不是相反方向。

需要 FastAP 时，在已选好实体目标的技能行中使用 `fastap{bam=1.0+caster_level*0.1}`，完整调用方式与属性规则见[FastAP](mechanics/fastap.md)。实际基础倍率不为 1 时，不要对自己攻击。

## 常见问题

- **算式不认识名字？** 检查拼写和大小写；配置变量只供玩家施法，内置变量不要改名。
- **我用了 MM 占位符为什么失败？** 本页不是 MM 的通用占位符解析器。使用内置名字或已配置的 PAPI 映射，不要把 `<caster.hp>` 直接混进公式。
- **PAPI 取的是被打玩家吗？** 不是，取施法玩家。目标血量直接写 `target_hp`。
- **分数怎么少了一点？** 先检查是否整数除法，再检查 NuStarThreat 最后截去小数的规则；例如一半生命值为 10.5 时记录为 10。
- **为什么换个目标结果变了？** `target_hp/target_mhp` 都取当前正在处理的那个目标，不是范围内所有实体的总血量。
- **可以拿这些名字给圆柱半径吗？** 不能照搬。该选择器另有独立解析流程，请按其页面配置。

示例只经静态核对，未做实服验证。[主配置](configuration.md) · [威胁技能](mechanics/nustarthreat.md) · [更多示例](examples/README.md)
