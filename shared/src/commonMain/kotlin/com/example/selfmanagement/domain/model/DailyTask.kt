package com.example.selfmanagement.domain.model

data class DailyTask(
    val id: String,
    val userId: String,
    val goalId: String,
    val date: String, // yyyy-MM-dd
    val title: String,
    val durationMinutes: Int,
    val status: TaskStatus,
    val order: Int
)
