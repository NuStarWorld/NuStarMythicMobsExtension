返回[技能列表](README.md)

# apmm：临时加上 AP 属性再攻击

## 用途与准备

`apmm` 先给**施法者**临时加上 AttributePlus（AP）属性，再攻击选中的目标，正常结束后移除本次临时来源。它不是给目标加增益；给目标添加持续属性请用 [apsource](apsource.md)。

需要额外安装、配置 AttributePlus。施法者和受击者应为存活的生物或玩家；示例属性名必须换成你服 AP 的有效名称。注册名是 `apmm`，不是 `DamageAp`。

## 完整参数表

| 参数 | 简写或其他写法 | 不填写时 | 填写说明与效果 |
| --- | --- | --- | --- |
| `attr` | `a` | 无可用默认值，必须填写 | AP 能识别的属性文本，多行属性用英文逗号隔开，例如 `攻击力:20,暴击几率:3`；这里的名字是假设，须按服替换。数值单位、文本格式由 AP 决定，不是直接扣血量。会通过当前 MM 的字符串占位符功能按本次目标展开。 |
| `clear` | `c` | `false` | `false` 在当前 AP 属性上追加；`true` 临时改用仅含白名单基础、内部蓄力来源及 `attr` 的数据攻击，正常结束后换回原数据。白名单来自 `white-attr-list`，每个属性只读取原区间的第一个数。 |
| `preventImmunity` | `pi` | `false` | `true` 在伤害调用**后**清零目标受伤间隔，便于后续攻击；不能承诺本次攻击绕过原有间隔或所有无敌保护。 |
| `preventKnockback` | `pk` | `false` | 目前填 `true` 也不会取消击退，不能依靠本参数免击退。这与 `fastap` 不同。 |

## 最小 YAML 例子

```yaml
# 调用 NSDocsAPMMEntry；主体攻击施法者的当前存活目标。
NSDocsAPMMEntry:
  Skills:
    - skill{s=NSDocsAPMMMain;sync=true} @self
NSDocsAPMMMain:
  Skills:
    # 必须把“攻击力”换成你服 AP 已配置的有效属性名。
    - apmm{attr=攻击力:20} @target
```

在现有技能或怪物配置中调用 `skill{s=NSDocsAPMMEntry} @self`，保留示例的入口与主体两段写法；需要事先有当前目标，`@target` 不负责搜索最近实体。完整定义见[属性技能组](../examples/attribute-skills.yml)。先在隔离测试服验证，不会自动部署或施放。

## 常见误区

- `attr=攻击力:20` 不是“必定扣 20 血”。扩展先以 `0.01` 发起一次伤害，后续由属性插件和其他插件处理；不要把这个起始值当成最终伤害。
- `clear=true` 不是删除玩家装备、永久清空属性或“忽略全部防御”。它调整的是攻击时施法者的数据；目标防御和其他插件仍可参与。
- `apmm` 没有 `bam`、`baml`、`sendMessage`、`damagecause` 参数，不要从 `fastap` 或原版 `damage` 抄过来。
- 属性文本使用 MM 的字符串占位符入口，不使用 FastAP 的 `caster_level` 公式变量。支持哪些占位符由当前 MM 决定，见[表达式说明](../expressions.md)。

## 使用提醒

**攻击中途报错时，临时属性可能残留，原属性也可能没有恢复。** 请检查日志与实体属性，不要反复触发，也不要把它用于长期增益或在一次攻击未结束时再调用一次。

不要将内部来源名 `APMM`、`APMM_XULI` 用作自己的长期来源名；内置的 `蓄力加成:100` 也不保证在本服有效。白名单规则见[配置说明](../configuration.md)。使用前确认本服 MM 与 AP 版本可用；保留示例两段写法，不要在两段之间插入异步回调，否则不能保证执行顺序。

<details>
<summary>实现细节与依据（可选阅读）</summary>

正常流程使用内部来源名 `APMM`、`APMM_XULI`，并添加固定文本 `蓄力加成:100`；这是现有实现，不是示例要求你的技能自行添加的属性。不要把这些名字当作自己的长期来源名，也不要假定未知的“蓄力加成”在你服必定生效。白名单规则见[配置说明](../configuration.md)。

`apmm` 会短暂修改 AP 中的施法者数据。正常结束后移除临时来源或恢复原数据，但恢复代码不在保证执行的收尾块中；中途异常时**不保证完整清理**，也不能视为可安全嵌套的长期增益方案。出现异常应检查日志与实体属性状态，而不是反复触发。

三版 MM 接入共用这一实现并要求同步执行；示例入口采用同步调度，不保证异步回调后的顺序。这里只核实代码，不承诺特定实服/AP 版本兼容。

实现依据：[AttributePlusMMAdapter](../../adapter-common/src/main/java/top/nustar/nustarmythicmobsextension/adapter/impl/skills/mechanics/AttributePlusMMAdapter.java) 第 44–85 行；[DamageUtil](../../plugin/src/main/java/top/nustar/nustarmythicmobsextension/utils/DamageUtil.java) 第 102–131 行；[AttributeUtils](../../plugin/src/main/java/top/nustar/nustarmythicmobsextension/utils/AttributeUtils.java) 第 29–34 行。

</details>
