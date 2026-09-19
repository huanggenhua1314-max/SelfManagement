package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.domain.model.StudySession
import com.example.selfmanagement.domain.model.StudySessionStatus

class CalculateFocusTimeUseCase {
    operator fun invoke(session: StudySession?, nowMillis: Long): Long {
        if (session == null) return 0L
        
        return if (session.status == StudySessionStatus.RUNNING) {
            (nowMillis - session.startTimeMillis) - session.pausedDurationMillis
        } else {
            val lastActiveTime = session.lastPauseTimestamp ?: nowMillis
            (lastActiveTime - session.startTimeMillis) - session.pausedDurationMillis
        }
    }
}
