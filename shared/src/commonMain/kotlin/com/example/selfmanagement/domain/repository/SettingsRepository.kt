package com.example.selfmanagement.domain.repository

import com.example.selfmanagement.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeThemeMode(userId: String): Flow<ThemeMode>
    fun observeNotificationsEnabled(userId: String): Flow<Boolean>
    suspend fun updateThemeMode(userId: String, mode: ThemeMode)
    suspend fun updateNotificationsEnabled(userId: String, enabled: Boolean)
}
