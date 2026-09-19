package com.example.selfmanagement.domain.repository

import com.example.selfmanagement.domain.model.DailyTask
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTodayTasks(goalId: String, date: String): Flow<List<DailyTask>>
    suspend fun insertTasks(tasks: List<DailyTask>)
    suspend fun updateTask(task: DailyTask)
    suspend fun getTodayTasks(goalId: String, date: String): List<DailyTask>
    suspend fun getTaskById(taskId: String): DailyTask?
}
