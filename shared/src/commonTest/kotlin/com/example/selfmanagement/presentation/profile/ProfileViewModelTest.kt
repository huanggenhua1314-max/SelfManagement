package com.example.selfmanagement.presentation.profile

import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.data.repository.*
import com.example.selfmanagement.db.AppDatabase
import com.example.selfmanagement.db.createTestDriver
import com.example.selfmanagement.domain.model.*
import com.example.selfmanagement.domain.usecase.CalculateLevelUseCase
import com.example.selfmanagement.domain.usecase.CalculateStreakUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.yield
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var userRepo: SqlDelightUserRepository
    private lateinit var goalRepo: SqlDelightGoalRepository
    private lateinit var growthRepo: SqlDelightGrowthRepository
    private lateinit var companionRepo: MockCompanionRepository
    private lateinit var settingsRepo: SqlDelightSettingsRepository
    
    private val clock = object : AppClock {
        override fun now(): Instant = Instant.fromEpochMilliseconds(0)
        override fun nowMillis(): Long = 0
        override fun nowEpochSeconds(): Long = 0
        override fun today(): String = "2026-09-16"
    }

    @BeforeTest
    fun setup() {
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        val driver = createTestDriver()
        val database = AppDatabase(driver)
        userRepo = SqlDelightUserRepository(database, testDispatcher)
        goalRepo = SqlDelightGoalRepository(database) // Ideally all should take dispatcher
        growthRepo = SqlDelightGrowthRepository(database)
        companionRepo = MockCompanionRepository(clock)
        settingsRepo = SqlDelightSettingsRepository(database)
        
        viewModel = ProfileViewModel(
            "local-user",
            userRepo,
            goalRepo,
            growthRepo,
            companionRepo,
            settingsRepo,
            CalculateLevelUseCase(),
            CalculateStreakUseCase(),
            clock
        )
    }

    @Test
    fun `should load default nickname and update it`() = runTest {
        advanceUntilIdle()
        assertEquals("用户", viewModel.uiState.value.nickname)
        
        viewModel.updateNickname("新昵称")
        
        // Use a loop to wait for the state update, as advanceUntilIdle might not catch IO dispatcher work
        var updated = false
        for (i in 1..10) {
            if (viewModel.uiState.value.nickname == "新昵称") {
                updated = true
                break
            }
            yield()
            advanceUntilIdle()
        }
        assertTrue(updated, "Nickname should be updated to '新昵称'")
    }

    @Test
    fun `should load active goal`() = runTest {
        val goal = Goal("g1", "local-user", GoalCategory.ENGLISH, "Test", 90, 30, 20, 0, true)
        goalRepo.createGoal(goal)
        advanceUntilIdle()
        
        assertEquals("Test", viewModel.uiState.value.activeGoal?.title)
    }

    @Test
    fun `should calculate growth metrics`() = runTest {
        val record = GrowthRecord("r1", "local-user", "2026-09-16", 60, 60, "s1")
        growthRepo.saveRecord(record)
        advanceUntilIdle()
        
        assertEquals(60, viewModel.uiState.value.totalXp)
        assertEquals(60, viewModel.uiState.value.totalStudyMinutes)
        assertEquals(1, viewModel.uiState.value.currentStreak)
    }
}
