package com.example.selfmanagement.data.repository

import com.example.selfmanagement.domain.model.DailyTask
import com.example.selfmanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class InMemoryTaskRepository : TaskRepository {
    private val tasksFlow = MutableStateFlow<List<DailyTask>>(emptyList())

    override fun observeTodayTasks(goalId: String, date: String): Flow<List<DailyTask>> {
        return tasksFlow.map { list ->
            list.filter { it.goalId == goalId && it.date == date }
                .sortedBy { it.order }
        }
    }

    override suspend fun insertTasks(tasks: List<DailyTask>) {
        tasksFlow.value = tasksFlow.value + tasks
    }

    override suspend fun updateTask(task: DailyTask) {
        tasksFlow.value = tasksFlow.value.map {
            if (it.id == task.id) task else it
        }
    }

    override suspend fun getTodayTasks(goalId: String, date: String): List<DailyTask> {
        return tasksFlow.value.filter { it.goalId == goalId && it.date == date }
            .sortedBy { it.order }
    }

    override suspend fun getTaskById(taskId: String): DailyTask? {
        return tasksFlow.value.find { it.id == taskId }
    }
}
