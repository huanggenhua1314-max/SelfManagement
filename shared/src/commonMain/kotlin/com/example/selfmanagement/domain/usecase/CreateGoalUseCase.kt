package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.Goal
import com.example.selfmanagement.domain.model.GoalCategory
import com.example.selfmanagement.domain.repository.GoalRepository
import kotlin.random.Random

class CreateGoalUseCase(
    private val repository: GoalRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(
        userId: String,
        category: GoalCategory,
        title: String,
        targetDays: Int,
        dailyMinutes: Int,
        preferredHour: Int?
    ): Result<Unit> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("标题不能为空"))
        }
        if (targetDays <= 0) {
            return Result.failure(IllegalArgumentException("目标天数必须大于0"))
        }
        if (dailyMinutes <= 0) {
            return Result.failure(IllegalArgumentException("每日时长必须大于0"))
        }

        val goal = Goal(
            id = Random.nextLong().toString(),
            userId = userId,
            category = category,
            title = title,
            targetDays = targetDays,
            dailyMinutes = dailyMinutes,
            preferredHour = preferredHour,
            createdAt = clock.nowMillis(),
            isActive = true
        )
        
        repository.createGoal(goal)
        return Result.success(Unit)
    }
}
