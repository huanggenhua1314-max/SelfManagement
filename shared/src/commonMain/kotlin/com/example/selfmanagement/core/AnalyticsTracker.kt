package com.example.selfmanagement.core

interface AnalyticsTracker {
    fun track(event: String, params: Map<String, String> = emptyMap())
}

object DebugAnalyticsTracker : AnalyticsTracker {
    override fun track(event: String, params: Map<String, String>) {
        println("[Analytics] Event: $event, Params: $params")
    }
}

object AnalyticsEvents {
    const val APP_OPEN = "app_open"
    const val ONBOARDING_START = "onboarding_start"
    const val GOAL_CREATED = "goal_created"
    const val GOAL_UPDATED = "goal_updated"
    const val TASK_STARTED = "task_started"
    const val TASK_COMPLETED = "task_completed"
    const val FOCUS_STARTED = "focus_started"
    const val FOCUS_COMPLETED = "focus_completed"
    const val FOCUS_ABANDONED = "focus_abandoned"
    const val FOCUS_RESUMED = "focus_resumed"
    const val GROWTH_VIEWED = "growth_viewed"
    const val COMPANION_VIEWED = "companion_viewed"
    const val COMPANION_MATCHED = "companion_matched"
    const val TOGETHER_FOCUS_STARTED = "together_focus_started"
    const val TOGETHER_FOCUS_COMPLETED = "together_focus_completed"
    const val PROFILE_VIEWED = "profile_viewed"
}
