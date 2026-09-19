package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.domain.model.DailyTask
import com.example.selfmanagement.domain.model.TaskStatus
import com.example.selfmanagement.domain.repository.TaskRepository

class CompleteTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: DailyTask) {
        if (task.status == TaskStatus.COMPLETED) return
        
        val updatedTask = task.copy(status = TaskStatus.COMPLETED)
        repository.updateTask(updatedTask)
    }
}
