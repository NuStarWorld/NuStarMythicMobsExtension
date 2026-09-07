[返回目标选择器列表](README.md)

# EntitiesInTargets：再次使用已经传入的实体目标

## 用途

`@EntitiesInTargets` 把**当前技能已经收到的实体目标**复制出来，交给这一行技能使用。适合在子技能里明确沿用父技能传来的那批目标。

它不是附近实体搜索，也不是从目标周围找实体。`@EIR` 会找附近实体，本选择器不会；名字中的 `InTargets` 不代表距离范围。

## 准备

- 安装与你的服务器匹配的 MM 与本扩展，使用前确认本服版本。
- 先用调用行提供**实体**目标，例如 `skill{s=子技能} @PIR{r=6}`。如果只传位置目标，不能靠它变成实体。
- 不需要开启 MM 威胁表或其他专项设置。若以玩家观察效果，使用生存模式并留意 MM 自带过滤规则。

## 参数

| 参数 | 简写或其他写法 | 不填写时 | 填写说明与效果 |
| --- | --- | --- | --- |
| 无插件专有参数 | 无；没有注册 `EIT` 等短名 | 复制当前已有的实体目标 | 直接写 `@EntitiesInTargets`。不需要数字或 `true/false`；不存在半径、角度或中心参数。 |

MM 自己的通用过滤项由 MM 处理，不是本插件新增参数；本页不承诺它们在所有版本中一致。

## 简短例子

以下两个完整技能组可放进 MM 的 `Skills` 文件：

```yaml
# 外部调用入口；由这一层主动选取六格内的玩家。
NSDocsTargetsEntry:
  Skills:
    - skill{s=NSDocsTargetsShow} @PIR{r=6}

# 只沿用收到的那批玩家，不重新搜索。
NSDocsTargetsShow:
  Skills:
    - message{m="你是入口传入的目标。"} @EntitiesInTargets
```

在 MM 生物的实体配置 `Skills` 中挂接 `- skill{s=NSDocsTargetsEntry} @self ~onInteract`，让生存模式玩家站在六格内并右键生物。入口会先用原生 `@PIR` 选玩家，再传给 `NSDocsTargetsShow`，并非假定已经存在目标。完整文件见 [targeter-skills.yml](../examples/targeter-skills.yml)，里面只包含本选择器示例。旧版专用的 SuperForward 已[单独存放](../examples/super-forward.yml)，5.x 不要加载那份文件。

## 常见问题

- **直接调用子技能却没消息？** 先确认调用方确实传入实体目标。只调用 `NSDocsTargetsShow`、没有提供目标，不会自动搜索玩家。
- **`r=20` 为什么没扩大范围？** 本选择器不读取半径。范围要改入口的 `@PIR{r=6}`。
- **为什么换成 `@self` 后只剩自己？** 它沿用的是本次技能带来的目标，不会永久保存最早传入的那一批。
- **为什么数量或顺序变了？** 相同实体只保留一次，不保证先后顺序；之后 MM 自带的过滤规则仍可能删减目标。

<details><summary>更多说明（可选阅读）</summary>

4.9.0、5.1.0、5.6.0 三个适配模块均有实现；不代表所有其他 MM 版本都兼容。

公共实现只执行 `new HashSet<>(skillMetadataAdapter.getEntityTargets())`。它新建集合，但不克隆实体；不遍历世界、不判定距离、不自行补上施法者或触发者。原目标集合为空时结果为空；调用方提供损坏或缺失元数据时，也没有额外兜底保证。

三版 `targets/EntitiesInTargetsSelector.java` 都将结果转回对应版本实体类型，随后继续走原生实体选择器流程。实体类型、创造/旁观玩家、数量上限等最终行为应以该 MM 版本与技能管线为准。

实现依据：`adapter-common/.../targets/EntitiesInTargetsSelectorAdapter.java:25-29`；三版 `EntitiesInTargetsSelectorHelper`；`plugin/.../service/enums/TargetSelectorType.java:22-35`。本文与示例未经过实服验证。

</details>

[更多示例](../examples/README.md)
