package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.data.repository.InMemoryGrowthRepository
import com.example.selfmanagement.domain.model.GrowthRecord
import com.example.selfmanagement.domain.model.StudySession
import com.example.selfmanagement.domain.model.StudySessionStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GrowthCalculationTest {
    private var todayStr = "2026-09-15"
    private val clock = object : AppClock {
        override fun now(): Instant = Instant.fromEpochMilliseconds(0)
        override fun nowMillis(): Long = 0
        override fun nowEpochSeconds(): Long = 0
        override fun today(): String = todayStr
    }

    private val repository = InMemoryGrowthRepository()
    private val streakUseCase = CalculateStreakUseCase()
    private val achievementUseCase = CalculateAchievementUseCase(clock)
    private val recordUseCase = RecordGrowthUseCase(repository, streakUseCase, achievementUseCase, clock)

    @Test
    fun `study session should generate xp and record`() = runTest {
        // StudySession(id, taskId, startTime, endTime, pausedDuration, lastPause, durationSeconds, status)
        val session = StudySession("s1", "t1", 0, 1000, 0, null, 120, StudySessionStatus.COMPLETED) // 120s = 2m
        recordUseCase("u1", session)
        
        val records = repository.observeAllRecords("u1").first()
        assertEquals(1, records.size)
        assertEquals(2, records[0].xp)
        assertEquals(2, records[0].studyMinutes)
    }

    @Test
    fun `duplicate settlement should be ignored`() = runTest {
        val session = StudySession("s1", "t1", 0, 1000, 0, null, 120, StudySessionStatus.COMPLETED)
        recordUseCase("u1", session)
        recordUseCase("u1", session) // Repeat
        
        val records = repository.observeAllRecords("u1").first()
        assertEquals(1, records.size)
    }

    @Test
    fun `streak should calculate correctly`() = runTest {
        val records = listOf(
            GrowthRecord("1", "u1", "2026-09-10", 10, 10, "s1"),
            GrowthRecord("2", "u1", "2026-09-11", 10, 10, "s2"),
            GrowthRecord("3", "u1", "2026-09-12", 10, 10, "s3")
        )
        val result = streakUseCase(records, "2026-09-12")
        assertEquals(3, result.currentStreak)
        assertEquals(3, result.bestStreak)
        
        // Break one day
        val resultAfterBreak = streakUseCase(records, "2026-09-14")
        assertEquals(0, resultAfterBreak.currentStreak)
        assertEquals(3, resultAfterBreak.bestStreak)
    }

    @Test
    fun `achievement should unlock when threshold reached`() = runTest {
        val records = listOf(GrowthRecord("1", "u1", "2026-09-10", 10, 10, "s1"))
        val unlocked = achievementUseCase(records, 1, emptyList())
        assertTrue(unlocked.any { it.id == "first_completion" })
        
        // 10 hours = 600 mins
        val longRecords = listOf(GrowthRecord("1", "u1", "2026-09-10", 600, 600, "s1"))
        val unlockedAdvanced = achievementUseCase(longRecords, 1, emptyList())
        assertTrue(unlockedAdvanced.any { it.id == "total_10_hours" })
    }
}
