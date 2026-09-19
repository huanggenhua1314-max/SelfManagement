package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.StudySessionStatus
import com.example.selfmanagement.domain.repository.StudySessionRepository

class ResumeFocusUseCase(
    private val repository: StudySessionRepository,
    private val clock: AppClock
) {
    suspend operator fun invoke(taskId: String) {
        val session = repository.getActiveSession(taskId) ?: return
        if (session.status != StudySessionStatus.PAUSED) return
        val pauseStart = session.lastPauseTimestamp ?: return

        val currentPauseDuration = clock.nowMillis() - pauseStart
        val updated = session.copy(
            status = StudySessionStatus.RUNNING,
            pausedDurationMillis = session.pausedDurationMillis + currentPauseDuration,
            lastPauseTimestamp = null
        )
        repository.updateSession(updated)
    }
}
