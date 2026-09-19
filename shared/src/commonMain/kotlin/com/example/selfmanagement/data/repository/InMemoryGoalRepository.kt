package com.example.selfmanagement.data.repository

import com.example.selfmanagement.domain.model.Goal
import com.example.selfmanagement.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class InMemoryGoalRepository : GoalRepository {
    private val goals = MutableStateFlow<Map<String, Goal>>(emptyMap())

    override fun observeActiveGoal(userId: String): Flow<Goal?> {
        return goals.map { map ->
            map.values.find { it.userId == userId && it.isActive }
        }
    }

    override suspend fun createGoal(goal: Goal) {
        goals.value = goals.value + (goal.id to goal)
    }

    override suspend fun updateGoal(goal: Goal) {
        goals.value = goals.value + (goal.id to goal)
    }

    override suspend fun deleteGoal(goalId: String) {
        goals.value = goals.value - goalId
    }
}
