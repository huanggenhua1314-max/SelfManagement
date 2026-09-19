package com.example.selfmanagement.domain.model

data class StudySession(
    val id: String,
    val taskId: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long? = null,
    val pausedDurationMillis: Long = 0,
    val lastPauseTimestamp: Long? = null,
    val durationSeconds: Long = 0,
    val status: StudySessionStatus
)
