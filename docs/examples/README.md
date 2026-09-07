[返回文档首页](../README.md)

# 完整示例与使用步骤

这里的 YAML 是技能组示例，不会自动安装到服务器，也不会仅因保存就自动施放。**先在测试服选一组验证，不要把整个目录直接复制到正式服。**

## 选择一个例子

| 文件 | 用途与调用入口 | 使用前准备 |
| --- | --- | --- |
| [attribute-skills.yml](attribute-skills.yml) | `NSDocsAPMMEntry` 攻击；`NSDocsAPSourceEntry` 添加增益；`NSDocsRemoveAPSourceEntry` 移除增益；`NSDocsSXMMEntry` 攻击；`NSDocsFastAPEntry` 倍率攻击 | 按组选择 AP 或 SX；属性名按服替换。攻击组需要施法者已有非自身的存活目标 |
| [fastap-base-multipliers.yml](fastap-base-multipliers.yml) | `NSFastAPGlobalEntry` 全局倍率；`NSFastAPCombinedEntry` 混合倍率与加值；`NSFastAPDynamicEntry` 按血量等级变化 | AP；属性名按服替换；需要非自身当前目标 |
| [cylinder-cone.yml](cylinder-cone.yml) | 推荐从 `NSDocsConeEntry` 看精确十格扇形；`NSDocsConeLevelEntry` 按 MM 等级改变半径；保留旧入口 `NSConeEntry`、`NSConeLevelEntry` | 停用同名旧脚本；完整保留两段写法；会造成真实伤害 |
| [targeter-skills.yml](targeter-skills.yml) | `NSDocsTargetsEntry` 搜索六格内玩家，再由子技能沿用这些目标并发消息 | 生存玩家在 MM 生物六格内右键 |
| [super-forward.yml](super-forward.yml) | `NSDocsForwardEntry` 在生物水平前方显示粒子 | **仅旧版接入使用，5.x 不要加载此文件** |
| [threat-skills.yml](threat-skills.yml) | `NSDocsThreatAdd/Set/Delete/Top` 修改分数；`NSDocsThreatTransfer` 转移分数 | 先读[威胁页](../mechanics/nustarthreat.md)；受伤用普通僵尸测试，转移另用玩家右键；一次只挂一种模式 |
| [placeholder-skills.yml](placeholder-skills.yml) | 威胁第一名查询的写法参考 | **当前有卡服风险，全注释、默认无可运行技能；修复并验证前不要启用** |

只想尝试不造成伤害的普通例子，可以先用 `targeter-skills.yml`。计算公式另有[完整的技能和怪物示例](../expressions.md)，无需 PAPI/AP。

## 文件放在哪里

1. 备份原配置，确认本扩展、MM 和 `minecraft-next` 正常加载；AP/SX 按所选例子准备。
2. 在 `plugins/MythicMobs/Skills/` 新建 `.yml` 文件，放入选中的完整技能组。`Entry` 入口和它调用的主体要一起复制。
3. 如例子需要怪物调用，在 `plugins/MythicMobs/Mobs/` 对应怪物的 `Skills` 下加调用行。**不要把怪物定义混进技能文件。**
4. 用本服 MM 的重载命令（通常 `/mm reload`）重新读取技能，检查日志后再触发。
5. 文档片段与独立文件往往是同一个例子，只选择一份加载，避免技能名重复。

## 一个最简单的接法

先安装上表的 `targeter-skills.yml`，再在测试服的 Mobs 文件中添加：

```yaml
NSDocsWelcomeVillager:
  Type: VILLAGER
  Display: '选择器测试村民'
  Health: 40
  Skills:
    # 右键触发，六格内的玩家会收到一条消息。
    - skill{s=NSDocsTargetsEntry} @self ~onInteract
```

重载 MM 后召唤 `NSDocsWelcomeVillager`，以生存模式站在六格内右键它。入口会先选玩家，子技能再发送消息。这只是接法示范；威胁技能、属性攻击有不同目标要求，不要把这一条触发方式套到全部例子上。

## 使用检查清单

- 属性名是否是本服真实存在的名称？AP 和 SX 的格式不要混用。
- 技能是否真的被调用？当前目标是否存在？`@target` 不会自动搜索最近生物。
- 圆柱的半角、额外范围和近身设置是否符合预期？不要把 `a=360` 当作合法角度。
- 不支持的组要从准备加载的文件中移除。**没有触发不等于加载时不会报错。**
- 需要改插件主配置时，按[配置说明](../configuration.md)操作；它与 MM 技能文件不是同一份配置。

这些示例只经过源码对照及文件检查，未在实际服务器运行。出现异常先停用对应例子并检查日志，不要反复触发伤害、增益或转移技能。
