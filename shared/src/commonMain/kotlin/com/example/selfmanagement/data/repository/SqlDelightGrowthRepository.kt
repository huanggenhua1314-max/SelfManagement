package com.example.selfmanagement.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.selfmanagement.db.AchievementEntity
import com.example.selfmanagement.db.AppDatabase
import com.example.selfmanagement.db.GrowthRecordEntity
import com.example.selfmanagement.domain.model.Achievement
import com.example.selfmanagement.domain.model.GrowthRecord
import com.example.selfmanagement.domain.repository.GrowthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightGrowthRepository(
    database: AppDatabase
) : GrowthRepository {

    private val queries = database.appDatabaseQueries

    override fun observeAllRecords(userId: String): Flow<List<GrowthRecord>> {
        return queries.getRecordsForUser(userId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun saveRecord(record: GrowthRecord) = withContext(Dispatchers.IO) {
        queries.insertRecord(record.toEntity())
    }

    override suspend fun isSourceSettled(sourceId: String): Boolean = withContext(Dispatchers.IO) {
        queries.isSourceSettled(sourceId).executeAsOne()
    }

    override fun observeAchievements(): Flow<List<Achievement>> {
        return queries.getAchievements()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun saveAchievement(achievement: Achievement) = withContext(Dispatchers.IO) {
        queries.insertAchievement(achievement.toEntity())
    }

    private fun GrowthRecordEntity.toDomain(): GrowthRecord {
        return GrowthRecord(
            id = id,
            userId = userId,
            date = date,
            studyMinutes = studyMinutes.toInt(),
            xp = xp.toInt(),
            sourceId = sourceId
        )
    }

    private fun GrowthRecord.toEntity(): GrowthRecordEntity {
        return GrowthRecordEntity(
            id = id,
            userId = userId,
            date = date,
            studyMinutes = studyMinutes.toLong(),
            xp = xp.toLong(),
            sourceId = sourceId
        )
    }

    private fun AchievementEntity.toDomain(): Achievement {
        return Achievement(
            id = id,
            title = title,
            description = description,
            unlockedAt = unlockedAt
        )
    }

    private fun Achievement.toEntity(): AchievementEntity {
        return AchievementEntity(
            id = id,
            title = title,
            description = description,
            unlockedAt = unlockedAt
        )
    }
}
