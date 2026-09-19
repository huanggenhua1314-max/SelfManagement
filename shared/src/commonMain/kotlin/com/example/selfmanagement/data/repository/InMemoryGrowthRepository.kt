package com.example.selfmanagement.data.repository

import com.example.selfmanagement.domain.model.Achievement
import com.example.selfmanagement.domain.model.GrowthRecord
import com.example.selfmanagement.domain.repository.GrowthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class InMemoryGrowthRepository : GrowthRepository {
    private val records = MutableStateFlow<List<GrowthRecord>>(emptyList())
    private val achievements = MutableStateFlow<List<Achievement>>(emptyList())

    override fun observeAllRecords(userId: String): Flow<List<GrowthRecord>> {
        return records.map { list -> list.filter { it.userId == userId } }
    }

    override suspend fun saveRecord(record: GrowthRecord) {
        records.value = records.value + record
    }

    override suspend fun isSourceSettled(sourceId: String): Boolean {
        return records.value.any { it.sourceId == sourceId }
    }

    override fun observeAchievements(): Flow<List<Achievement>> {
        return achievements
    }

    override suspend fun saveAchievement(achievement: Achievement) {
        if (achievements.value.none { it.id == achievement.id }) {
            achievements.value = achievements.value + achievement
        }
    }
}
