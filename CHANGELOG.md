# Changelog

All notable changes to this project will be documented in this file.

The format is based on Keep a Changelog.

## [Unreleased]

### Added

- Open-source readiness design and execution documents.
- Safe serializer with compatibility toggle (`zrpc.serializer`).
- RPC exception hierarchy for timeout, transport, and server-side failures.
- Request lifecycle tests and Netty lifecycle integration test.

### Changed

- `RpcClient` now registers pending request before send and cleans up on timeout/failure.
- `NettyClient` lifecycle refactored: non-blocking `connect()` and explicit `close()`.
- `TargetServer` lifecycle refactored with explicit `start()/close()`.
- Logger now supports `{}` placeholder formatting.

### Fixed

- Pending request leak on timeout path.
- Parameter type handling for primitive types in server-side invocation.
- Metric handler timeout gauge mapping.
