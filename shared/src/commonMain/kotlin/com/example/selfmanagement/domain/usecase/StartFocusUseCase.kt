package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.StudySession
import com.example.selfmanagement.domain.model.StudySessionStatus
import com.example.selfmanagement.domain.repository.StudySessionRepository
import kotlin.random.Random

class StartFocusUseCase(
    private val repository: StudySessionRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(taskId: String) {
        val existing = repository.getActiveSession(taskId)
        if (existing != null && existing.status != StudySessionStatus.COMPLETED) return

        val session = StudySession(
            id = Random.nextLong().toString(),
            taskId = taskId,
            startTimeMillis = clock.nowMillis(),
            status = StudySessionStatus.RUNNING
        )
        repository.startSession(session)
    }
}
