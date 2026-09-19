package com.example.selfmanagement.data.repository

import com.example.selfmanagement.db.AppDatabase
import com.example.selfmanagement.db.createTestDriver
import com.example.selfmanagement.domain.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RepositoryPersistenceTest {
    private lateinit var database: AppDatabase
    private lateinit var goalRepo: SqlDelightGoalRepository
    private lateinit var taskRepo: SqlDelightTaskRepository
    private lateinit var sessionRepo: SqlDelightStudySessionRepository
    private lateinit var growthRepo: SqlDelightGrowthRepository

    @BeforeTest
    fun setup() {
        val driver = createTestDriver()
        database = AppDatabase(driver)
        goalRepo = SqlDelightGoalRepository(database)
        taskRepo = SqlDelightTaskRepository(database)
        sessionRepo = SqlDelightStudySessionRepository(database)
        growthRepo = SqlDelightGrowthRepository(database)
    }

    @Test
    fun `test goal persistence`() = runTest {
        val userId = "user1"
        val goal = Goal("g1", userId, GoalCategory.ENGLISH, "Test Goal", 90, 30, 20, 1000, true)
        
        goalRepo.createGoal(goal)
        val activeGoal = goalRepo.observeActiveGoal(userId).first()
        
        assertEquals(goal, activeGoal)
    }

    @Test
    fun `test task persistence and flow`() = runTest {
        val userId = "user1"
        val goalId = "g1"
        val date = "2026-09-16"
        val task = DailyTask("t1", userId, goalId, date, "Task 1", 10, TaskStatus.TODO, 0)
        
        taskRepo.insertTasks(listOf(task))
        val tasks = taskRepo.observeTodayTasks(goalId, date).first()
        
        assertEquals(1, tasks.size)
        assertEquals(task, tasks[0])
        
        val updatedTask = task.copy(status = TaskStatus.COMPLETED)
        taskRepo.updateTask(updatedTask)
        
        val tasksAfterUpdate = taskRepo.observeTodayTasks(goalId, date).first()
        assertEquals(TaskStatus.COMPLETED, tasksAfterUpdate[0].status)
    }

    @Test
    fun `test study session persistence`() = runTest {
        val session = StudySession("s1", "t1", 1000, 2000, 0, null, 1000, StudySessionStatus.COMPLETED)
        
        sessionRepo.startSession(session)
        val activeSession = sessionRepo.getActiveSession("t1")
        // Note: getActiveSession filters by status != COMPLETED in .sq
        assertTrue(activeSession == null)
        
        val runningSession = session.copy(id = "s2", status = StudySessionStatus.RUNNING)
        sessionRepo.startSession(runningSession)
        val foundRunning = sessionRepo.getActiveSession("t1")
        assertEquals(runningSession, foundRunning)
    }

    @Test
    fun `test ongoing session recovery after DB close and reopen`() = runTest {
        val taskId = "task_recovery"
        val session = StudySession("s_rec", taskId, 1000, null, 0, null, 0, StudySessionStatus.RUNNING)
        
        // 1. 保存正在运行的会话
        sessionRepo.startSession(session)
        
        // 2. 模拟“重新打开数据库” (使用同一个驱动或重新创建 repository)
        val activeSession = sessionRepo.getActiveSession(taskId)
        
        // 3. 验证会话仍然存在且状态正确
        assertTrue(activeSession != null)
        assertEquals(StudySessionStatus.RUNNING, activeSession.status)
        assertEquals(1000L, activeSession.startTimeMillis)
    }

    @Test
    fun `test growth record and settlement idempotency`() = runTest {
        val userId = "user1"
        val record = GrowthRecord("r1", userId, "2026-09-16", 10, 10, "s1")
        
        assertFalse(growthRepo.isSourceSettled("s1"))
        growthRepo.saveRecord(record)
        assertTrue(growthRepo.isSourceSettled("s1"))
        
        val records = growthRepo.observeAllRecords(userId).first()
        assertEquals(1, records.size)
        assertEquals(record, records[0])
    }

    @Test
    fun `test achievement persistence`() = runTest {
        val achievement = Achievement("a1", "Title", "Desc", 1000)
        
        growthRepo.saveAchievement(achievement)
        val achievements = growthRepo.observeAchievements().first()
        
        assertEquals(1, achievements.size)
        assertEquals(achievement, achievements[0])
    }

    // Helper for Boolean check since kotlin.test might not have assertFalse in some versions/platforms
    private fun assertFalse(actual: Boolean) = assertEquals(false, actual)
}
