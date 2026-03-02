# zRpc

[English](README.md) | 简体中文

zRpc 是一个基于 Netty + Protobuf 的轻量级 Java RPC 框架，当前聚焦在 `zrpc-core` 的稳定性与开源可用性建设。

## 当前状态

- 已实现：`zrpc-core`
- 规划中：`zrpc-api`、`zrpc-adapter`、`zrpc-agent`

说明：规划中的模块尚未在仓库落地，请以当前代码与 release note 为准。

## 特性

- 基于 Netty 的长连接通信
- 自定义请求/响应协议
- 请求-响应异步关联与超时控制
- 安全默认序列化（默认不使用 Java 原生反序列化）
- 基础指标采集与日志能力

## 环境要求

- JDK 17+
- Maven 3.9+

## 快速开始

### 1) 编译并测试

```bash
mvn -q test
```

### 2) 运行最小 Demo

在 IDE 中分别运行以下类：

1. 服务端：`zrpc-core/src/test/java/com/github/zack/zrpc/core/test/demo/ServerTester.java`
2. 客户端：`zrpc-core/src/test/java/com/github/zack/zrpc/core/test/demo/ClientTester.java`

客户端将输出 RPC 调用结果。

## 序列化策略

- 默认：`safe`（受限类型集合，安全默认）
- 兼容模式：`java`（仅兼容用途，不建议用于不可信输入）

切换方式：

```bash
mvn -Dzrpc.serializer=java test
```

## Roadmap

- `v0.1.x`
  - 完成 core 稳定性治理
  - 建立 CI、测试门禁和发布基线
- `v0.2.x`
  - 规划引入 `zrpc-api`
  - 扩展序列化/注册中心适配能力

## 文档

- 设计文档：`docs/open-source-readiness-design.md`
- 执行方案：`docs/open-source-readiness-execution-plan.md`
- 发布手册：`docs/release-runbook.md`

## 贡献

请先阅读 `CONTRIBUTING.md`。

## 安全

安全漏洞提交流程见 `SECURITY.md`。

## 许可证

`LICENSE`（Apache-2.0）
