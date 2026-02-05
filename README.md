# fintech-api-tests

API test suite for fintech services using JUnit 5 and RestAssured.

## Requirements
- Java 21 (toolchain configured in `build.gradle`)
- Gradle via the wrapper (`./gradlew`)

If you use SDKMAN, the project includes `.sdkmanrc` for a consistent JDK.

## Quick Start
Run the test suite:
```bash
./gradlew test
```

Run a single test class:
```bash
./gradlew test --tests "com.example.fintech.SomeTest"
```
## Notes
- Test logging is configured to show passed/skipped/failed with full stack traces.
- The Gradle wrapper is pinned to version 9.0.0.
