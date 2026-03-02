# zRpc

English | [简体中文](README.zh-CN.md)

zRpc is a lightweight Java RPC framework based on Netty + Protobuf, currently focused on hardening `zrpc-core` for open-source readiness.

## Status

- Implemented: `zrpc-core`
- Planned: `zrpc-api`, `zrpc-adapter`, `zrpc-agent`

Note: planned modules are not implemented in this repository yet. Please treat the current code and release notes as the source of truth.

## Features

- Long-lived connections based on Netty
- Custom request/response protocol
- Async request-response correlation with timeout handling
- Safe serializer by default (Java native deserialization is not the default)
- Basic metrics and logging hooks

## Requirements

- JDK 17+
- Maven 3.9+

## Quick Start

### 1) Build and test

```bash
mvn -q test
```

### 2) Run the minimal demo

Run these classes in your IDE:

1. Server: `zrpc-core/src/test/java/com/github/zack/zrpc/core/test/demo/ServerTester.java`
2. Client: `zrpc-core/src/test/java/com/github/zack/zrpc/core/test/demo/ClientTester.java`

The client will print the RPC result.

## Serializer Strategy

- Default: `safe` (bounded type set, security-first)
- Compatibility mode: `java` (for compatibility only; not recommended for untrusted inputs)

Switch mode:

```bash
mvn -Dzrpc.serializer=java test
```

## Roadmap

- `v0.1.x`
  - Core stability hardening
  - CI, test gate, and release baseline
- `v0.2.x`
  - Introduce `zrpc-api`
  - Extend serializer/registry adapters

## Docs

- Design: `docs/open-source-readiness-design.md`
- Execution plan: `docs/open-source-readiness-execution-plan.md`
- Release runbook: `docs/release-runbook.md`

## Contributing

See `CONTRIBUTING.md`.

## Security

See `SECURITY.md` for vulnerability reporting.

## License

`LICENSE` (Apache-2.0)
