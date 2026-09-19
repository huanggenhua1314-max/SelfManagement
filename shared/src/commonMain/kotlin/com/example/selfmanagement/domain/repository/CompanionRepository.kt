package com.example.selfmanagement.domain.repository

import com.example.selfmanagement.domain.model.Companion
import com.example.selfmanagement.domain.model.CompanionProfile
import com.example.selfmanagement.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface CompanionRepository {
    fun findCandidates(goal: Goal): Flow<List<CompanionProfile>>
    suspend fun becomeCompanion(userId: String, companionUserId: String, goalId: String): Companion
    fun observeCurrentCompanion(userId: String): Flow<Companion?>
    fun observeCompanionProfile(companionUserId: String): Flow<CompanionProfile?>
    
    // Mock 控制方法
    suspend fun simulateCompanionStatusChange(companionUserId: String, status: com.example.selfmanagement.domain.model.CompanionActivityStatus)
}
