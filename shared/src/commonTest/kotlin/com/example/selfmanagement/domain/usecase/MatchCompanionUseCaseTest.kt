package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.domain.model.CompanionProfile
import com.example.selfmanagement.domain.model.Goal
import com.example.selfmanagement.domain.model.GoalCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MatchCompanionUseCaseTest {
    private val useCase = MatchCompanionUseCase()

    private val myGoal = Goal(
        id = "g1", userId = "u1",
        category = GoalCategory.ENGLISH,
        title = "Learn English",
        targetDays = 90, dailyMinutes = 30,
        preferredHour = 20, createdAt = 0, isActive = true
    )

    @Test
    fun `perfect match should get 100 points`() {
        val perfect = CompanionProfile("p1", "Perfect", GoalCategory.ENGLISH, 30, 20)
        val results = useCase(myGoal, listOf(perfect))
        
        assertEquals(1, results.size)
        assertEquals(100, results[0].score)
        assertTrue(results[0].reasons.contains("目标一致"))
    }

    @Test
    fun `only goal match should get 50 points`() {
        val onlyGoal = CompanionProfile("p2", "GoalOnly", GoalCategory.ENGLISH, 60, 8)
        val results = useCase(myGoal, listOf(onlyGoal))
        
        assertEquals(50, results[0].score)
    }

    @Test
    fun `results should be sorted by score`() {
        val candidates = listOf(
            CompanionProfile("p1", "Mid", GoalCategory.ENGLISH, 60, 8), // 50
            CompanionProfile("p2", "High", GoalCategory.ENGLISH, 30, 20), // 100
            CompanionProfile("p3", "Low", GoalCategory.PROGRAMMING, 10, 8) // 0
        )
        val results = useCase(myGoal, candidates)
        
        assertEquals("p2", results[0].profile.id)
        assertEquals("p1", results[1].profile.id)
        assertEquals("p3", results[2].profile.id)
    }
}
