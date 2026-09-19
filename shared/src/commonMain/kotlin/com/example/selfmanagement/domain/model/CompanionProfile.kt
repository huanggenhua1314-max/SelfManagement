package com.example.selfmanagement.domain.model

data class CompanionProfile(
    val id: String,
    val nickname: String,
    val goalCategory: GoalCategory,
    val dailyMinutes: Int,
    val preferredHour: Int,
    val status: CompanionActivityStatus = CompanionActivityStatus.IDLE
)
