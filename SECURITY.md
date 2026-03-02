# Security Policy

## Supported Versions

Security fixes are provided for the latest development line (`0.1.x`) first.

## Reporting a Vulnerability

Do not disclose vulnerabilities publicly before a fix is available.

Please report with:

- affected version/commit
- reproduction steps
- expected vs actual behavior
- impact assessment

## Response Process

- Acknowledgement target: within 72 hours
- Initial triage: within 7 days
- Fix timeline depends on severity and complexity

## Security Notes

- Default serializer is `safe`.
- Java native serialization (`-Dzrpc.serializer=java`) is compatibility-only and not recommended for untrusted inputs.
