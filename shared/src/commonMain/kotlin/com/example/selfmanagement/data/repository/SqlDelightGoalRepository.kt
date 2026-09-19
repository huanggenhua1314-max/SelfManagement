package com.example.selfmanagement.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.selfmanagement.db.AppDatabase
import com.example.selfmanagement.db.GoalEntity
import com.example.selfmanagement.domain.model.Goal
import com.example.selfmanagement.domain.model.GoalCategory
import com.example.selfmanagement.domain.repository.GoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightGoalRepository(
    database: AppDatabase
) : GoalRepository {

    private val queries = database.appDatabaseQueries

    override fun observeActiveGoal(userId: String): Flow<Goal?> {
        return queries.getActiveGoal(userId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    Goal(
                        id = it.id,
                        userId = it.userId,
                        category = GoalCategory.valueOf(it.category),
                        title = it.title,
                        targetDays = it.targetDays.toInt(),
                        dailyMinutes = it.dailyMinutes.toInt(),
                        preferredHour = it.preferredHour?.toInt(),
                        createdAt = it.createdAt,
                        isActive = it.isActive == 1L
                    )
                }
            }
    }

    override suspend fun createGoal(goal: Goal) = withContext(Dispatchers.IO) {
        queries.insertGoal(
            GoalEntity(
                id = goal.id,
                userId = goal.userId,
                category = goal.category.name,
                title = goal.title,
                targetDays = goal.targetDays.toLong(),
                dailyMinutes = goal.dailyMinutes.toLong(),
                preferredHour = goal.preferredHour?.toLong(),
                createdAt = goal.createdAt,
                isActive = if (goal.isActive) 1L else 0L
            )
        )
    }

    override suspend fun updateGoal(goal: Goal) = withContext(Dispatchers.IO) {
        createGoal(goal)
    }

    override suspend fun deleteGoal(goalId: String) = withContext(Dispatchers.IO) {
        // MVP 中暂未定义删除逻辑
    }
}
