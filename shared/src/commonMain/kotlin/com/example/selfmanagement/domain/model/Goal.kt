package com.example.selfmanagement.domain.model

data class Goal(
    val id: String,
    val userId: String,
    val category: GoalCategory,
    val title: String,
    val targetDays: Int,
    val dailyMinutes: Int,
    val preferredHour: Int?,
    val createdAt: Long,
    val isActive: Boolean
)
