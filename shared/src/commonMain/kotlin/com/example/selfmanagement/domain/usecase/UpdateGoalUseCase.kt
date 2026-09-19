package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.domain.model.Goal
import com.example.selfmanagement.domain.model.GoalCategory
import com.example.selfmanagement.domain.repository.GoalRepository

class UpdateGoalUseCase(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(
        currentGoal: Goal,
        title: String,
        targetDays: Int,
        dailyMinutes: Int,
        preferredHour: Int?
    ): Result<Unit> {
        if (title.isBlank()) return Result.failure(IllegalArgumentException("标题不能为空"))
        if (targetDays <= 0) return Result.failure(IllegalArgumentException("目标天数必须大于0"))
        if (dailyMinutes <= 0) return Result.failure(IllegalArgumentException("每日时长必须大于0"))

        val updatedGoal = currentGoal.copy(
            title = title,
            targetDays = targetDays,
            dailyMinutes = dailyMinutes,
            preferredHour = preferredHour
        )
        
        repository.updateGoal(updatedGoal)
        return Result.success(Unit)
    }
}
