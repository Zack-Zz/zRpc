# zRpc
## 架构

### zRpc-api
接口层

### zRpc-core
核心实现

### zRpc-agent
agent

### zRpc-adapter
适配层


## 组件

### 网络通信层（Netty）：
* 基于 Netty 实现长连接、高吞吐量的异步通信。

### 协议层：
* 自定义 RPC 协议（请求/响应格式）。
* 轻量级的序列化机制（如 ProtoBuf、Kryo）。

### 注册中心（可选）：

提供服务注册与发现（如基于 Zookeeper、Etcd、Consul）。
也可以使用本地直连模式（无注册中心）。

### 负载均衡（可选）：
轮询（Round Robin）、随机（Random）、一致性哈希（Consistent Hashing）。

### 容错机制：
* 重试机制
* 超时控制
* 熔断和限流

### 扩展机制
* SPI


### 侵入方式
* maven
* agent