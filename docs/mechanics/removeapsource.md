返回[技能列表](README.md)

# removeapsource：按名字移除目标的 AP 属性来源

## 用途与准备

`removeapsource` 从**选中的目标**身上移除指定的 AttributePlus（AP）来源，也清理本扩展记录的对应计时任务。本身不攻击、不扣血，不是把某项属性直接改成零。

需要额外安装 AttributePlus。通常与 [apsource](apsource.md) 配套使用，来源名和目标必须对应；给自己加的属性一般也用 `@self` 移除。注册名是 `removeapsource`。

## 完整参数表

| 参数 | 简写或其他写法 | 不填写时 | 填写说明与效果 |
| --- | --- | --- | --- |
| `sourceName` | `s` | 没有可依赖的移除目标，必须明确填写 | 要移除的来源名，支持当前 MM 的字符串占位符。占位符没有返回内容时不会移除；直接省略参数则不保证安全，请明确填写。 |
| `isStartWith` | `i` | `false` | `false` 按完整名字删除；`true` 删除以给定文本开头的 API 来源。区分大小写，不是正则，也不是通配符；不要在末尾加 `*`。同时用同样的完整/前缀规则清理计时记录。 |

## 最小 YAML 例子

```yaml
# 调用 NSDocsRemoveAPSourceEntry；移除自己身上的 NSDocsBuffAttack 来源。
NSDocsRemoveAPSourceEntry:
  Skills:
    - skill{s=NSDocsRemoveAPSourceMain;sync=true} @self
NSDocsRemoveAPSourceMain:
  Skills:
    - removeapsource{sourceName=NSDocsBuffAttack} @self
```

在现有技能或怪物配置中调用 `skill{s=NSDocsRemoveAPSourceEntry} @self`，保留示例的入口与主体两段写法。这个名字对应 [apsource 示例](apsource.md) 的来源；完整添加/移除组见[属性技能组](../examples/attribute-skills.yml)。文件不会自动部署或执行，使用前确认本服 MM 与 AP 版本。

## 常见误区

- `sourceName` 写的是 `NSDocsBuffAttack` 这类**来源名**，不是“攻击力”这类属性名。名字里包含什么词不决定属性效果。
- 删除某来源不代表目标的这项属性一定归零，装备及其他来源还可继续提供它。
- `isStartWith=true` 会扩大删除范围。只使用自己专属的前缀，不要删除其他插件或其他技能的来源。
- 找不到匹配项、目标不是生物或玩家时，代码可以直接结束；返回成功不证明真的删除了属性。
- 不是清空所有 AP 数据，不移除装备，也不是清理所有持久来源的通用命令。

## 使用提醒

移除时 AP 和其他插件可能进行处理，而且可能不止一次。属性面板和其他插件显示不保证立即更新，请在本服确认效果。

<details>
<summary>移除细节与实现依据（可选阅读）</summary>

完整匹配直接调用 AP 的来源移除接口；前缀匹配先枚举目标当前的 API 来源名，再逐个移除。随后通知本扩展的临时来源管理器取消对应计时；停止正在运行的计时任务时也可能再次调用来源移除。不要假定相关 AP 事件只发生一次。

本技能没有像 `apsource` 添加玩家属性那样额外显式调用 `updateAttribute`，但这不意味着 AP 内部完全不更新：接口本身有自己的更新/事件行为。客户端展示、第三方插件状态以实际联调为准，不保证立即同步所有面板。

三版 MM 接入共用实现并要求同步执行。占位符能力由当前 MM 决定，见[表达式说明](../expressions.md)；调用与目标区别见[示例索引](../examples/README.md)。

实现依据：[RemoveAPSourceAdapter](../../adapter-common/src/main/java/top/nustar/nustarmythicmobsextension/adapter/impl/skills/mechanics/RemoveAPSourceAdapter.java) 第 43–76 行；[AttributeSourceInstance](../../plugin/src/main/java/top/nustar/nustarmythicmobsextension/entity/AttributeSourceInstance.java) 第 62–79、110–117 行。

</details>
