返回[技能列表](README.md)

# apsource：给目标添加一组 AP 属性

## 用途与准备

`apsource` 给**选中的目标**添加 AttributePlus（AP）属性，并为这组属性起一个来源名，方便到期或手动移除。它本身不发起攻击。需要额外安装、配置 AttributePlus，目标应为存活的生物或玩家；`@self` 就是给施法者自己添加。

建议明确写 `sourceName`，并且先只用 `attr`。**当前 `percentage` 填非空值会报错，暂时不要使用。** 注册名是 `apsource`。

## 完整参数表

| 参数 | 简写或其他写法 | 不填写时 | 填写说明与效果 |
| --- | --- | --- | --- |
| `attr` | `a` | 无可用默认值，必须填写 | AP 能识别的属性文本，多项用英文逗号分开，例如 `攻击力:20`。属性名、单位以你服 AP 配置为准；通过当前 MM 的字符串占位符功能按本次目标展开。 |
| `sourceName` | `s` | 随机名 `NSMME-APSource-` 加随机标识 | 这组属性的名字，不是属性本身的名字。支持 MM 字符串占位符；要手动删除或刷新同一组属性，建议填写固定且独有的名字，不要留空字符串。 |
| `time` | `t` | `-1`，不新建自动到期任务 | **单位是秒，不是 tick。** 经 MM 数字占位符求值后截去小数部分；只有截断后大于 0 才安排自动移除。建议写正整数，如 `10`。`0`、负数和 `0.5` 都不会新建计时任务。 |
| `percentage` | `pa` | 不追加百分比属性 | 目前填非空值会报错，暂时不要使用。 |

## 最小 YAML 例子

```yaml
# 调用 NSDocsAPSourceEntry，给自己添加一组约 10 秒后移除的属性。
NSDocsAPSourceEntry:
  Skills:
    - skill{s=NSDocsAPSourceMain;sync=true} @self
NSDocsAPSourceMain:
  Skills:
    # 先清理本示例同名来源与旧计时，方便重复试用。
    - removeapsource{sourceName=NSDocsBuffAttack} @self
    # “攻击力”只是示意，必须换成你服 AP 已配置的有效属性名。
    - apsource{attr=攻击力:20;sourceName=NSDocsBuffAttack;time=10} @self
```

在现有技能或怪物配置中调用 `skill{s=NSDocsAPSourceEntry} @self`，保留示例的入口与主体两段写法。完整文件及配套移除入口见[属性技能组](../examples/attribute-skills.yml)。不自动部署或施放，使用前确认本服 MM 与 AP 版本，并先在隔离测试服确认属性生效与移除。

## 常见误区

- `time=20` 是 20 秒，不是 1 秒。计时按现实时间判断，但每 20 tick 检查一次；卡服时不保证准点移除。
- 不写时间只是“不安排到期”，**不是保证永久保存或跨重启存在**；这里调用的是普通 AP 来源接口，不是持久来源接口。
- 同一目标、同一来源名在所核验 AP 依赖中是替换这一来源，不是每次增加一层独立增益。不同名字可以同时存在，但最终属性仍受 AP 规则影响。
- 随机来源名不方便精确移除；不要靠大范围前缀删除来弥补没有规划命名。
- 想用百分比时不能照抄参数表后假定已可用；当前 `percentage` 的限制来自代码，示例有意不使用它。

## 更多细节

### 重复添加与计时注意

相同目标、相同来源名且旧计时尚未到期时，新的正整数时间会把截止时间改为“现在加这次秒数”，不是把两次时长相加。

旧计时若仍在运行，再用同名 `time=-1` 或 `time=0` 添加，并不会取消旧计时，仍可能被旧任务删除。旧来源已自然到期后直接复用同名正时间，现实现有时间计算缺陷，可能在下次检查时立即移除。需要重新添加时，先明确调用 [removeapsource](removeapsource.md) 清理同名记录，再添加；不要将此当成无条件可靠的自动续期系统。

添加或移除属性时，AP 和其他插件也可能进行处理，不保证只影响本页提到的属性。玩家属性会额外更新一次；其他生物没有这一步。

<details>
<summary>属性更新、百分比问题与实现依据（可选阅读）</summary>

`percentage` 原格式为 `属性名:整数百分数`，也能解析 `属性名=整数百分数`，多项用英文逗号分开，`20` 表示 20%；它不经过 MM 占位符展开。以下仅解释原设计，不表示目前可用。

添加后若目标是玩家，会额外调用 AP 的属性更新；非玩家不走这次显式更新。AP 来源接口自身也可能触发属性检查和来源事件，不能理解成对其他插件完全无副作用。

`percentage` 的预期算法是读取目标本次属性随机值，再乘整数百分数除以 100；并非持续跟随基础变化的比例来源。但属性列表由 `Arrays.asList` 创建，之后对它执行 `addAll`，非空百分比项会在真正添加来源之前失败。此页只说明现状，没有修改源码。

三版 MM 使用相同共用实现；`attr`、`sourceName` 经过各版 `PlaceholderString.of(...).get(技能上下文, 目标)`，`time` 经过 `PlaceholderDouble`，不等于 FastAP 自己的公式系统。详见[表达式说明](../expressions.md)。示例采用同步入口；并非实服或所有 AP 版本兼容证明。

实现依据：[APSourceAdapter](../../adapter-common/src/main/java/top/nustar/nustarmythicmobsextension/adapter/impl/skills/mechanics/APSourceAdapter.java) 第 46–87 行；[AttributeSourceInstance](../../plugin/src/main/java/top/nustar/nustarmythicmobsextension/entity/AttributeSourceInstance.java) 第 40–49、90–107 行；[AttributeUtils](../../plugin/src/main/java/top/nustar/nustarmythicmobsextension/utils/AttributeUtils.java) 第 37–46 行。同名替换另静态核实当前依赖 AP 3.3.3.0 的 `AttributeCentral.addSource` 使用同名映射替换；不代表所有 AP 版本的行为保证。

</details>
