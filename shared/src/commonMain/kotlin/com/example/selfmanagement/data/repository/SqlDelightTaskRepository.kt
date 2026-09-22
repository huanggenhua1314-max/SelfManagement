package com.example.selfmanagement.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.selfmanagement.db.AppDatabase
import com.example.selfmanagement.db.DailyTaskEntity
import com.example.selfmanagement.domain.model.DailyTask
import com.example.selfmanagement.domain.model.TaskStatus
import com.example.selfmanagement.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightTaskRepository(
    database: AppDatabase
) : TaskRepository {

    private val queries = database.appDatabaseQueries

    override fun observeTodayTasks(goalId: String, date: String): Flow<List<DailyTask>> {
        return queries.getTasksForDate(goalId, date)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun insertTasks(tasks: List<DailyTask>) = withContext(Dispatchers.IO) {
        queries.transaction {
            tasks.forEach { task ->
                queries.insertTask(task.toEntity())
            }
        }
    }

    override suspend fun updateTask(task: DailyTask) {
        withContext(Dispatchers.IO) {
            queries.updateTaskStatus(
                status = task.status.name,
                id = task.id
            )
        }
    }

    override suspend fun getTodayTasks(goalId: String, date: String): List<DailyTask> = withContext(Dispatchers.IO) {
        queries.getTasksForDate(goalId, date).executeAsList().map { it.toDomain() }
    }

    override suspend fun getTaskById(taskId: String): DailyTask? = withContext(Dispatchers.IO) {
        queries.getTaskById(taskId).executeAsOneOrNull()?.toDomain()
    }

    private fun DailyTaskEntity.toDomain(): DailyTask {
        return DailyTask(
            id = id,
            userId = userId,
            goalId = goalId,
            date = date,
            title = title,
            durationMinutes = durationMinutes.toInt(),
            status = TaskStatus.valueOf(status),
            order = sortOrder.toInt()
        )
    }

    private fun DailyTask.toEntity(): DailyTaskEntity {
        return DailyTaskEntity(
            id = id,
            userId = userId,
            goalId = goalId,
            date = date,
            title = title,
            durationMinutes = durationMinutes.toLong(),
            status = status.name,
            sortOrder = order.toLong()
        )
    }
}
