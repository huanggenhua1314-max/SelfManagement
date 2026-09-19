# AI Assistant Development Rules: SelfManagement (Growth)

To maintain the architectural integrity, test coverage, and cross-platform consistency of the **Growth** project, all AI assistant actions must strictly adhere to the following engineering rules.

---

## 1. Architectural Integrity

* **Kotlin Multiplatform First**: Implement all business logic, ViewModels, UseCases, Repositories, and UI in `shared/commonMain`.
* **Zero Platform Pollution**: Do not import Android-only APIs (e.g., `android.util.Log`, `Context`, standard JVM imports not supported by Kotlin Native) inside `commonMain`. Utilize platform-specific abstractions with `expect`/`actual` if required.
* **Strict Unidirectional Data Flow (UDF)**:
  ```
  UI Screen (Compose) ──[Invokes Intent]──> ViewModel (StateFlow) ──> UseCase ──> Repository
  ```
* **No Direct DB Access inside ViewModels**: ViewModels must never query DAOs or SQLDelight Queries directly. They must operate solely via **UseCases** (for domain rules) or **Repositories** (for simple CRUD actions).
* **Clock Isolation**: Never use raw `System.currentTimeMillis()` or `Clock.System.now()` inside business rules. Always inject and use [AppClock](class://com.example.selfmanagement.core.AppClock) to ensure mockability and test reliability.

---

## 2. Timer & Business Constraints

* **Do Not Rely on UI Loop Delays for Timekeeping**: Timer countdowns and durations must always compare timestamps from `AppClock` through `CalculateFocusTimeUseCase` to ensure they are immune to system pauses, app suspensions, or UI lag.
* **Idempotency of Actions**: Focus completion and growth settlement must always be protected against double-clicks or multiple triggers. Always check `isSourceSettled` before persisting any `GrowthRecord`.

---

## 3. Database Modifications (`AppDatabase.sq`)

* When modifying database structures, always update `AppDatabase.sq` in `shared/src/commonMain/sqldelight`.
* Keep naming conventions consistent: table entities should end with the suffix `Entity` (e.g., `GoalEntity`, `DailyTaskEntity`).
* Ensure matching companion queries are written safely, mapping foreign keys or dependencies clean-up manually where necessary.

---

## 4. UI Consistency & Style

* **System Design Compliance**: Always use custom material UI attributes configured in `Theme.kt` and `Color.kt`. Avoid hardcoded colors or direct font definitions inside local layouts.
* **Respect Scaffolding**: Keep `Scaffold` controls uniform. Hide the global bottom navigation bars inside focus views, onboarding screens, or detailed companion sheets.

---

## 5. Automated Tests Check

* Every added UseCase or ViewModel logic must be accompanied by relevant unit tests inside `shared/src/commonTest`.
* Verify that any change to repositories passes `RepositoryPersistenceTest` to prevent regressions in KMP SQLite database layers on both Android and iOS targets.
* Ensure tests run successfully on the local environment before declaring a task complete.
