# zRpc Release Runbook

## 1. Prerequisites

- OSSRH account and publishing permission
- GPG key pair
- GitHub repository secrets configured:
  - `MAVEN_USERNAME`
  - `MAVEN_PASSWORD`
  - `MAVEN_GPG_PRIVATE_KEY`
  - `MAVEN_GPG_PASSPHRASE`

## 2. Local Verification

```bash
mvn -q verify
```

## 3. Version Update

Update root `pom.xml` version from snapshot to release version, for example:

- `0.1.0-SNAPSHOT` -> `0.1.0`

Then update `CHANGELOG.md`.

## 4. Create Tag

```bash
git tag v0.1.0
git push origin v0.1.0
```

This triggers `.github/workflows/release.yml`.

## 5. Post Release

- Verify artifacts in Sonatype staging.
- Close and release the staging repository.
- Confirm dependency is searchable in Maven Central.

## 6. Next Cycle

Bump version to next snapshot, for example:

- `0.1.0` -> `0.1.1-SNAPSHOT`
