返回[技能列表](README.md)

# sxmm：临时加上 SX 属性再攻击

## 用途与准备

`sxmm` 把属性文本交给 SX-Attribute 解析，临时加到**施法者**身上，攻击选中的目标后正常移除。需要额外安装、配置 **SX-Attribute**；安装 AP 不等于安装 SX。本扩展不会把 AP 的属性名或格式自动转换成 SX 的格式。

施法者与目标应为存活的生物或玩家。使用前确认 SX 能正确识别你的属性文本。注册名是 `sxmm`，不是 `DamageAp`。

## 完整参数表

| 参数 | 简写或其他写法 | 不填写时 | 填写说明与效果 |
| --- | --- | --- | --- |
| `attr` | `a` | 无可用默认值，必须填写 | SX 能识别的属性描述文本，多条用英文逗号分开。名称、冒号空格等格式、数值单位均按你服 SX 配置；经过当前 MM 的字符串占位符功能按本次目标展开，不是 FastAP 数字公式。 |
| `preventImmunity` | `pi` | `false` | `true` 在伤害调用**后**清零受击者的受伤间隔；不保证本次伤害绕过已有间隔或其他保护。 |
| `preventKnockback` | `pk` | `false` | 目前填 `true` 也不会取消击退，不能依赖它防击退。 |

## 最小 YAML 例子

```yaml
# 调用 NSDocsSXMMEntry；需要施法者已有存活的当前目标。
NSDocsSXMMEntry:
  Skills:
    - skill{s=NSDocsSXMMMain;sync=true} @self
NSDocsSXMMMain:
  Skills:
    # 假设你服 SX 能识别“攻击力: 20”；必须换成已配置的有效属性文本。
    - 'sxmm{attr=攻击力: 20} @target'
```

在现有技能或怪物配置中调用 `skill{s=NSDocsSXMMEntry} @self`，保留示例的入口与主体两段写法；外层 `@self` 只是启动主体，真正攻击的是主体里的当前目标，不自动搜索实体。完整例子见[属性技能组](../examples/attribute-skills.yml)。使用前确认本服 MM 与 SX 版本，先在隔离测试服验证，不会自动部署或施放。

## 常见误区

- 这不是给受击者加 SX 增益，也不是永久修改施法者。
- `attr` 文本解析后无有效属性时，实现仍继续发起攻击；“属性名写错”不等于“这行不会造成伤害”。
- 扩展用 `0.01` 发起伤害调用，再由 SX 和其他插件处理；它不是最终固定伤害。
- 没有 `clear`、`time`、`sourceName`、`bam` 或 `sm` 参数；不能用 AP 参数控制 SX。
- `pi=true` 不表示无视所有无敌，`pk=true` 当前也没有防击退效果。

## 使用提醒

**攻击中途报错时，临时属性可能残留。** 请检查日志与施法者属性，不要把它当作长期、多层增益，也不要在一次攻击未结束时再调用一次。其他插件参与处理时，应单独确认效果。

占位符与公式的区别见[表达式说明](../expressions.md)，更多调用方式见[示例索引](../examples/README.md)。

<details>
<summary>属性处理与实现依据（可选阅读）</summary>

属性文本通过 SX 的 `getLoreData` 解析；有效时以 `SXAttributeMMAdapter.class` 作为内部标识写入施法者 API 属性并刷新。正常攻击结束后删除这一标识下的属性并再次刷新。不要把它当作能独立管理多层、长期增益的来源命名系统。

清理没有放在保证执行的收尾块中；中途异常时会记录错误，**不保证临时属性必定清干净**。不要承诺嵌套调用或第三方监听下完全隔离。三版 MM 接入共用实现并要求同步执行；这是代码核实，不是实服兼容保证。

占位符与公式的区别见[表达式说明](../expressions.md)，更多调用方式见[示例索引](../examples/README.md)。

实现依据：[SXAttributeMMAdapter](../../adapter-common/src/main/java/top/nustar/nustarmythicmobsextension/adapter/impl/skills/mechanics/SXAttributeMMAdapter.java) 第 38–62 行；[DamageUtil](../../plugin/src/main/java/top/nustar/nustarmythicmobsextension/utils/DamageUtil.java) 第 102–131 行。

</details>
