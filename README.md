# actions-flow-test

验证「GitHub 公开仓库 + Actions 自动构建 + 发布 Release」全流程的测试项目，同时作为 D:\VibeCodingSpace 后续所有软件项目的发版模板。

## 发版方式

本地只需两条命令：

```bash
git tag v0.1.0
git push origin v0.1.0   # 推送标签即触发云端构建与发版
```

推送 `v*` 标签后，安卓构建流水线并行启动（轻量 release 流水线仅 tag 触发）：

| 触发 | 行为 |
|------|------|
| push main | 安卓构建但**不发布**：验证主干可构建 + 写入 Gradle 缓存（供所有 tag 复用） |
| push v* 标签 | 安卓构建 + 自动发布 Release（读 main 写的缓存，冷启动变热启动） |

> Actions 缓存按 ref 隔离：只有 main 写的缓存才能被 tag 构建读取，纯 tag 触发会导致缓存永远命不中。

## 安卓构建提速要点（已内置）

- 只构建 arm64-v8a（`ndk.abiFilters`），包体与构建时间双降
- `gradle/actions/setup-gradle` 自动缓存 Gradle 依赖与构建缓存，第二次构建显著提速
- 版本号由标签自动注入（`appVersionName`），无需手改代码
- 关闭 release 前 lint 强制检查（模板项目防卡 + 提速）
- 云端构建，本地零环境依赖：不需要装 JDK / Android SDK / Gradle

## 产物

每次发版自动产出：

- `flow-test-<tag>-arm64-v8a-unsigned.apk` — 未签名 APK（正式项目需接入 keystore 签名）
- `*.sha256` — 校验文件
- 自动生成的 Release Notes
