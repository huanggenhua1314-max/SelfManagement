package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.domain.model.StudySession
import com.example.selfmanagement.domain.model.StudySessionStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateFocusTimeUseCaseTest {
    private val useCase = CalculateFocusTimeUseCase()

    @Test
    fun `running session should calculate elapsed based on now`() {
        val session = StudySession(
            id = "1", taskId = "t1",
            startTimeMillis = 1000,
            status = StudySessionStatus.RUNNING
        )
        // Now is 5000. Elapsed = 5000 - 1000 = 4000
        assertEquals(4000, useCase(session, 5000))
    }

    @Test
    fun `paused session should freeze elapsed time`() {
        val session = StudySession(
            id = "1", taskId = "t1",
            startTimeMillis = 1000,
            lastPauseTimestamp = 3000,
            status = StudySessionStatus.PAUSED
        )
        // Now is 10000. Elapsed should still be 3000 - 1000 = 2000
        assertEquals(2000, useCase(session, 10000))
    }

    @Test
    fun `resumed session should exclude paused duration`() {
        // Timeline:
        // 1000: Start
        // 3000: Pause (Duration so far: 2000)
        // 8000: Resume (Paused for: 5000)
        // 10000: Now
        val session = StudySession(
            id = "1", taskId = "t1",
            startTimeMillis = 1000,
            pausedDurationMillis = 5000, // Pre-calculated during resume
            status = StudySessionStatus.RUNNING
        )
        // Elapsed = (10000 - 1000) - 5000 = 4000
        assertEquals(4000, useCase(session, 10000))
    }
}
