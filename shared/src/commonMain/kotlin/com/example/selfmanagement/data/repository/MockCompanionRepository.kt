package com.example.selfmanagement.data.repository

import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.*
import com.example.selfmanagement.domain.repository.CompanionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlin.random.Random

class MockCompanionRepository(private val clock: AppClock) : CompanionRepository {
    private val candidates = listOf(
        CompanionProfile("c1", "小王", GoalCategory.ENGLISH, 30, 20),
        CompanionProfile("c2", "阿明", GoalCategory.PROGRAMMING, 60, 14),
        CompanionProfile("c3", "玲玲", GoalCategory.READING, 20, 8),
        CompanionProfile("c4", "大伟", GoalCategory.EXAM, 60, 20),
        CompanionProfile("c5", "苏苏", GoalCategory.ENGLISH, 45, 21)
    )

    private val currentCompanion = MutableStateFlow<Companion?>(null)
    private val profiles = MutableStateFlow(candidates.associateBy { it.id })

    override fun findCandidates(goal: Goal): Flow<List<CompanionProfile>> {
        // 直接返回全量，由 UseCase 排序
        return MutableStateFlow(candidates)
    }

    override suspend fun becomeCompanion(userId: String, companionUserId: String, goalId: String): Companion {
        val companion = Companion(
            id = Random.nextLong().toString(),
            userId = userId,
            companionUserId = companionUserId,
            goalId = goalId,
            createdAt = clock.nowMillis(),
            status = CompanionStatus.ACTIVE
        )
        currentCompanion.value = companion
        return companion
    }

    override fun observeCurrentCompanion(userId: String): Flow<Companion?> = currentCompanion

    override fun observeCompanionProfile(companionUserId: String): Flow<CompanionProfile?> {
        return profiles.map { it[companionUserId] }
    }

    override suspend fun simulateCompanionStatusChange(companionUserId: String, status: CompanionActivityStatus) {
        val currentMap = profiles.value
        val profile = currentMap[companionUserId] ?: return
        profiles.value = currentMap + (companionUserId to profile.copy(status = status))
    }
}
