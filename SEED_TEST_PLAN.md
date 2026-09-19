# Seed Test Plan V1: Growth (SelfManagement)

This plan outlines the strategy for the first real-user testing phase (Seed Test V1) of the **Growth** application. The goal is to gather qualitative feedback and initial quantitative metrics from a small group of users to validate the core value proposition.

## 1. Test Objectives
- Validate the core **Goal -> DailyTask -> Focus** habit loop.
- Assess the psychological impact of the **Gamification (XP/Level/Streak/Achievement)** system.
- Observe natural user behavior regarding the **Companion (Mock)** feature.
- Identify critical UX friction points and bugs in a real-world environment.
- Verify the technical stability of the KMP + SQLDelight architecture under daily usage.

## 2. Target User Personas
- **The Self-Improver**: Students or professionals (5-10 people) who actively set learning goals (English, Coding, Reading) but struggle with consistency.
- **The Tech-Savvy Early Adopter**: Users familiar with productivity apps who can provide constructive feedback on UX/UI.

## 3. Test Period
- **Duration**: 7 to 14 consecutive days.
- **Phase**: Seed V1.

## 4. Test Workflow
1. **Recruitment**: Onboard 5-10 users via direct invitation.
2. **Distribution**: Provide Android APK (0.1.0-seed) or iOS build instructions.
3. **Onboarding**: Users set their first goal and start their daily loop.
4. **Observation**: Daily check-ins (informal) to ensure the app is functioning.
5. **Interview**: Conduct one-on-one interviews on Day 7 and Day 14.
6. **Closing**: Collect final feedback and decide on Phase 7/8 priorities.

## 5. Core Metrics (KPIs)
| Metric | Description |
| :--- | :--- |
| **goal_created** | Successful completion of the onboarding flow. |
| **first_focus_rate** | % of users who complete their first focus session within 24h. |
| **retention_d2/d3/d7** | % of users returning on Day 2, Day 3, and Day 7. |
| **focus_frequency** | Average number of focus sessions completed per week per user. |
| **avg_focus_duration** | Average time spent in an active focus session. |
| **focus_abandoned** | % of sessions manually cancelled vs. completed. |
| **companion_engagement** | Number of users viewing (`viewed`) and matching (`matched`) with a mock companion. |
| **together_focus_rate** | % of focus sessions started as "Together Focus". |

## 6. User Interview Questions
(Detailed in [SEED_USER_INTERVIEW.md](file:///D:/AndroidProject/SelfManagement/SEED_USER_INTERVIEW.md))

## 7. Data Collection Method
- **Quantitative**: Users provide screenshots of their "Growth" and "Profile" pages at the end of the test.
- **Qualitative**: Direct feedback through the feedback template and recorded interviews.
- **Technical**: Debug logs (if errors occur).

## 8. Success Criteria
- **Retention**: > 60% of users reach Day 3.
- **Engagement**: > 80% of users complete at least 3 focus sessions in the first week.
- **Gamification**: Users express positive sentiment toward streaks and level-ups during interviews.
- **Stability**: Zero critical database corruption or crash-loop bugs.

## 9. Failure Criteria
- **Retention**: < 30% of users return after the first day.
- **Technical**: Frequent data loss or timer accuracy issues on specific devices.
- **Value Prop**: Users report that the daily task generation feels irrelevant or annoying.

## 10. Next Phase Decision Rules
- **If retention is high but Companion usage is low**: Prioritize Phase 7 (Local Notifications) and ignore Phase 8 (Network Matchmaking) for now.
- **If users complain about "Quiet Focus"**: Accelerate Phase 7 (Native Notifications).
- **If users find Mock Companions motivating**: Prioritize Phase 8 (Ktor/Backend) to make them real.
