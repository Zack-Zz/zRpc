# Contributing to zRpc

## Development Setup

1. Install JDK 17+ and Maven 3.9+.
2. Run tests:

```bash
mvn -q test
```

## Branch and Commit

- Use short-lived feature branches.
- Keep each PR focused on one topic.
- Use conventional commit style when possible:
  - `feat:`
  - `fix:`
  - `docs:`
  - `test:`
  - `chore:`

## Pull Request Checklist

- Tests pass locally (`mvn -q test`).
- Added or updated tests for behavior changes.
- Updated docs for user-visible changes.
- Added entry in `CHANGELOG.md` for notable changes.

## Code Guidelines

- Keep public API changes minimal in patch releases.
- Prefer explicit error handling over silent fallbacks.
- Do not add insecure defaults for network input handling.

## Reporting Issues

Use GitHub issue templates:
- bug report
- feature request
