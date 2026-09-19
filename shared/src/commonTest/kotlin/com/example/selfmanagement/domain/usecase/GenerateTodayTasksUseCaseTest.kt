package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.data.repository.InMemoryTaskRepository
import com.example.selfmanagement.domain.model.Goal
import com.example.selfmanagement.domain.model.GoalCategory
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GenerateTodayTasksUseCaseTest {
    private val repository = InMemoryTaskRepository()
    private val useCase = GenerateTodayTasksUseCase(repository)

    private fun createMockGoal(category: GoalCategory, dailyMinutes: Int) = Goal(
        id = "goal_1",
        userId = "user_1",
        category = category,
        title = "Mock Goal",
        targetDays = 90,
        dailyMinutes = dailyMinutes,
        preferredHour = 20,
        createdAt = 0,
        isActive = true
    )

    @Test
    fun `English 30 minutes should generate 3 tasks with 30 mins total`() = runTest {
        val goal = createMockGoal(GoalCategory.ENGLISH, 30)
        val tasks = useCase(goal, "2026-09-15")
        
        assertEquals(3, tasks.size)
        assertEquals(30, tasks.sumOf { it.durationMinutes })
        assertEquals("词汇学习", tasks[0].title)
    }

    @Test
    fun `Programming 20 minutes should normalize total to 20`() = runTest {
        val goal = createMockGoal(GoalCategory.PROGRAMMING, 20)
        val tasks = useCase(goal, "2026-09-15")
        
        assertEquals(3, tasks.size)
        assertEquals(20, tasks.sumOf { it.durationMinutes })
    }

    @Test
    fun `Custom goal should generate one task with full duration`() = runTest {
        val goal = createMockGoal(GoalCategory.CUSTOM, 45)
        val tasks = useCase(goal, "2026-09-15")
        
        assertEquals(1, tasks.size)
        assertEquals(45, tasks[0].durationMinutes)
        assertEquals("今日成长", tasks[0].title)
    }

    @Test
    fun `should not generate duplicate tasks for same day`() = runTest {
        val goal = createMockGoal(GoalCategory.READING, 30)
        val firstBatch = useCase(goal, "2026-09-15")
        val secondBatch = useCase(goal, "2026-09-15")
        
        assertEquals(firstBatch.map { it.id }, secondBatch.map { it.id })
    }

    @Test
    fun `updating goal should not change already generated tasks for today`() = runTest {
        val goal = createMockGoal(GoalCategory.ENGLISH, 30)
        val date = "2026-09-16"
        
        // 1. 生成 30 分钟的任务
        val initialTasks = useCase(goal, date)
        assertEquals(30, initialTasks.sumOf { it.durationMinutes })

        // 2. 模拟目标修改为 60 分钟 (ID 保持不变)
        val updatedGoal = goal.copy(dailyMinutes = 60)
        
        // 3. 再次请求今日任务
        val tasksAfterUpdate = useCase(updatedGoal, date)
        
        // 4. 验证任务仍然是旧的 30 分钟那一批，没有被重新生成为 60 分钟
        assertEquals(30, tasksAfterUpdate.sumOf { it.durationMinutes })
        assertEquals(initialTasks.map { it.id }, tasksAfterUpdate.map { it.id })
    }
}
