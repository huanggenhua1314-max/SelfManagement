package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.StudySessionStatus
import com.example.selfmanagement.domain.repository.StudySessionRepository

class PauseFocusUseCase(
    private val repository: StudySessionRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(taskId: String) {
        val session = repository.getActiveSession(taskId) ?: return
        if (session.status != StudySessionStatus.RUNNING) return

        val updated = session.copy(
            status = StudySessionStatus.PAUSED,
            lastPauseTimestamp = clock.nowMillis()
        )
        repository.updateSession(updated)
    }
}
