# zRpc 开源化开发方案（执行版）

## 1. 文档目的

本方案基于以下输入生成：

- 设计文档：[open-source-readiness-design.md](/Users/zhouze/Documents/git-projects/zRpc/docs/open-source-readiness-design.md)
- 核心模块审查结果（P0/P1/P2 风险项）

目标是把“方向性设计”转成“可排期、可拆解、可验收”的执行计划。

## 2. 执行范围与原则

## 2.1 执行范围（本轮）

- 仅聚焦 `zrpc-core` 的开源可用性
- 不在本轮拆分 `zrpc-api / zrpc-adapter / zrpc-agent`
- 输出物必须可直接用于发起外部试用版本（`v0.1.0-rc1`）

## 2.2 执行原则

- 先“安全与正确性”，再“可维护性与体验”
- 先收敛接口与行为，再扩展功能面
- 每一批次都要有可运行结果与回归用例

## 3. 里程碑与时间表

建议从 **2026-03-02** 启动，按 4 个里程碑推进：

### M0：准备与基线冻结（2026-03-02 ~ 2026-03-04）

目标：
- 冻结当前行为基线，补齐任务看板与分支策略

交付：
- 本执行方案确认版
- Issue 分解（按 M1/M2/M3）
- 主分支保护规则（PR + CI 必过）

验收：
- 所有任务都有优先级、负责人、验收标准

### M1：P0 问题清零（2026-03-05 ~ 2026-03-14）

目标：
- 清零高危问题（安全、超时泄漏、失败语义）

交付：
- 替换/封禁不安全反序列化默认路径
- 请求超时异常化 + pending 清理机制
- 调用链关键回归测试（成功/超时/异常）

验收：
- 无 `readObject()` 默认网络入口
- 超时场景无 pending 残留
- 测试稳定通过，行为可复现

### M2：P1 稳定性收敛（2026-03-15 ~ 2026-03-26）

目标：
- 修复核心并发和生命周期设计问题

交付：
- “先注册 pending 再发送”请求路径
- 客户端连接生命周期重构（`connect` 非阻塞 + `close`）
- primitive 参数分发兼容修复
- 错误模型标准化（异常类型与错误码）

验收：
- 竞态回归用例通过
- 客户端 API 不再要求业务侧额外起线程维持连接
- primitive + 包装类型调用用例通过

### M3：开源发布基线（2026-03-27 ~ 2026-04-10）

目标：
- 打通开源治理、CI 门禁和 RC 发布能力

交付：
- LICENSE / CONTRIBUTING / SECURITY / CHANGELOG
- CI（build + test + static + coverage gate）
- README 重构（快速开始 + 能力边界 + roadmap）
- 发布流程演练并产出 `v0.1.0-rc1`

验收：
- 干净环境可在 15 分钟内跑通最小 demo
- PR 必经 CI，主分支可持续绿色
- 产出可下载试用版本与变更说明

## 4. 工作分解结构（WBS）

## 4.1 EPIC-A：安全与序列化治理（P0）

A1. 引入序列化 SPI（接口 + 工厂）
- 任务：
  - 定义 `Serializer` 接口
  - 提供 `JavaSerializer`（默认禁用）和安全默认实现（建议 JSON/Protobuf 结构化）
  - 协议中带上 serializer 标识（可先内部字段）
- 验收：
  - 默认配置下不依赖 `ObjectInputStream.readObject()`

A2. 风险开关与文档
- 任务：
  - 若保留 Java 序列化，必须显式开关开启
  - 增加安全告警日志和文档说明
- 验收：
  - `SECURITY.md` 明确风险边界和建议配置

## 4.2 EPIC-B：请求生命周期与并发正确性（P0/P1）

B1. 调整发送顺序
- 任务：
  - 先 `register(requestId)`，再 `writeAndFlush`
  - 发送失败时回滚 pending 并完成异常
- 验收：
  - “响应先到”仿真测试通过

B2. timeout/cancel 语义统一
- 任务：
  - `RequestFuture#get(timeout)` 超时抛出 `RpcTimeoutException`
  - timeout 后强制从 pending 移除
- 验收：
  - 压测下 pending 不持续增长

B3. 错误模型标准化
- 任务：
  - 建立 `RpcException` 体系
  - 映射传输错误、超时、序列化、服务端错误
- 验收：
  - 客户端调用侧能稳定区分错误类型

## 4.3 EPIC-C：连接生命周期与调用体验（P1）

C1. `NettyClient` 生命周期改造
- 任务：
  - `connect()` 建立连接后返回，不阻塞到 close
  - 新增 `close()` 主动关闭连接与线程组
  - 提供 `isConnected()`/健康状态
- 验收：
  - demo 与集成测试中业务线程不被 `connect()` 阻塞

C2. 服务端优雅关闭补齐
- 任务：
  - `boss/worker/business` 统一关闭
  - 优化 close 超时等待策略
- 验收：
  - 集成测试退出无资源泄漏告警

## 4.4 EPIC-D：调用分发与协议健壮性（P1/P2）

D1. 参数类型解析增强
- 任务：
  - primitive 类型映射（`int.class`、`long.class` 等）
  - 参数与签名严格校验，错误信息可诊断
- 验收：
  - primitive 与包装类型方法都可正确调用

D2. 协议与编解码测试补齐
- 任务：
  - 编解码边界测试（非法长度、空包、损坏包）
  - 请求/响应一致性与 requestId 校验
- 验收：
  - 核心 codec/handler/dispatcher 覆盖率达到目标

## 4.5 EPIC-E：工程化与开源治理（M3）

E1. POM 与发布元数据
- 任务：
  - 补齐 name/description/url/licenses/developers/scm
  - 完善 source/javadoc 插件
- 验收：
  - 发布前检查无元数据缺失

E2. CI 与质量门禁
- 任务：
  - GitHub Actions：build/test/static/coverage
  - 覆盖率阈值：整体 >= 70%，核心包 >= 85%
- 验收：
  - 主分支只接受通过门禁的 PR

E3. 文档与模板
- 任务：
  - README 重构
  - 贡献/安全/变更文档与模板上线
- 验收：
  - 外部贡献者可按文档独立提交首个 PR

## 5. 测试与验收策略

## 5.1 测试优先级

P0 用例（必须先落地）：
- 超时与 pending 清理
- 发送失败回滚 pending
- 序列化策略安全默认值

P1 用例：
- 响应先到竞态
- 客户端非阻塞连接生命周期
- primitive 参数调用

P2 用例：
- 指标采集正确性
- 日志参数格式化正确性

## 5.2 质量门禁

- 单元测试全通过
- 新增逻辑必须有回归测试
- 不允许引入“只靠 demo 证明正确”的改动

## 6. 分支与提交流程

- 分支命名：`codex/m1-...`、`codex/m2-...`、`codex/m3-...`
- 合并策略：小步 PR，单 PR 单主题
- 建议 PR 粒度：
  1. 序列化与安全
  2. dispatcher 并发修复
  3. client 生命周期
  4. 测试补齐
  5. CI 与治理文档

## 7. 风险与回滚预案

风险 R1：序列化切换影响兼容性  
预案：
- 提供临时兼容开关
- 先在内部 demo/集成测试灰度，再对外默认启用

风险 R2：并发修复引入隐藏死锁  
预案：
- 增加并发压力测试与超时保护
- 关键路径保持无锁或最小锁粒度

风险 R3：改造期间 API 抖动过大  
预案：
- 在 `v0.1.x` 周期尽量保持接口稳定
- 破坏性变更推迟到 `v0.2.0`

## 8. 版本发布策略

- `v0.1.0-rc1`（目标日期：2026-04-10）
  - 满足开源最小基线
- `v0.1.0`
  - rc 反馈修复后发布稳定版
- `v0.2.0`
  - 再考虑 `zrpc-api` 拆分与扩展模块演进

## 9. 立即执行清单（本周）

1. 创建 M1 对应 issues（A1/A2/B1/B2）
2. 先做 P0 两项：
   - 序列化默认策略整改
   - timeout + pending 清理修复
3. 同步补 3 条核心回归测试：
   - timeout cleanup
   - send fail rollback
   - response-race safety
4. 建立最小 CI（build + test）并设为必需检查

---

本文件是执行约束文档。若执行中发生方案调整，必须同步更新本文档对应章节（里程碑、WBS、验收标准、风险预案）。
