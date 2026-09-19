# Growth (Growth / SelfManagement) Project Context

This document captures the authentic development state of the **Growth** application, a Kotlin Multiplatform (KMP) and Compose Multiplatform product supporting Android and iOS. It serves as the single source of truth for the project's codebase, architecture, database schemas, business logical loops, and technical debt.

---

## 1. Product Overview
**Growth** (SelfManagement) is a personal productivity and habit-building application. It encourages users to define their goals, automatically schedules daily sub-tasks tailored to their goal categories, features an industrial-grade focus session timer that keeps tracking time seamlessly through background suspensions and app terminations, provides level systems (XP), streaks, and achievements, and incorporates an interactive "Study Companion" (搭子) matchmaking and joint focus mechanism to motivate progress.

---

## 2. Current Features & Implementation State

| Feature | Completion State | Implementation Notes / Details |
| :--- | :--- | :--- |
| **Onboarding / Goal** | **Completed** | Seamlessly navigates to Onboarding if no active goal is stored in SQLDelight. Includes [CreateGoalViewModel](class://com.example.selfmanagement.presentation.onboarding.CreateGoalViewModel) supporting creation/updates. |
| **DailyTask / Sub-tasks** | **Completed** | Generates 3 customizable sub-tasks when first launching the app on any day using [GenerateTodayTasksUseCase](class://com.example.selfmanagement.domain.usecase.GenerateTodayTasksUseCase). Uses **Duration Normalization** to balance sub-task limits against target minutes. |
| **Focus / Timer** | **Completed** | Built using [FocusViewModel](class://com.example.selfmanagement.presentation.focus.FocusViewModel). Timer flow handles Pause, Resume, and Complete accurately using milliseconds elapsed. |
| **Industrial Timer** | **Completed** | Independent of Compose's UI tick delay. Calculates the exact elapsed time by comparing epochs via [CalculateFocusTimeUseCase](class://com.example.selfmanagement.domain.usecase.CalculateFocusTimeUseCase) and `pausedDurationMillis` to tolerate system suspension/app termination. |
| **StudySession Persistence**| **Completed** | Each session's start, pause, resume, and completion are fully logged in SQLDelight (`StudySessionEntity`). |
| **Growth Record / XP** | **Completed** | Completion triggers `RecordGrowthUseCase` which saves a growth record. Growth calculation is **idempotent** utilizing a unique `sourceId` constraint. |
| **Streak** | **Completed** | Calculates consecutive active days via [CalculateStreakUseCase](class://com.example.selfmanagement.domain.usecase.CalculateStreakUseCase), tracking current and historical best streaks. |
| **Achievement** | **Completed** | Awards achievements ("First step", "3-day streak", "7-day streak", "10-hour focus") evaluated inside [CalculateAchievementUseCase](class://com.example.selfmanagement.domain.usecase.CalculateAchievementUseCase). |
| **Companion (搭子)** | **Partially Completed** | Mock implementation. Supports profile view, searching, matchmaking recommendation scores, and "Together Focus". It relies on in-memory lists; no SQLDelight table or network integration exists yet. |
| **Profile** | **Completed** | Fully functional. Loads user nickname, active goal details, level progression, total minutes studied, companion details, and historical logs. |
| **Settings** | **Completed** | Themes (System, Light, Dark) and notification flags are fully persisted and integrated with the global UI styling state. |
| **Analytics** | **Completed** | Outlines [AnalyticsTracker](class://com.example.selfmanagement.core.AnalyticsTracker) with [DebugAnalyticsTracker](class://com.example.selfmanagement.core.DebugAnalyticsTracker) logging events (`APP_OPEN`, `ONBOARDING_START`, `GOAL_CREATED`, `FOCUS_STARTED`, etc.) to stdout. |

---

## 3. System Architecture

The project adheres to a highly standardized, clean-architectural unidirectional data flow tailored for Kotlin Multiplatform:

```
                  [Compose Multiplatform UI Screen]
                                ↓ (Calls intent functions)
                    [Jetpack Lifecycle ViewModel]
                     ↓ (Exposes StateFlow / observes flows)
                    [Domain UseCase] (Encapsulates business rules)
                                ↓
                    [Domain Repository] (Abstractions)
                                ↓
         ┌──────────────────────┴──────────────────────┐
         ▼                                             ▼
[SqlDelight Production Repositories]         [Mock Repository Implementation]
         ↓ (SQLite DB Queries)                         ↓ (In-Memory updates)
    [AppDatabase] (SQLDelight driver)            [Local Memory States]
```

### File & Layer Mapping:
* **UI Layer**: Composed of Screens like `HomeScreen.kt`, `FocusScreen.kt`, `CompanionScreen.kt`, `ProfileScreen.kt` using standard Compose Multiplatform components.
* **ViewModel Layer**: Jetpack standard ViewModels in `shared/commonMain` (`HomeViewModel`, `FocusViewModel`, `CompanionViewModel`, `GrowthViewModel`, `ProfileViewModel`, `CreateGoalViewModel`).
* **UseCase Layer**: Specialized business controllers like `RecordGrowthUseCase`, `GenerateTodayTasksUseCase`, `CalculateStreakUseCase`, etc.
* **Repository Layer**: Separated into Domain Interfaces (`domain/repository/*`) and Platform/Storage implementations:
  * **Production Repositories** (SQLDelight): `SqlDelightGoalRepository`, `SqlDelightTaskRepository`, `SqlDelightStudySessionRepository`, `SqlDelightGrowthRepository`, `SqlDelightUserRepository`, `SqlDelightSettingsRepository`.
  * **Mock Repositories**: `MockCompanionRepository` manages companion structures in-memory.
  * **Alternative InMemory Repositories** (Unused in production AppContainer but fully tested): `InMemoryGoalRepository`, `InMemoryTaskRepository`, `InMemoryGrowthRepository`, `InMemoryTaskRepository`.

---

## 4. Navigation Flow & Routes

All routes are modeled inside [Screen](class://com.example.selfmanagement.ui.navigation.Screen):

1. **`onboarding`**: Initial screen for goal settings; moves to `home` once a goal is successfully persisted.
2. **`home`**: Core navigation dashboard representing the "Today" view with daily sub-tasks and goal progression.
3. **`focus/{taskId}`**: Detailed countdown timer screen for focusing.
4. **`together_focus/{taskId}`**: Dual-focus panel. Integrates user and companion focus progress.
5. **`companion`**: Central hub displaying active companion profile or candidates list.
6. **`match`**: Matchmaking candidate dashboard sorted by matching scores.
7. **`companion_detail`**: Displays complete stats of the selected companion candidate with action buttons.
8. **`growth`**: Detailed statistics highlighting XP, current level, streaks, and unlocked achievements.
9. **`profile`**: Personal panel managing user nicknames, goals edit redirects, system dark themes toggle, and app configurations.

---

## 5. Database Schema Structure (`AppDatabase.sq`)

The app utilizes a local SQLite database compiled with SQLDelight. It contains 7 tables:

### 5.1 GoalEntity
```sql
CREATE TABLE GoalEntity (
    id TEXT NOT NULL PRIMARY KEY,
    userId TEXT NOT NULL,
    category TEXT NOT NULL,
    title TEXT NOT NULL,
    targetDays INTEGER NOT NULL,
    dailyMinutes INTEGER NOT NULL,
    preferredHour INTEGER,
    createdAt INTEGER NOT NULL,
    isActive INTEGER NOT NULL DEFAULT 1
);
```

### 5.2 DailyTaskEntity
```sql
CREATE TABLE DailyTaskEntity (
    id TEXT NOT NULL PRIMARY KEY,
    userId TEXT NOT NULL,
    goalId TEXT NOT NULL,
    date TEXT NOT NULL,
    title TEXT NOT NULL,
    durationMinutes INTEGER NOT NULL,
    status TEXT NOT NULL,
    sortOrder INTEGER NOT NULL
);
```

### 5.3 StudySessionEntity
```sql
CREATE TABLE StudySessionEntity (
    id TEXT NOT NULL PRIMARY KEY,
    taskId TEXT NOT NULL,
    startTimeMillis INTEGER NOT NULL,
    endTimeMillis INTEGER,
    pausedDurationMillis INTEGER NOT NULL DEFAULT 0,
    lastPauseTimestamp INTEGER,
    durationSeconds INTEGER NOT NULL DEFAULT 0,
    status TEXT NOT NULL
);
```

### 5.4 GrowthRecordEntity
```sql
CREATE TABLE GrowthRecordEntity (
    id TEXT NOT NULL PRIMARY KEY,
    userId TEXT NOT NULL,
    date TEXT NOT NULL,
    studyMinutes INTEGER NOT NULL,
    xp INTEGER NOT NULL,
    sourceId TEXT NOT NULL
);
```

### 5.5 AchievementEntity
```sql
CREATE TABLE AchievementEntity (
    id TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    unlockedAt INTEGER NOT NULL
);
```

### 5.6 UserEntity
```sql
CREATE TABLE UserEntity (
    id TEXT NOT NULL PRIMARY KEY,
    nickname TEXT NOT NULL,
    avatarUrl TEXT
);
```

### 5.7 SettingsEntity
```sql
CREATE TABLE SettingsEntity (
    userId TEXT NOT NULL PRIMARY KEY,
    themeMode TEXT NOT NULL DEFAULT 'SYSTEM',
    notificationsEnabled INTEGER NOT NULL DEFAULT 0
);
```

---

## 6. Comprehensive Test Suite

All unit and integration tests are situated inside `shared/src/commonTest` utilizing platform-specific memory drivers:

1. **`RepositoryPersistenceTest`**:
   * Validates full CRUD on SQLite tables (Goal, DailyTask, Session, GrowthRecord, Achievements).
   * Verifies **idempotency** of growth settling to prevent double-claiming of XP.
   * Asserts **Focus Resume state recovery**: Confirms that closing and reopening the database with an active running session (`StudySessionStatus.RUNNING`) returns the intact state properly.
2. **`CalculateFocusTimeUseCaseTest`**:
   * Tests time elapsed under active running vs paused sessions.
3. **`CreateGoalUseCaseTest`**:
   * Validates target limitations and error handling for empty titles or durations.
4. **`GenerateTodayTasksUseCaseTest`**:
   * Asserts English/Exam/Programming templates duration-scaling mathematics.
5. **`GrowthCalculationTest`**:
   * Asserts levels scaling, achievements trigger states, and streaks computation.
6. **`MatchCompanionUseCaseTest`**:
   * Verifies recommendation matching scoring matrices.
7. **`ProfileViewModelTest`**:
   * Asserts states emission for user nickname loading, goal bindings, and configurations.

---

## 7. Known Tech Debt & Constraints

1. **Companion Mocking**: The entire `Companion` matchmaking framework is mocked in `MockCompanionRepository.kt` on local state. No server-side connectivity or DB-level local storage is wired.
2. **No Background Notifications**: While settings have a `notificationsEnabled` database entry, there are no actual local notification systems implemented on Android/iOS (no native `AlarmManager`, FCM, or APNs hooks).
3. **No External Analytics**: All tracking triggers print lines to the standard terminal; there is no Firebase Analytics or Sentry integration.
4. **Goal Customization Edit Constraint**: The system maps active goal edits to a single local user profile. Re-routing during mid-focus adjustments requires additional verification to avoid breaking the ongoing daily tasks flow.
