# Product Development Roadmap: Growth

Growth (SelfManagement) is a cross-platform (KMP + Compose Multiplatform) application designed for high-efficiency habit tracking. This roadmap details the development milestones completed so far and lists future steps according to immediate priorities.

---

## Completed Phases

### Phase 0: Foundations
* Set up KMP structure supporting `:androidApp` and `:shared` modules.
* Integrated SQLDelight to compile cross-platform databases.

### Phase 1: Onboarding & Goal Settings
* Created `GoalEntity` database query contracts.
* Implemented `CreateGoalViewModel` and corresponding interactive validation layouts.

### Phase 2: Today's Tasks
* Engineered `DailyTaskEntity` with SQLite migrations.
* Created `GenerateTodayTasksUseCase` with duration normalization algorithms that split the daily goal into distinct sub-tasks based on categorized templates.

### Phase 3: Focus & Timer Core
* Implemented `StudySessionEntity` to track session lifecycle states.
* Designed the industrial-grade `CalculateFocusTimeUseCase` to ensure precise calculations immune to system suspensions or app backgrounding.

### Phase 4: Gamification (XP, Level & Streaks)
* Developed mathematical rules to map XP to 6 progressive level thresholds.
* Added `CalculateStreakUseCase` with tests to accurately compute active daily streak sequences.

### Phase 4.5: Gamification (Achievements)
* Integrated `AchievementEntity` tables.
* Added dynamic achievement unlocking rules triggered after completed focus sessions.

### Phase 5: Interactive Companions (Mock)
* Engineered matchmaking algorithms based on category preferences and matching scores.
* Implemented dual status-board layouts for joint study sessions with a mock partner.

### Profile V1 & Settings
* Developed user configuration tables to persist theme choices and notification options.
* Created user nickname editors.

### Phase 6: Code Quality Audit
* Completed dependency cleaning and established `AppClock` to maintain time synchronization across all use cases.

### Phase 6.5: Release Candidate (Current State)
* Fully functional KMP + Compose layout with robust SQLDelight persistence, extensive JVM/KMP tests, and high architecture compliance.

---

## Current Phase: Phase 6.5 (Release Candidate Verification)
The app has completed its core offline logic. The next immediate step is to transition the mock layers to robust, persistent systems and verify integration on real hardware.

---

## The Next Recommended Phase: Phase 7 (Local Persistence & Platform Integration)

To prepare for production release, the app must transition its mock companion systems into a locally persistent architecture and implement platform-native integration.

### Core Objectives:
1. **Local Companion Persistence**: Create SQLDelight table structures for Companions and CompanionProfiles to store matched companion data locally.
2. **Local Notifications**: Implement native alarm-level focus triggers and completion notifications using Platform-Specific Expect/Actual APIs (Android's `NotificationManager` and iOS's `UNUserNotificationCenter`).
3. **Production Analytics integration**: Replace the terminal-based `DebugAnalyticsTracker` with standard multiplatform integrations.

---

## Future Phases (Long-Term Backlog)

### Phase 8: Cloud Synchronization & True Network Matchmaking
* Deploy a server-side Ktor backend to enable real-time companion pairing.
* Implement database sync capabilities using WebSockets for live status updates in the joint-focus panel.

### Phase 9: Advanced Gamification & Widgets
* Add homescreen widgets on iOS (WidgetKit) and Android to display streaks and daily tasks.
* Integrate rich media feedback for level-ups and achievement unlocking.
