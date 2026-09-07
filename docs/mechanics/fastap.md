返回[技能列表](README.md)

# fastap：用 AP 属性进行一次攻击

## 用途与准备

`fastap` 把施法者的属性交给 AttributePlus（AP）计算，再对选中的实体造成伤害。需要另外安装并正确配置 **AttributePlus**；本扩展不会替你配置属性。施法者和目标都应是存活的生物或玩家。

可以只追加本次攻击属性，也可以调整本次使用的基础属性倍率。**倍率不是最终伤害倍数，不永久修改实体属性**；防御、暴击、AP 事件和其他插件仍会影响结果。注册名是 `fastap`，不是第三方教程中的 `DamageAp`。

## 完整参数表

| 参数 | 简写或其他写法 | 不填写时 | 填写说明与效果 |
| --- | --- | --- | --- |
| `attr` | `a` | 不追加属性 | 写 `属性名:数值或公式`，多项用英文逗号分开。是倍率计算后再加的本次属性值，可为负数；单位沿用你服 AP 配置。省略合法，但不要写空的 `attr=`。 |
| `clear` | `c` | `false` | `true` 表示本次基础只保留配置文件 `white-attr-list` 中的属性，再应用倍率、追加 `attr`；不是永久清空装备或实体属性。旧逻辑只保留属性区间的第一个数，不保留完整上下限。 |
| `baseAttributeMultiple` | `bam` | `1` | 全部适用基础属性的倍数，写非负数字或公式，不要填无限大或无效数字；`1.25` 表示乘 1.25，不是增加 1.25 点。也会影响概率等属性，不仅是攻击力。 |
| `baseAttributeMultipleList` | `baml` | 空列表；各属性局部倍数为 `1` | 写 `属性名:倍数或公式`，多项用英文逗号分开；该属性实际倍数是 `bam × 此处倍数`。名称必须是 AP 已登记的有效名称，不接受猜测的别名或重复项。 |
| `sendMessage` | `sm` | `true` | 是否调用 AP 的属性消息发送功能；`false` 只关闭这里发送的消息，不拦截其他插件的消息，也不关闭伤害。具体内容由 AP 决定。 |
| `preventImmunity` | `pi` | `false` | `true` 在本次伤害前把目标的受伤间隔清零；不是无视所有保护、区域规则或事件取消。 |
| `preventKnockback` | `pk` | `false` | `true` 在伤害调用后把目标速度清零；可能也清掉当时的其他移动速度，不是只减去这一次击退。 |
| `damagecause` | `cause`、`dc` | `GENERIC` | 传给伤害接口的来源名称。按依赖中的 `NextDamageSource` 原样填写，区分大小写；如 `GENERIC`、`MAGIC`。不是 AP 属性名，也不直接等于最终伤害算法。非法名称会报错，不要照搬 MM 的 `ENTITY_ATTACK`。 |

## 最小 YAML 例子

```yaml
# 调用 NSDocsFastAPEntry；施法者必须已有非自身的存活当前目标。
NSDocsFastAPEntry:
  Skills:
    - skill{s=NSDocsFastAPMain;sync=true} @self
NSDocsFastAPMain:
  Skills:
    # 此例不需要猜属性名：全部现有适用基础属性乘 1.25，仅用于本次攻击。
    - fastap{bam=1.25;sm=false} @target
```

在现有技能或怪物配置中调用 `skill{s=NSDocsFastAPEntry} @self`，保留示例的入口与主体两段写法。外层 `@self` 只是启动主体；实际受击者是主体的 `@target`，它不会自动寻找最近实体。示例不会自动部署或施放，先在隔离测试服验证。完整例子见 [属性技能组](../examples/attribute-skills.yml) 和 [倍率技能组](../examples/fastap-base-multipliers.yml)。

## 常见误区

- **实际倍率不为 1 时，不要对自己攻击。** 施法者与目标相同且有适用属性实际倍数不为 1 时会报错。没有配置倍率或实际倍率全为 1 时沿用旧计算方式，不代表支持带倍率的自伤。
- `基础 × 全局倍率 × 局部倍率 + attr` 只是交给 AP 的属性输入。基础 80、全局 2、局部 1.5、追加 20，输入是 260，不能据此保证扣 260 血。
- `bam=0` 不保证零伤害；追加属性、AP 事件和其他插件仍可能产生效果。
- `clear=false` 使用当前已生效的 AP 汇总，包括已生效的来源；不会重复计算已包含的百分比增量。`clear=true` 清掉的基础不会被倍率恢复，但可以用 `attr` 另加。
- 示例中的“攻击力”等名称只是假设某服已配置的有效名字；**请替换为自己 AP 的有效属性名**。概率属性的单位也以 AP 为准，不自动换算百分数。
- 公式使用 `caster_level`、`target_hp` 等变量，不直接写 MM 的 `<...>` 或 PAPI 的 `%...%`。`1/2` 是整数除法，写一半请用 `1.0/2`。详见[表达式说明](../expressions.md)。
- `pi` 不等于无敌无效，`sm=false` 不等于静音所有插件，`pk=true` 也不是对后续其他插件击退的永久拦截。

## 更多细节

白名单与自定义变量见[配置说明](../configuration.md)，目标选择见[目标器列表](../targeters/README.md)。使用前确认本服 MM 与 AP 版本。保留示例两段写法，不要在入口与主体之间插入异步回调；不同服务器和第三方属性仍需单独验证。

下面保留原倍率专题及历史验证记录。其“本轮”“本次开发构建”属于原功能开发时的记录，**不是此次文档编辑进行了构建、测试或部署**。

<details>
<summary>更多说明（可选阅读）</summary>

# FastAP：基础属性倍率（bam / baml）

`fastap` 使用 AttributePlus（AP）的属性处理流程进行一次攻击。本页只说明新增的基础属性倍率及它与 `clear`、`attr` 的关系；**倍率修改的是本次 AP 处理的属性输入，不是最终伤害乘数，也不永久修改施法者属性。**

> **先替换 AP 属性名，再使用示例。** 下文的 `攻击力`、`暴击几率`、`暴击倍率` 仅是假设某服已配置的有效 AP 属性名称，并非所有服务器通用。请按你自己的 AP 配置替换；属性的单位、上限及实现也由 AP 配置决定。
>
> 示例会造成真实伤害，尚未实服验证。请先在隔离测试服使用，确保施法者和目标是不同的存活实体。完整技能文件见 [fastap-base-multipliers.yml](../examples/fastap-base-multipliers.yml)。

## 1. 参数与名称解析

| 参数 | 简写或其他写法 | 不填写时 | 填写说明与效果 |
| --- | --- | --- | --- |
| `baseAttributeMultiple` | `bam` | `1` | 全局基础倍率 `G`，一个现有 `Expression` 表达式 |
| `baseAttributeMultipleList` | `baml` | 空列表 | 属性局部倍率，格式为 `属性名:Expression,属性名:Expression` |
| `attr` | `a` | 省略时不追加属性 | 原有临时加法属性，格式仍为 `属性名:Expression,属性名:Expression`；不受基础倍率影响 |
| `clear` | `c` | `false` | 是否先按现有白名单逻辑清理本次攻击的基础属性 |

长短参数同时出现时，按表中的长名、短名顺序取**首个非 `null` 值**，不是按书写顺序选最后一项。显式空值也会命中长名，不会回退短名：

- `baseAttributeMultiple=;bam=2`：长名空表达式非法，不使用 `2`。
- `baseAttributeMultipleList=;baml=攻击力:2`：长名选中空列表，不使用短名列表。
- 省略 `attr` 合法；显式 `attr=` 不是“省略”，仍按错误格式处理。
- `baml` 真正的空字符串表示空列表；只有空白、空条目、尾部逗号、空属性名、空表达式或多余冒号等均不是合法列表。

### baml 属性名

名称去除首尾空白后，使用当前 AP 的 server/default 名称映射归一化：先尝试 server → default；未命中时将输入视作 default 名，再使用 default → server 得到规范 server 名称。因此可使用当前服务器的有效显示/配置名称，也可使用 AP 已登记的 default 名称，包括已注册的自定义属性。

这不是模糊匹配，也不额外提供大小写折叠、中文同义词或任意英文别名。**未知名称、归一化后重复的名称和错误列表格式会抛出 `NSMMEException`**，不会被忽略或当成零属性。比如同一属性的 server 名与 default 名同时列出，仍属于重复项；不要用重复项实现覆盖。

上述严格名称校验是新 `baml` 的规则；`attr` 原有名称处理与同名覆盖行为没有统一改成这套规则。

## 2. 计算顺序与公式

对本次目标、每个属性 `A`：

```text
G       = bam 的求值结果（未配置时为 1）
L(A)    = baml 中 A 的求值结果（未列出时为 1）
M(A)    = G × L(A)
base(A) = clear 处理后、attr 追加前，施法者当前 AP 有效基础值
input(A)= base(A) × M(A) + attr(A)
```

`attr(A)` 未配置时是零追加。**全局倍率作用于全部适用的 AP 基础属性**，不只是攻击力：暴击几率、暴击倍率等率/倍率型属性也在范围内，且保留其原 AP 数值单位。这不是“只乘裸装”，不是只读取装备属性，也不是把 AP 百分比来源系数再乘一遍。

- `bam=2`、某属性未列入 `baml`：该属性的基础倍率为 `2`。
- `bam=2`、`baml=攻击力:1.5`：攻击力的基础倍率为 `3`，其余属性仍为 `2`。
- 仅 `baml=攻击力:1.5`：全局缺省为 `1`，只调整该属性。
- `bam=2`、`baml=攻击力:0.5`：攻击力有效倍率为 `1`，**但其他未列出的属性仍为 `2`**，不能据此认定整个技能恒等。
- 基础攻击力为 `80`、有效倍率为 `3`、`attr=攻击力:20` 时，攻击力输入为 `260`，不是 `(80+20)×3=300`。

基础属性通常是区间 `[low, high]`，倍率分别作用于两端，再由 AP 后续处理选择/消费数值；扩展不会提前随机取一个数来乘，也不会用一次随机样本推算差额。`clear=true` 的旧白名单端点限制是例外，见下一节。

### 零倍率、恒等与自伤

`0` 是合法倍率，但不等于关闭 AP 攻击流程：基础乘零后仍可追加 `attr`，AP 属性/事件仍可能触发，防御、反伤及第三方逻辑仍可能参与，**最终伤害不保证为零**。

没有配置倍率，或所有适用属性的有效倍率都为 `1` 时，保留原有 `clear` / `attr` / AP handle 路径，不创建倍率快照。已配置的表达式仍需先完成求值和合法性检查；恒等判断复用结果，不重新求值。不能用“当前基础恰好为零”代替倍率恒等判断。

当施法者与目标的 UUID 相同且存在适用的非恒等有效倍率时，明确抛出 `NSMMEException`。AP handle 按 UUID 保存攻击/防御数据，自伤会覆盖攻击端快照，无法同时保证“攻击端有倍率、受击端保持原值”。未配置倍率或有效倍率全为 `1` 时仍走原路径；这不代表支持带非恒等倍率的自伤。

## 3. clear 与基础来源

### clear=false（默认）

使用原来可见的施法者当前 AP 有效汇总，包括装备、API/系统来源和已生效到当前汇总的持久来源等。AP 已物化的百分比增量只计入一次；不会为倍率主动刷新原 AP 数据、再次应用百分比系数或重新叠加持久存储。

例如 AP 当前有效基础已经是 `100 + 100×20% = 120`，倍率 `2` 得到 `240`，而不是把 `20%` 再应用一次得到 `288`。若 AP 尚有待刷新变更，以旧 FastAP 路径此时能读取到的有效值为准。

### clear=true

先沿用插件 `config.yml` 的 `white-attr-list` 重建本次白名单基础，再应用倍率。**倍率不能恢复被 clear 清掉的基础值**；不在白名单内的属性仍可以通过 `attr` 另行追加。

这里保留了原 `AttributeUtils.getWhiteAttributeList` 的旧行为：按名读取后只取数组端点 `[0]`，把它作为白名单基础值。本功能没有顺便修复为保留原区间上端点，也不能承诺 `clear=true` 与 `clear=false` 的区间相同。旧白名单构建及其可能的 AP 源事件仍保留。

## 4. 数字、表达式与变量

`bam` 和 `baml` 使用 FastAP 已有的 `Expression` 编译/求值能力，既可写数字，也可写四则表达式；没有另建一种动态语法。

| 内置变量 | 含义 |
| --- | --- |
| `caster_level` | 技能上下文中的施法者等级 |
| `caster_hp` | 施法者当前血量 |
| `caster_mhp` | 施法者最大血量 |
| `target_hp` | 本次目标当前血量 |
| `target_mhp` | 本次目标最大血量 |

启用倍率求值时，**每个目标的 `G` 求值一次、每个列表表达式各求值一次**。变量上下文按需读取并在该目标内缓存：静态 `bam=1`、`2/2` 或常量列表抵消等恒等表达式，不会为了倍率额外调用 PAPI；动态恒等仍必须读取变量才能判定，不承诺零读取。即使 `G=0`，列表仍全部求值并校验，不能借零倍率隐藏非法表达式。

只有实际建立非恒等倍率快照时，`attr` 才复用本次倍率上下文；未配置或恒等倍率时，`attr` 保留原来在 clear 和 handle 构造后逐项读取上下文的方式。通常非恒等且有 `attr` 会在 clear 事件前固定上下文；若仅补入原始来源名称后才发现非恒等，则在 clear 后、数值快照前固定。无 `attr` 的常量倍率无需读取变量。不同目标不共享求值结果，也不会对上一目标的快照继续累乘。

插件 `config.yml` 的 `variables` 也沿用旧规则：

- **只有玩家施法者才注入自定义配置变量，包括配置为常量的变量。** 非玩家仍可使用上述五个内置变量。
- 数值/可解析数值字符串可以缓存；其他字符串按该施法玩家的 PlaceholderAPI 语义解析，并要求得到有效数值，PAPI 动态结果不缓存为常量。
- 自定义变量针对的是施法玩家，不自动变成“目标的 PAPI”。五个内置变量在自定义变量之后写入，因此覆盖同名配置项。
- 在表达式里引用配置变量的名称，不直接嵌入 `%placeholder%`。同样不直接写 MythicMobs 的 `<caster.level>` / `<target.hp>`。

这里**不是**新目标器（例如 CylinderCone）的 PlaceholderString 严格展开规则；不要把目标器支持的 `<...>` 写法或其数值解析限制搬过来。

### 数值限制与报错

- `G` 与每个局部倍率都必须是**非负有限数**。负数、NaN、正负 Infinity 被拒绝；两个负倍率相乘为正也不合法。BigDecimal 的极小负数在转为 double 前检查符号，不会因下溢为 `-0.0` 被当成合法零倍率。
- 表达式编译错误、缺失变量、整数除零等显式求值异常，会作为带参数信息的 `NSMMEException` 报错。非法倍率不会静默回退默认值。
- 新增的 `G × L(A)`、基础区间两端乘倍率若产生非有限结果，也会报错。`attr` 不套用这项非负倍率限制，原有负数追加仍可使用。
- **保留原 Expression 的整数除法。** `1/2` 得到 `0`；要写一半请用 `1.0/2`。血量上下文是浮点数，但纯整数字面量表达式仍可能做整数运算。
- **不承诺检测所有数值溢出。** 原 Expression 的 Long 运算可能静默回绕，例如 `9223372036854775807*9223372036854775807` 会得到 `1`。该上游限制保持不变，有限且非负的回绕结果无法仅靠结果检查识别，甚至可能被视为恒等倍率。请避免超过数值范围；新增非有限检查不等于全程 checked arithmetic。

动态例 `1.0+target_hp/target_mhp` 在目标 `50/100` 血量时得到 `1.5`。前提是目标最大血量有效且大于零；若为零导致 Infinity/NaN 或异常，将拒绝本次倍率，没有自动补 `1` 或其他兜底值。

## 5. AP 输入快照与兼容边界

非恒等倍率为**本次 handle**创建独立 `AttributeData`，按名读取 clear 后基础并用新数组保存缩放后的两端，再通过新数据的 Central force 值入口供 handle 读取。

这一步不修改原施法者的数据、source、map、数组或受害者 AP 数据，也不是先给原实体添加临时来源再删除。多目标不会相互污染。这里说的是扩展准备倍率快照的行为，不是承诺后续真实伤害、AP 事件或第三方监听不会修改实体。

### 为什么不再次套 Central cap

基础读取时已包含当前 AP 汇总自身适用的上限。重建使用新数据的 force 入口，避免对已缩放基础再进行一次 Central cap、来源 corrector 或额外的倍率源添加事件。原 AP 后续的 SubAttribute 上限、概率、暴击、防御与伤害流程仍保留。

例如某属性确有 Central cap=`100`，当前有效基础=`80`，`M=2`，`attr=-70`：

```text
本次属性输入：80 × 2 - 70 = 90
不是：       min(80 × 2, 100) - 70 = 30
```

这不表示所有 AP 属性都有相同上限，也不表示绕过后续 AP cap。`base × M + attr` 是交给后续 AP 算法的属性输入公式，**不是实际伤害的线性保证**。

### 不是完整 AttributeData 克隆

- 快照隔离的是数值，复制了相应攻击/防御时间戳，但不会完整复制 counter、persistent、variable、来源/百分比/战力等元数据；不能假定这些额外状态与原数据相同或具有相同持久副作用。
- 内置 AP `3.3.3.0` 已核验的攻击、暴击等数值读取路径通过 handle 取值，未发现这些已核验路径依赖上述额外状态。此结论来自限定版本的静态 API/字节码核验，**不是实服或所有 AP 版本/自定义属性兼容证明**。
- 第三方通过本次 handle 读取攻击端属性数值，可看到倍率快照；直接调用 `AttributeAPI.getAttrData(entity)` 则仍看到原数据，不会自动带上本次倍率。按来源、计数器或持久状态工作的第三方逻辑需单独核验。
- 枚举覆盖当前 AP 有效登记名称，并补入可见来源中的名称；不能承诺枚举未注册且只藏在任意 force key 中的字符串属性。
- 受害者仍使用原 AP 数据。后续监听主动修改 handle、临时属性或实体的行为，属于第三方边界，不能由数值快照保证透明兼容。

## 6. 完整技能示例与线程

将 [示例 YAML](../examples/fastap-base-multipliers.yml) 放入 `plugins/MythicMobs/Skills/`。文件提供三组互不依赖的完整入口和主体：

| 入口技能 | 内容 |
| --- | --- |
| `NSFastAPGlobalEntry` | 静态仅 `bam`，省略 `attr` |
| `NSFastAPCombinedEntry` | `bam + baml + attr` |
| `NSFastAPDynamicEntry` | 使用本次目标血量比例的动态全局倍率及动态局部倍率 |

这些是技能组定义，不会因放入文件就自动施放；请从你现有的合法技能触发链调用相应入口。没有要求额外示例物品、未定义怪物或虚构的命令/触发器。三个主体都使用 `@target`，即 MythicMobs 的**施法者当前目标**，不是外层 `@self`，也不是固定选最近实体。施法者必须确有有效的非自身目标；没有当前目标时，不能指望它自动找一个目标。

每个入口以 `skill{s=主体名;sync=true} @self` 调度，主体内才用 `fastap ... @target` 选择受击者。外层 `@self` 只是入口调度目标，**不要把真正造成伤害的非恒等 `fastap` 行改成 `@self`**。

三个 MM wrapper（4.9.0、5.1.0、5.6.0）仍委托同一共用 FastAP 实现，本功能没有分别重写它们。线程策略仍是 wrapper 原有的 `forceSync=true`、`setAsyncSafe(false)`；这里没有新增 CylinderCone 那种 Bukkit 真实主线程 guard。示例采用显式同步入口，但不承诺异步切换后与父技能后续行之间的阻塞执行顺序，也不要插入异步回调后仍假定同步。

## 7. 验证范围

本轮已完成三版定向编译、94 项 FastAP 定向测试及一次显式 `check build --offline --console=plain`。最终全项目 235 项测试通过，0 失败、0 错误、0 跳过；格式检查通过。测试包括真实 Expression/生产数值辅助逻辑与 ASM 接入契约，不包含 AP/NMS 服务器单例或完整攻击流程的实服运行验证。

本次开发构建为 `build/libs/NuStarMythicMobsExtension-2.0.12-dev.local-4f060976.jar`（310791 字节）。已核对新实现及三版 FastAP 入口的 class major version 均为52（Java 8），未包含 FastAP 测试类、JUnit 或 ASM 测试依赖。版本末尾是构建基线提交号，不表示本轮工作区改动已经提交。

构建保留了现有 AP 重定位 Kotlin 注解类缺失及 Gradle/API 弃用警告，不是零警告构建。未部署、未执行服务器命令，第三方属性及监听器兼容边界仍以本页说明为准。

维护者如需补充定向验证，应注意本项目的 `build` 默认不运行测试，需要显式指定对应模块的 `:test` 任务及 `--tests '*FastAP*Test'`，例如 `:NuStarMythicMobsExtension-adapter-common:test --tests '*FastAP*Test'`；这只是任务选择说明，不表示本页编写过程中执行过该命令，也不要求 `clean`。

实现参考：[FastAPAdapter](../../adapter-common/src/main/java/top/nustar/nustarmythicmobsextension/adapter/impl/skills/mechanics/FastAPAdapter.java)、[FastAPBaseMultiplier](../../adapter-common/src/main/java/top/nustar/nustarmythicmobsextension/adapter/impl/skills/mechanics/FastAPBaseMultiplier.java)、[GlobalVariable](../../adapter-common/src/main/java/top/nustar/nustarmythicmobsextension/adapter/impl/skills/GlobalVariable.java)、[MainConfiguration](../../plugin/src/main/java/top/nustar/nustarmythicmobsextension/configuration/MainConfiguration.java)、[AttributeUtils](../../plugin/src/main/java/top/nustar/nustarmythicmobsextension/utils/AttributeUtils.java)。

</details>
