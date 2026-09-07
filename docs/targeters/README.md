[返回文档首页](../README.md)

# 目标选择器

选择器写在技能行的 `@` 后面，决定“对谁生效”或“在哪个位置生效”。选中位置不等于选中那个位置上的生物。

| 选择器 | 选出来的是什么 | 适合用途 |
| --- | --- | --- |
| [EntitiesInTargets](EntitiesInTargets.md) | 前面传入的实体目标 | 子技能沿用同一批目标，不重新查找 |
| [SuperForward](SuperForward.md) | 施法者前方的一个位置 | 播放粒子等位置效果；**5.x 不要使用** |
| [NuStarCylinderCone](NuStarCylinderCone.md) | 圆柱扇形内的实体 | 扇形挥击、四周范围攻击、覆盖高低差 |

## 圆柱选择器的名称

`@NuStarCylinderCone`、`@NSCylinderCone`、`@TailedBeastCylinderCone`、`@PTBCylinderCone` 是同一功能的四种写法。

- `CYLINDER_CONE` 不是配置名称。
- 旧脚本的 `PTBCylinder` 不是本扩展注册的名称。要保持四周范围，可按圆柱页使用 `@NSCylinderCone{a=180;...}`，并保留原半径和上下范围。
- 不要同时启用注册了同名选择器的旧脚本。

## 先确认这些

- 圆柱选择器请完整复制“入口 + 主体”两段技能。只给同一行加 `sync=true` 不能代替这两段。
- `SuperForward` 目前仅在项目旧版接入中实现，示例已[单独存放](../examples/super-forward.yml)，不要在 5.x 加载。
- 本插件的选择结果还可能被 MM 自身规则筛掉；“允许玩家”不代表必定选中创造或旁观玩家。
- 参数没有说明遮挡或阵营判断时，不要假设它会自动避开墙后的实体或友军。

[完整示例与加载步骤](../examples/README.md) · [技能列表](../mechanics/README.md)
