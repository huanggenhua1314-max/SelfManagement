package com.example.selfmanagement.domain.repository

import com.example.selfmanagement.domain.model.Achievement
import com.example.selfmanagement.domain.model.GrowthRecord
import kotlinx.coroutines.flow.Flow

interface GrowthRepository {
    fun observeAllRecords(userId: String): Flow<List<GrowthRecord>>
    suspend fun saveRecord(record: GrowthRecord)
    suspend fun isSourceSettled(sourceId: String): Boolean
    
    fun observeAchievements(): Flow<List<Achievement>>
    suspend fun saveAchievement(achievement: Achievement)
}
