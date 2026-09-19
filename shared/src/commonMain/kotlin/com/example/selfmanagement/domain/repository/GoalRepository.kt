package com.example.selfmanagement.domain.repository

import com.example.selfmanagement.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun observeActiveGoal(userId: String): Flow<Goal?>
    suspend fun createGoal(goal: Goal)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goalId: String)
}
