package com.example.selfmanagement.presentation.profile

import com.example.selfmanagement.domain.model.*

data class ProfileUiState(
    val nickname: String = "用户",
    val avatarUrl: String? = null,
    val level: Int = 1,
    val totalXp: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalStudyMinutes: Int = 0,
    val activeGoal: Goal? = null,
    val companionProfile: CompanionProfile? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
