package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.StudySessionStatus
import com.example.selfmanagement.domain.model.TaskStatus
import com.example.selfmanagement.domain.repository.StudySessionRepository
import com.example.selfmanagement.domain.repository.TaskRepository

class CompleteFocusUseCase(
    private val sessionRepository: StudySessionRepository,
    private val taskRepository: TaskRepository,
    private val calculateFocusTimeUseCase: CalculateFocusTimeUseCase,
    private val recordGrowthUseCase: RecordGrowthUseCase,
    private val clock: AppClock
) {
    suspend operator fun invoke(taskId: String) {
        val session = sessionRepository.getActiveSession(taskId) ?: return
        if (session.status == StudySessionStatus.COMPLETED) return

        // 1. 计算最终时长 (ms 转 s)
        val elapsedMillis = calculateFocusTimeUseCase(session, clock.nowMillis())
        val finalDurationSeconds = elapsedMillis / 1000

        // 2. 完成 Session
        val updatedSession = session.copy(
            status = StudySessionStatus.COMPLETED,
            endTimeMillis = clock.nowMillis(),
            durationSeconds = finalDurationSeconds
        )
        sessionRepository.updateSession(updatedSession)

        // 3. 完成 Task
        val task = taskRepository.getTaskById(taskId) ?: return
        val updatedTask = task.copy(status = TaskStatus.COMPLETED)
        taskRepository.updateTask(updatedTask)

        // 4. 触发成长结算
        recordGrowthUseCase(task.userId, updatedSession)
    }
}
