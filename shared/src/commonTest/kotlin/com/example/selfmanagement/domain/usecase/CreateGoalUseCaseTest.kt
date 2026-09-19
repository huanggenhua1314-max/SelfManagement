package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.data.repository.InMemoryGoalRepository
import com.example.selfmanagement.domain.model.GoalCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateGoalUseCaseTest {
    private val repository = InMemoryGoalRepository()
    private val fakeClock = object : AppClock {
        override fun now(): Instant = Instant.fromEpochMilliseconds(1000)
        override fun nowMillis(): Long = 1000
        override fun nowEpochSeconds(): Long = 1
        override fun today(): String = "2026-09-14"
    }
    private val useCase = CreateGoalUseCase(repository, fakeClock)

    @Test
    fun `title is blank should fail`() = runTest {
        val result = useCase(
            userId = "user1",
            category = GoalCategory.ENGLISH,
            title = " ",
            targetDays = 30,
            dailyMinutes = 30,
            preferredHour = 8
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `targetDays invalid should fail`() = runTest {
        val result = useCase(
            userId = "user1",
            category = GoalCategory.ENGLISH,
            title = "Test Goal",
            targetDays = 0,
            dailyMinutes = 30,
            preferredHour = 8
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `dailyMinutes invalid should fail`() = runTest {
        val result = useCase(
            userId = "user1",
            category = GoalCategory.ENGLISH,
            title = "Test Goal",
            targetDays = 30,
            dailyMinutes = -5,
            preferredHour = 8
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `valid goal should succeed and save to repository`() = runTest {
        val result = useCase(
            userId = "user1",
            category = GoalCategory.ENGLISH,
            title = "English Mastery",
            targetDays = 90,
            dailyMinutes = 60,
            preferredHour = 20
        )
        
        assertTrue(result.isSuccess)
        val activeGoal = repository.observeActiveGoal("user1").first()
        assertTrue(activeGoal != null)
        assertEquals("English Mastery", activeGoal.title)
        assertEquals(90, activeGoal.targetDays)
        assertEquals(60, activeGoal.dailyMinutes)
        assertEquals(20, activeGoal.preferredHour)
        assertEquals(1000, activeGoal.createdAt)
    }
}
