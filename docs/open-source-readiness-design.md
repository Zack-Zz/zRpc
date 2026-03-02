# zRpc 开源化详细设计文档

## 1. 文档信息

- 项目：zRpc
- 文档版本：v1.0
- 日期：2026-03-02
- 状态：Draft
- 目标读者：核心开发者、贡献者、早期使用者

## 2. 背景与问题陈述

当前仓库已经具备可运行的 RPC 原型能力（基于 Netty + Protobuf 消息 + 反射调用），但距离“可被外部团队稳定采用”的开源级别还有明显差距，主要体现在：

- 项目元信息与发布链路缺失（Maven Central 级别的 POM 元数据、签名、版本治理）。
- 文档以架构说明为主，缺少快速上手、API 用法、兼容策略和运维说明。
- 自动化测试覆盖不足，现有 demo 偏手工验证，难以支撑版本迭代。
- 运行时稳定性与安全边界尚未定义（反序列化策略、请求生命周期、异常模型）。
- CI/CD 与社区协作机制未建立（质量门禁、Issue/PR 规范、安全响应流程）。

本设计文档用于定义 zRpc 从“原型”演进到“开源可用”的目标架构、工程约束和实施里程碑。

## 3. 设计目标

### 3.1 总体目标

在不推翻现有核心实现的前提下，建立 zRpc 的开源最小可用基线（Open-Source Minimum Viable Foundation, OSMVF），使项目具备：

- 可被外部开发者快速运行并理解。
- 可持续发布与回归验证。
- 可追踪风险并稳定迭代。

### 3.2 非目标（本阶段不做）

- 不追求一次性覆盖完整服务治理体系（注册中心、熔断、限流等全部高级能力）。
- 不立即支持多语言 SDK。
- 不承诺企业级 SLA。

## 4. 现状基线（As-Is）

### 4.1 仓库结构

- 根模块 `zrpc`（聚合 POM）
- 子模块 `zrpc-core`
- 当前尚无 `zrpc-api`、`zrpc-agent`、`zrpc-adapter` 的实际代码模块

### 4.2 当前核心能力

- 网络层：Netty client/server 基础通信
- 协议层：`RequestMessage` / `ResponseMessage` Protobuf 定义
- 调度层：`RequestPendingDispatcher` + `RequestFuture` 进行请求响应关联
- 服务调用：`ServiceRegistry` 注册实例，`ServerHandler` 通过反射调用方法

### 4.3 当前风险点（与代码一致）

- 请求发送与 pending 注册顺序存在竞态窗口（可能先收到响应，再登记 Future）。
- 使用 Java 原生对象序列化/反序列化，存在开源场景安全风险。
- 客户端连接生命周期与业务调用耦合偏紧，不利于 SDK 易用性。
- 超时、异常、错误码语义未标准化。
- 自动化测试与可观测性能力不足。

## 5. 目标架构（To-Be）

### 5.1 模块规划

阶段化规划如下：

- Phase A（当前仓库内落地）
- `zrpc-core`：协议、传输、调度、调用、错误模型、基础观测

- Phase B（后续新增）
- `zrpc-api`：公开接口、注解、客户端配置模型
- `zrpc-adapter-*`：序列化/注册中心扩展适配
- `zrpc-agent`：可选增强能力（调用拦截、埋点）

说明：在 `Phase B` 代码落地前，README 与文档不得宣称已提供对应模块能力。

### 5.2 关键技术决策

- 传输层：继续采用 Netty
- 消息定义：继续采用 Protobuf，保留字段向后兼容策略
- 调用模型：短期保留同步调用接口，同时补齐异步 Future/Callback 能力
- 序列化策略：默认安全序列化器（禁用不受控 Java 原生反序列化）
- 扩展方式：通过 SPI 提供序列化器与负载策略插拔点

### 5.3 请求生命周期设计

请求执行的标准流程：

1. Client 生成全局唯一 requestId
2. 先将 requestId -> pending promise 注册到调度器
3. 再发送请求到 Netty channel
4. ClientHandler 收到响应后按 requestId 投递并清理 pending
5. 调用线程按 timeout 等待结果并完成异常映射

强约束：

- 任何异常路径都必须清理 pending，避免内存泄露。
- timeout 与 cancel 必须可观测（计数器 + 日志 + 可选事件）。

### 5.4 错误模型设计

统一错误抽象：

- `RpcException`（基类）
- `RpcTimeoutException`
- `RpcTransportException`
- `RpcSerializationException`
- `RpcServerException`

`ResponseMessage` 中建议新增（下一版本协议）：

- `errorCode`
- `errorMessage`
- `serverTraceId`（可选）

兼容策略：

- 协议升级后保留对旧响应格式的兼容解码逻辑一个主版本周期。

### 5.5 并发与资源生命周期

客户端：

- 连接管理与请求调用解耦，提供 `start()/stop()` 或 `connect()/close()` 生命周期 API
- 调用侧禁止在连接线程阻塞等待业务结果

服务端：

- 明确 boss/worker/business 线程池职责
- 提供可配置参数（端口、线程数、空闲超时、backlog）
- 统一优雅关闭流程与超时策略

### 5.6 安全设计

- 默认禁用 Java 原生反序列化作为公网可达场景的默认方案
- 提供白名单类校验或改用结构化序列化协议
- 明确边界：zRpc 默认假设“可信内网”，若跨信任域需额外启用安全策略
- 发布 `SECURITY.md`，定义漏洞提交流程与响应 SLA

### 5.7 可观测性设计

指标（最小集）：

- `rpc_requests_total`
- `rpc_requests_success_total`
- `rpc_requests_timeout_total`
- `rpc_requests_error_total`
- `rpc_latency_ms`（直方图）
- `rpc_pending_requests`

日志（结构化字段建议）：

- `requestId`
- `service`
- `method`
- `elapsedMs`
- `remoteAddr`
- `result`（success/timeout/error）

## 6. 工程化与开源治理设计

### 6.1 构建与发布

根 POM 与模块 POM 补齐：

- `name`
- `description`
- `url`
- `licenses`
- `developers`
- `scm`

发布能力：

- Maven Central（OSSRH）发布流程
- GPG 签名
- 版本号采用 SemVer（`MAJOR.MINOR.PATCH`）

### 6.2 CI/CD 设计

PR 流水线（必跑）：

1. compile
2. unit test
3. integration test
4. style/static check（Checkstyle + SpotBugs）
5. coverage gate（JaCoCo）

主干流水线（发布前）：

1. full test matrix（JDK 17/21）
2. dependency vulnerability scan
3. snapshot publish（可选）

质量门禁建议：

- 单元测试覆盖率 >= 70%
- 核心包（codec/dispatcher/handler）覆盖率 >= 85%

### 6.3 文档与社区治理

新增治理文档：

- `CONTRIBUTING.md`
- `CODE_OF_CONDUCT.md`
- `SECURITY.md`
- `CHANGELOG.md`
- `.github/ISSUE_TEMPLATE/*`
- `.github/pull_request_template.md`

README 结构重写为：

1. 项目简介
2. 快速开始（最小可运行示例）
3. 核心概念
4. API 示例
5. 兼容性声明（JDK、版本）
6. Roadmap
7. 贡献指南链接

## 7. 测试设计

### 7.1 测试分层

- 单元测试：工具类、编解码、ID 生成、异常映射
- 组件测试：Dispatcher 生命周期、timeout/cancel、序列化边界
- 集成测试：client-server 回环调用（成功、超时、异常、并发）
- 回归测试：协议向后兼容、错误码兼容

### 7.2 关键测试用例

- 正常调用：返回值与 requestId 一致性
- 超时调用：达到超时阈值后异常类型正确，pending 已清理
- 服务端异常：客户端得到标准化错误信息
- 高并发：并发请求下无丢响应、无死锁、无 pending 泄露
- 协议兼容：旧版本响应可被新版本客户端解析

## 8. 里程碑计划

### M1（1-2 周）：开源基线

- 补齐 LICENSE + POM 元信息 + 治理文档骨架
- 建立 CI 流水线与覆盖率门禁
- README 快速开始可跑通

验收标准：

- 新用户在干净环境 15 分钟内可完成 demo
- PR 默认触发并通过 CI

### M2（2-4 周）：核心稳定性

- 修复请求注册竞态
- 规范错误模型与超时逻辑
- 增加核心包测试覆盖

验收标准：

- 核心包覆盖率达到目标
- 压测下无明显 pending 增长趋势

### M3（4-8 周）：发布可用

- 完成首次 RC 发布流程演练
- 产出 changelog 与升级说明
- 收集外部试用反馈并修复关键问题

验收标准：

- 完成至少 1 次公开可用版本发布
- 无 P0/P1 已知缺陷遗留

## 9. 风险与缓解

- 风险：序列化改造带来兼容性影响
- 缓解：引入双栈兼容期与灰度开关

- 风险：测试补齐成本高于预期
- 缓解：优先覆盖高风险路径（dispatcher + handler + codec）

- 风险：功能承诺超前于实现
- 缓解：Roadmap 显式标注阶段状态，README 只写已交付能力

## 10. 验收清单（Definition of Done）

满足以下条件视为“达到开源可用级别”：

- 合规：LICENSE、SCM、贡献与安全文档齐全
- 质量：CI 全绿且有覆盖率门禁
- 可用：README 快速开始可复现
- 稳定：核心链路有自动化回归测试
- 发布：具备标准化版本发布和变更记录

## 11. 下一步执行建议

按文档分三批提交 PR：

1. `docs/governance + build metadata + CI`
2. `core stability + tests`
3. `release pipeline + first RC`

每批 PR 控制在可审查范围（建议 < 800 行净变更），并附带验收截图或日志链接。
