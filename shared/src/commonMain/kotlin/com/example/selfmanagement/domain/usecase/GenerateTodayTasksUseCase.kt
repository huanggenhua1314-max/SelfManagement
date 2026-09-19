package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.domain.model.DailyTask
import com.example.selfmanagement.domain.model.Goal
import com.example.selfmanagement.domain.model.GoalCategory
import com.example.selfmanagement.domain.model.TaskStatus
import com.example.selfmanagement.domain.repository.TaskRepository
import kotlin.random.Random

class GenerateTodayTasksUseCase(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(goal: Goal, date: String): List<DailyTask> {
        // 1. 检查是否已存在
        val existingTasks = taskRepository.getTodayTasks(goal.id, date)
        if (existingTasks.isNotEmpty()) return existingTasks

        // 2. 根据分类生成基础模板
        val templates = when (goal.category) {
            GoalCategory.ENGLISH -> listOf(
                "词汇学习" to 10,
                "听力训练" to 10,
                "口语练习" to 10
            )
            GoalCategory.PROGRAMMING -> listOf(
                "技术学习" to 10,
                "编码练习" to 15,
                "学习总结" to 5
            )
            GoalCategory.READING -> listOf(
                "阅读" to 25,
                "阅读笔记" to 5
            )
            GoalCategory.EXAM -> listOf(
                "知识点学习" to 10,
                "练习题" to 15,
                "错题总结" to 5
            )
            GoalCategory.CUSTOM -> listOf(
                "今日成长" to goal.dailyMinutes
            )
        }

        // 3. 时长归一化 (Duration Normalization)
        val totalTemplateMinutes = templates.sumOf { it.second }
        val factor = goal.dailyMinutes.toDouble() / totalTemplateMinutes
        
        var currentSum = 0
        val tasks = templates.mapIndexed { index, (title, baseMinutes) ->
            val duration = if (index == templates.lastIndex) {
                goal.dailyMinutes - currentSum
            } else {
                (baseMinutes * factor).toInt().coerceAtLeast(1)
            }
            currentSum += duration
            
            DailyTask(
                id = Random.nextLong().toString(),
                userId = goal.userId,
                goalId = goal.id,
                date = date,
                title = title,
                durationMinutes = duration,
                status = TaskStatus.TODO,
                order = index
            )
        }

        // 4. 保存并返回
        taskRepository.insertTasks(tasks)
        return tasks
    }
}
