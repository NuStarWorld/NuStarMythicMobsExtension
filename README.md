# NuStarMythicMobsExtension

为 MythicMobs 补充属性攻击、属性增益、威胁记录和目标选择功能。通过 YAML 技能配置使用，适合服主和技能配置人员查表、参考示例后调整。

## 使用前准备

- 必需：**MythicMobs**、**minecraft-next** 和本扩展。
- 按功能准备：AP 系技能需要 **AttributePlus**；`sxmm` 需要 **SX-Attribute**。PAPI 数值变量另需 **PlaceholderAPI** 及对应扩展。
- 先备份配置，在测试服确认版本和效果。不要把“能加载”当作所有技能均可用；`SuperForward` 目前不支持本项目的 5.x 版本接入。

## 文档导航

| 想了解什么 | 去哪里看 |
| --- | --- |
| 不知道从哪里开始、按效果找功能 | [文档首页](docs/README.md) |
| 属性攻击、添加或移除属性、调整威胁记录 | [技能说明（6 项）](docs/mechanics/README.md) |
| 决定技能对谁生效、在哪个位置生效 | [目标选择器（3 类）](docs/targeters/README.md) |
| 显示威胁第一名的名称 | [占位符说明与风险提示](docs/placeholders/README.md) |
| 修改白名单、调试开关和变量 | [插件配置](docs/configuration.md) |
| 按等级、血量计算技能数值 | [计算公式与变量](docs/expressions.md) |
| 复制完整技能组，查看调用方法 | [示例与使用步骤](docs/examples/README.md) |

**当前注意：**威胁第一名占位符有卡服风险，暂不建议启用；其示例默认全注释。部分参数也有已知限制，详见对应功能页。本次文档与示例只做文件和源码核对，未经过实服验证。

自行打包插件可看[构建说明](docs/building.md)，日常配置技能无需阅读。许可证见 [GPL](LICENSE.txt)，第三方说明见 [NOTICE](NOTICE.txt)。
