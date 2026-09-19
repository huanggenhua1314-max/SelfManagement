package com.example.selfmanagement.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.selfmanagement.db.AppDatabase
import com.example.selfmanagement.db.SettingsEntity
import com.example.selfmanagement.domain.model.ThemeMode
import com.example.selfmanagement.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightSettingsRepository(
    database: AppDatabase
) : SettingsRepository {
    private val queries = database.appDatabaseQueries

    override fun observeThemeMode(userId: String): Flow<ThemeMode> {
        return queries.getSettings(userId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.themeMode?.let { mode -> ThemeMode.valueOf(mode) } ?: ThemeMode.SYSTEM }
    }

    override fun observeNotificationsEnabled(userId: String): Flow<Boolean> {
        return queries.getSettings(userId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.notificationsEnabled == 1L }
    }

    override suspend fun updateThemeMode(userId: String, mode: ThemeMode) = withContext(Dispatchers.IO) {
        ensureSettingsExists(userId)
        queries.updateThemeMode(mode.name, userId)
    }

    override suspend fun updateNotificationsEnabled(userId: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        ensureSettingsExists(userId)
        queries.updateNotificationsEnabled(if (enabled) 1L else 0L, userId)
    }

    private suspend fun ensureSettingsExists(userId: String) {
        if (queries.getSettings(userId).executeAsOneOrNull() == null) {
            queries.insertSettings(SettingsEntity(userId, ThemeMode.SYSTEM.name, 0L))
        }
    }
}
