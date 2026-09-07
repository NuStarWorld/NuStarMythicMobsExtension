[返回目标选择器列表](README.md)

# NuStarCylinderCone：选择前方圆柱扇形内的实体

## 用途

用于扇形挥击、横向扫射等技能：从施法者附近选出**水平前方扇形内、且高度合适的有生命实体**。抬头或低头不会让区域变成斜着的圆锥；它也不会检查墙壁遮挡或敌我阵营。

## 准备

- 安装本扩展及对应 MM 适配，使用前确认本服版本。
- 有效名称为 `@NuStarCylinderCone`、`@NSCylinderCone`、`@TailedBeastCylinderCone`、`@PTBCylinderCone`，不区分大小写。`CYLINDER_CONE` 是代码里的分类名，不是配置名称；`PTBCylinder` 没有注册。
- 从旧 JS 迁移时先停用注册上述同名选择器的脚本。不需要开启 MM 原生威胁表。
- **保留下面两段写法：外层用 `skill{...;sync=true} @self`，内层再选目标。** 这样先安排好技能执行，再找附近实体；不能只给带本选择器的同一行加 `sync=true` 来代替。
- 示例会造成伤害，只在隔离测试服验证；观察玩家命中请用生存模式并留意 MM 原生过滤。

## 参数

七个数字参数填普通数字，或最终能展开成数字的 MM 占位符。**不支持算式与随机范围**，例如 `2+3`、`<caster.level>*2`、`2to5` 都不能填。`r/a/rot` 与长名同时出现时优先短名。

| 参数 | 简写或其他写法 | 不填写时 | 填写说明与效果 |
| --- | --- | --- | --- |
| `r` | `range` | `10` | 填非负数字，单位方块；基础水平半径，实际半径还要加 `padding`。 |
| `a` | `angle` | `30` | 填 0 到 180 的数字，单位度；这是**半角**，30 表示左右各 30 度，180 表示整个圆柱。 |
| `rot` | `rotation` | `0` | 填数字，单位度；相对施法者水平朝向旋转，正数向右、负数向左。 |
| `down` | 无 | `16` | 填非负数字，单位方块；中心向下的基础高度。 |
| `up` | 无 | `10` | 填非负数字，单位方块；中心向上的基础高度。 |
| `padding` | 无 | `1.25` | 填非负数字，单位方块；同时加到半径、向下高度、向上高度上。 |
| `close` | 无 | `2.5` | 填非负数字，单位方块；水平近身范围内不限制方向，但仍必须在半径和高度内。 |
| `origin` | 无 | `false` | 填 `true/false`；`true` 用本次技能的原点作中心，没有原点则仍用施法者位置，朝向始终取施法者。 |
| `ignoreplayers` | 无 | `false` | 填 `true/false`；`true` 在本插件这一步排除玩家，`false` 不保证能绕过 MM 后续过滤。 |
| `ignorearmorstands` | 无 | `true` | 填 `true/false`；`true` 排除盔甲架，设为 `false` 也不能强制绕过 MM 过滤。 |
| `ignore` | 无 | 空字符串 | 填兼容过滤文本；转小写后只要包含 `player`，本插件也会排除玩家，即使 `ignoreplayers=false`。MM 还可能自行处理它。 |

不要填无限大或无效数字，例如 `NaN` 或 `Infinity`。开关项也接受 `yes/no`、`1/0`，建议统一写 `true/false`。

## 简短例子

将下面**两个完整技能组**放进 MM 的 `Skills` 文件：

```yaml
# 外部调用这个入口，先进入同步子技能。
NSDocsConeEntry:
  Skills:
    - skill{s=NSDocsConeMain;sync=true} @self

# 在子技能内部才搜索附近实体。
NSDocsConeMain:
  Skills:
    - damage{a=4} @NSCylinderCone{r=10;a=30;down=2;up=2;padding=0;close=0}
```

在 MM 生物的实体配置 `Skills` 中挂接 `- skill{s=NSDocsConeEntry} @self ~onInteract`。右键生物时，按**生物自身朝向**选取半径 10 方块、左右各 30 度、上下各 2 方块的区域，造成 4 点技能伤害。附近目标由圆柱选择器实际查找，不依赖右键玩家被传成技能目标；实际伤害受护甲、MM 与其他插件影响。

完整示例文件 [cylinder-cone.yml](../examples/cylinder-cone.yml) 还保留了默认范围与等级半径的旧入口，并提供 `NSDocs` 新入口。动态例需要有等级上下文的 MM 生物，不能直接当作普通玩家施法例子。

## 常见问题

- **只加 `sync=true` 仍报错？** 如果与 `@NSCylinderCone` 写在同一行，选择目标时可能还没安排好执行。必须用上面“外层 `@self` + 内层选择器”的两段写法；投射物或光环的新回调也要重新安排。
- **为什么背后的近身实体也中？** 默认 `close=2.5` 放宽了方向。设 `close=0` 才更容易观察纯扇形；与中心水平重叠的位置仍可能命中。
- **半径写 10 为什么超过 10？** 默认 `padding=1.25`，实际半径是 11.25，上下范围也各多 1.25。需要精确范围时写 `padding=0`。
- **角度写 180 为什么全方向？** `a` 是半角，不是总张角；有效范围是 0 到 180，不是 0 到 360。
- **为什么玩家或盔甲架没有被选中？** 先检查插件过滤开关，再检查 MM 原生过滤。允许进入本插件结果不等于 MM 必须保留。
- **占位符失败会当成没目标吗？** 不会按正常空目标处理：会记录参数信息并报错。优先检查它最终是否展开成合法数字。

<details><summary>更多说明（可选阅读）</summary>

## 深入规则与旧示例

以下保留详细几何、占位符与过滤边界说明，以及原有入口 `NSConeEntry`、`NSConeLevelEntry`，便于已有配置迁移。

在指定中心附近，以施法者的**水平朝向**选择圆柱中的扇形区域；俯仰角不改变上下范围。这不是三维圆锥，也不是碰撞箱相交检测。

> **使用前必须确认两件事：**
>
> 1. **只能在 Bukkit 真实主线程选择目标。** 必须先通过外层 `skill{...;sync=true} @self` 进入同步子技能，再在子技能中使用本选择器。给带本选择器的同一行追加 `sync=true` 不能保证安全。
> 2. **数字是“本版 `PlaceholderString` 展开后严格解析”，不是算式。** 支持数字及最终展开为数字的占位符；不支持 `2+3`、`<caster.level>*2` 或随机范围字符串。非法结果会记录错误并抛出 `IllegalStateException`，不会自动修正或当作空目标继续。

## 名称、接入与迁移

以下四个名称等价，名称匹配使用 `equalsIgnoreCase`，不区分大小写：

- `@NuStarCylinderCone`
- `@NSCylinderCone`
- `@TailedBeastCylinderCone`
- `@PTBCylinderCone`

**从 JS 迁移前，先停用注册了上述同名选择器的脚本。** 不要让脚本与插件同时争用这些名称。

三个适配模块均已接入公共实现：`mythicmobs-adapter-4-9-0`、`mythicmobs-adapter-5-1-0`、`mythicmobs-adapter-5-6-0`。这些是项目模块名，**不是精确的 MythicMobs 兼容版本范围**；本文与示例未经过实服测试，不据此承诺某个服务端组合可用。

## 参数

距离单位为方块，角度单位为度。`r`、`a`、`rot` 分别优先于自己的长名；同时填写时采用短名，即使短名内容非法，也不会改用长名兜底。

| 参数 | 简写或其他写法 | 不填写时 | 填写说明与效果 |
| --- | --- | --- | --- |
| `r` | `range` | `10` | 基础水平半径，非负；实际半径还要加 `padding` |
| `a` | `angle` | `30` | **半角**，左右各 `30` 度，默认总张角 `60` 度；范围 `[0,180]` |
| `rot` | `rotation` | `0` | 相对施法者水平朝向的旋转；正值向右，负值向左，允许负数 |
| `down` | — | `16` | 向下的基础高度，非负 |
| `up` | — | `10` | 向上的基础高度，非负 |
| `padding` | — | `1.25` | 同时扩展水平半径、向下高度、向上高度，非负 |
| `close` | — | `2.5` | 水平近身距离，非负；只放宽方向判断，不再叠加 `padding` |
| `origin` | — | `false` | 使用技能元数据的原点作为中心，缺失时回到施法者位置 |
| `ignoreplayers` | — | `false` | 插件本地是否排除玩家 |
| `ignorearmorstands` | — | `true` | 插件本地是否排除盔甲架 |
| `ignore` | — | 空字符串 | 兼容项：字符串转为小写后只要包含 `player`，插件本地也会排除玩家；MM 还可能按自己的规则处理此通用参数 |

三个布尔项接受 `true` / `yes` / `1` 与 `false` / `no` / `0`，字母不区分大小写；未知字符串回到**该参数自己的默认值**。本插件不对布尔字符串执行 `trim`，例如传到解析器的 `" true "` 不匹配真值写法，而是回到该参数默认值（`ignorearmorstands` 仍默认 `true`）。这些布尔项不是下面七个动态数字参数的一部分。

`ignoreplayers=false` 不会覆盖 `ignore` 中包含 `player` 的排除要求。插件允许某类实体进入本地结果，也不代表 MM 后续一定保留它，见“目标过滤边界”。

## 几何规则

设目标的实体位置点相对中心为 `(dx, dy, dz)`，水平距离为 `d = hypot(dx, dz)`：

```text
radius = r + padding
水平限制：d <= radius
高度限制：-down - padding <= dy <= up + padding
```

先满足水平和高度限制，再满足下列任意一条方向条件：

- `d == 0`，即与中心水平重叠；
- `d <= close`，即处于近身范围；
- 目标水平方向与旋转后朝向的夹角不超过半角 `a`。

因此：

- 默认实际水平半径为 `11.25`，垂直相对范围为 `[-17.25, 11.25]`，近身距离仍是 `2.5`。
- `close` **不豁免半径或高度**；即使大于 `radius`，也不能选中圆柱外的目标。
- `close` 使用水平距离，正上方或正下方的实体也可能命中，但仍须在高度范围内。即使 `close=0`，水平重叠点仍被保留。
- `a=0` 是朝向轴线（另有近身放宽）；`a=90` 的方向区域是前半圆；`a=180` 是全圆柱。默认 `close` 仍会让前半圆后侧的近身点命中；要单独观察角度效果，可设 `close=0`。
- `a` 在 `90..180` 内继续向后扩展，**不会出现旧 JS 的 `cos²` 比较丢失点积符号导致的大角度折返**。
- `rot` 按 `360` 度取模；方向只取施法者视线的水平投影。施法者几乎垂直朝上或朝下时，改用其 `yaw` 保持水平朝向，再应用旋转。
- 最终按 `Entity.getLocation()` 的位置点精判，不以实体碰撞箱相交作为命中。半径与高度包含边界且不另加容差；方向比较有 `1e-12` 的点积数值容差。
- Bukkit 附近查询的方盒采用严格相交，因此候选预筛会将各轴边界向外扩展少量浮点步长，避免零高度和恰在上边界的实体被提前漏掉。最终位置精判仍使用原始数值，不扩大技能实际命中范围。

### `origin=true`

只替换中心，不替换施法者身份，也**不采用原点的朝向**；方向仍来自施法者。元数据没有原点时回退到施法者位置。候选查询使用**中心所在世界**，不是固定使用施法者世界，且不会修改原点或施法者的位置对象。

原点由调用技能的元数据提供；把技能目标写成 `@self` 并不等价于为本选择器设置原点。不同 MM 技能、回调与版本的原点传递可能不同，这里不提供未经各版本核验的原点构造示例。

## 必须先进入真实主线程

三个版本的 `getEntities` 在入口第一条语句执行 `Bukkit.isPrimaryThread()` 检查，早于读取元数据、展开占位符和访问实体。检查失败时记录错误并抛出 `IllegalStateException`；插件**不自动切线程、不提交 `Future`，也不等待异步查询结果**。

可靠的配置结构如下，入口只用 `@self`，本选择器放在同步子技能的实际技能行中：

```yaml
# 外部技能或实体触发器应调用此入口。
NSConeEntry:
  Skills:
    - skill{s=NSConeMain;sync=true} @self

# 不要在此例中插入异步回调或延迟后，再假定仍然同步。
NSConeMain:
  Skills:
    - damage{a=4} @NSCylinderCone{r=10;a=30}
```

**不要把同一行的 `damage{a=4;sync=true} @NSCylinderCone{...}` 或 `skill{s=...;sync=true} @NSCylinderCone{...}` 当作安全写法。** MM 会先选择目标，再进入技能的 `cast` 同步调度；选择器可能在该行的 `sync` 生效前就已经异步执行。

外层技能从异步转到同步时，子技能可能晚于父技能的后续行运行；这个示例不是阻塞等待，不能据此保证父子行之间的即时顺序。若改成投射物、光环或其他回调，应在那个新的入口再次采用“外层同步技能 + 子技能内选择”的结构，不要假定同步状态永久传递。

检查依据是真实线程，不是 `SkillMetadata` 的异步标记。第三方若在异步线程错误地将元数据标成同步，MM 可能不再调度，最终仍会被本插件的线程检查拒绝；此时需修正调用方，不能绕过检查。

## 动态数字：只展开，再严格解析

每次正常选择会将 `r`、`a`、`rot`、`down`、`up`、`padding`、`close` **各展开一次并各解析一次**，形成该次选择的固定数值快照；不为每个候选实体重复展开。若中途遇到错误，立即抛出，不继续展开后续项。静态数字也走相同流程。

各版本封装使用当前 MM 版本自己的 `PlaceholderString`，以仅传入 `SkillMetadata` 的重载展开（`metadata-only`），然后交给 `Double.parseDouble`。这里不使用会将非法值替换成默认数字的 `PlaceholderDouble`，不调用表达式计算器，**也不把施法者强行当成目标传给占位符**。

| 配置值示例 | 是否可用 |
| --- | --- |
| `r=10`、`padding=1.25`、`rot=-45` | 可以，最终数字须有限且满足各项范围 |
| `r=<caster.level>` | 可以，但此例需要有等级上下文的 MythicMobs 生物作为施法者，且提供者最终返回非负有限数字 |
| `r=2+3` | 不支持算术表达式 |
| `r=<caster.level>*2` | 不支持展开后的乘法表达式 |
| `r=2to5`、`r=2-5` | 不支持随机范围字符串 |

`<caster.level>` 表示 MM 施法者等级，不应把动态示例直接当成普通玩家施法的通用示例。其他占位符是否可用，取决于对应版本的提供者及本次元数据；需要目标上下文的占位符不能依赖本插件偷偷传入施法者来工作。

以下情况会记录包含参数名、原始值、展开值和原因的错误，并抛出 **`IllegalStateException`**：

- 占位符创建或展开失败；
- 展开结果为 `null`、空字符串、仍未解析的占位符或其他非数字文本；
- 展开结果为 `NaN`、`Inf` / `Infinity` 等非法或非有限数字；
- `r`、`down`、`up`、`padding`、`close` 为负，或半角 `a` 不在 `[0,180]` 内；`rot` 可以为负但必须有限；
- 各输入单独有限，但 `r + padding`、`down + padding` 或 `up + padding` 溢出为非有限值。

另外，若中心坐标与范围无法构造有限且严格覆盖闭边界的查询方盒，会记录坐标轴、中心、原范围和扩张范围，并抛出 `IllegalStateException`，不会开始附近实体查询。

占位符在加载时创建失败的情况会被保存在本目标器中，不中断目标器注册；在执行时记录并重抛该错误，不重新尝试创建、不使用默认数值。这样避免 MM 在注册失败后继续使用默认 `TriggerTargeter` 而选到错误目标。静态非法数值同样在选择执行时通过严格解析报错，不应将“加载成功”当作配置数值已经有效。

使用 `IllegalStateException` 是为了避免 MM 将 `IllegalArgumentException` 转为空目标，从而掩盖配置错误。**本功能不会自动修数，也不会把失败当作一次正常的空目标选择。** 若 MM 占位符提供者在内部已经把异常转成了一个合法默认数字，本插件只看到该数字，无法识别其原始错误；严格解析不能穿透提供者内部的兜底逻辑。

## 目标过滤边界

插件本地先排除施法者自身和所有非 `LivingEntity`，再按 `ignoreplayers` / `ignore` 和 `ignorearmorstands` 排除玩家、盔甲架。没有额外的 `isDead` 过滤，也不做视线遮挡检查、阵营或敌我过滤。墙后、同阵营实体仍可能进入本地结果，具体技能能否对其生效是另一层行为。

返回的是可变、去重的实体集合；本插件不保证遍历顺序。**返回后 MM 原生 `filter` 仍会继续处理**，包括创造/旁观玩家、盔甲架以及通用目标数量限制 `limit`、排序 `sort` 等，具体行为以运行的 MM 版本与配置为准。

所以 `ignoreplayers=false` 或 `ignorearmorstands=false` 仅表示不在插件本地排除，**不能强行把 MM 后续过滤掉的实体纳入最终目标**。本功能不承诺绕过或穿透 MM 原生过滤器。

## 完整技能示例与入口调用

完整技能组见 [cylinder-cone.yml](../examples/cylinder-cone.yml)，包含静态入口 `NSConeEntry` 和动态入口 `NSConeLevelEntry`，所有被调用的子技能都在同一文件中。将其复制到服务端的 `plugins/MythicMobs/Skills/cylinder-cone.yml`；不需要 JS。

为便于手动触发，在 `plugins/MythicMobs/Mobs/cylinder-cone-demo.yml` 中另建以下实体配置；**不要把实体配置一起放进 Skills 文件**：

```yaml
# 右键触发静态示例；选择方向来自这头牛，不是右键玩家。
NSConeStaticDemo:
  Type: COW
  Display: '圆柱扇形静态示例'
  Health: 100
  Skills:
    - skill{s=NSConeEntry} @self ~onInteract

# 右键触发动态示例；水平半径直接取这头 MM 生物的等级。
NSConeLevelDemo:
  Type: COW
  Display: '圆柱扇形等级示例'
  Health: 100
  Skills:
    - skill{s=NSConeLevelEntry} @self ~onInteract
```

在已安装相应插件且停用同名 JS 的隔离测试服中，管理员可按以下方式加载配置并生成示例生物：

```text
/mm reload
/mm m spawn NSConeStaticDemo:1
/mm m spawn NSConeLevelDemo:8
```

然后右键相应生物。静态示例使用默认几何参数并造成 `4` 点技能伤害；动态示例在等级为 `8` 时使用半径 `8`、半角 `45`、上下各 `2`、`padding=0`、`close=0`，造成 `2` 点技能伤害。实际伤害仍可能受 MM、护甲或其他插件影响。这些配置会影响附近实体，请勿直接拿生产玩家验证；观察玩家命中时还要考虑 MM 对创造/旁观模式的过滤。

若从已有技能组调用，使用 `- skill{s=NSConeEntry} @self` 或 `- skill{s=NSConeLevelEntry} @self`，由示例入口完成同步切换；不要将入口行的 `@self` 换成新选择器。本文没有执行以上服务端命令，也没有进行实服验证。

## 与原 JS 的重要区别

- **大角度正确扩展：** 保留点积符号，半角 `90..180` 不再因 `cos²` 比较而折返，`180` 覆盖整个圆柱。
- **真实主线程限定：** 异步调用明确失败，不在选择器内部调度或阻塞等待。
- **严格动态数字：** 七个数值参数逐次使用当前版本 `PlaceholderString` 展开，只接受符合约束的最终数字，不求算式、不把错误静默变成空目标。
- 别名、默认值和近身方向放宽规则用于迁移兼容，但仍受上述边界与 MM 原生过滤器约束。

## 实现依据与验证边界

- [公共参数解析](../../adapter-common/src/main/java/top/nustar/nustarmythicmobsextension/adapter/impl/targets/CylinderConeConfig.java)、[几何判定](../../adapter-common/src/main/java/top/nustar/nustarmythicmobsextension/adapter/impl/targets/CylinderConeGeometry.java)、[主线程检查与候选筛选](../../adapter-common/src/main/java/top/nustar/nustarmythicmobsextension/adapter/impl/targets/CylinderConeSelectorAdapter.java)。
- 三版 `targets/CylinderConeSelector.java` 使用各自版本的 `PlaceholderString` 与 `SkillMetadata`，再委托公共实现；三版 helper 都关联 `TargetSelectorType.CYLINDER_CONE`。
- 同步 `skill{s=...;sync=true} @self`、`<caster.level>` 和生成命令语法已对照本地 Wiki；Wiki 语法说明不等于各版本实服兼容性验证。
- 旧文档曾记录 141 项定向测试通过及一次开发构建，这是历史开发记录，不是本次文档修改的验收结果，也不代表当前文件的构建状态。本次没有执行构建、测试或服务端操作。
- 开发验证方法见[构建说明](../building.md)。当前构建约定下，单独 `build` 成功不代表测试通过。

</details>

[返回目标选择器列表](README.md) · [更多示例](../examples/README.md) · [数字与算式区别](../expressions.md)

