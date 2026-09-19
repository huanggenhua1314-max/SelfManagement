package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.domain.model.Goal
import com.example.selfmanagement.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow

class GetActiveGoalUseCase(private val repository: GoalRepository) {
    operator fun invoke(userId: String): Flow<Goal?> {
        return repository.observeActiveGoal(userId)
    }
}
