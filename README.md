# fintech-api-tests

API test suite for fintech services using JUnit 5 and RestAssured.

## Requirements
- JDK 21 installed (tests compile/run with Java 21; toolchain configured in `build.gradle`)
- SDKMAN available (project includes `.sdkmanrc`)
- No local Gradle install required (wrapper included)

The Gradle wrapper still needs a local JDK to run, so ensure `JAVA_HOME` points to JDK 21 (or at least JDK 17+).

## Quick Start
Run the test suite:
```bash
sdk env
./gradlew test
```

Run a single test class:
```bash
./gradlew test --tests "com.example.fintech.SomeTest"
```
## Test Coverage
- Auth tests cover registration and login flows.
- Account tests cover funding, balance retrieval, and invalid amount handling.
- Transaction tests cover successful payments, insufficient funds, and history listing.
- End-to-end tests cover authenticated payment flow and balance consistency.
- Security tests verify unauthenticated (401) and unauthorized (403) access across account and transaction endpoints.

## Notes
- Test logging is configured to show passed/skipped/failed with full stack traces.
- The Gradle wrapper is pinned to version 9.0.0.
