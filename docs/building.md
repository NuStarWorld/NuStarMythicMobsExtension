[返回文档首页](README.md)

# 构建与测试

本页仅供需要自行打包插件的人阅读；日常配置技能可以直接看[技能说明](mechanics/README.md)，无需执行下面的命令。

## 日常构建

```powershell
.\gradlew.bat build
```

`build` 默认编译、打包并执行 Spotless 格式检查，不编译或运行测试。Javadoc、源码包及 `build/build-metadata.properties` 仍保留；主插件 JAR 位于 `build/libs/`。这是有意调整的生命周期，构建成功不代表测试已经通过。

## 按需验证

```powershell
# 先编译受影响模块，再运行一个测试类。
.\gradlew.bat :NuStarMythicMobsExtension-adapter-common:compileJava
.\gradlew.bat :NuStarMythicMobsExtension-adapter-common:test --tests '*CylinderConeGeometryTest'

# 全部测试，或包含格式检查的完整验证。
.\gradlew.bat test
.\gradlew.bat check

# CI、发布前或需要完整验收时使用。
.\gradlew.bat check build
```

测试任务没有被禁用；显式请求模块测试时仍会运行，也可与 `build` 同一次调用。上述不带项目限定的命令应在仓库根目录执行；只运行 `:build` 或某个子模块的任务，其任务选择范围不同。

## 增量与 CI

- 默认启用 Gradle daemon，复用 JVM；保留现有串行配置，不额外启用未验证的并行或配置缓存。
- 日常不加 `clean`、`--no-daemon` 或 `--rerun-tasks`。依赖已缓存且无需更新时可追加 `--offline`；首次构建不必使用离线模式。
- CI 的普通构建和标签发布均显式执行 `check build`，保留全量测试，并移除不必要的 `clean`。
- 构建元数据任务已声明版本、提交号、构建号等输入；这些值变化时重新生成，未变化时允许增量跳过。
- 没有移除 Javadoc、源码包或发布校验，也没有引入基于命令名猜测、全局禁用测试或额外的测试开关。
