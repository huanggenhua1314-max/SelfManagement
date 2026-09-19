package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.Achievement
import com.example.selfmanagement.domain.model.GrowthRecord

class CalculateAchievementUseCase(private val clock: AppClock) {
    operator fun invoke(
        records: List<GrowthRecord>,
        currentStreak: Int,
        existingAchievements: List<Achievement>
    ): List<Achievement> {
        val newAchievements = mutableListOf<Achievement>()
        val existingIds = existingAchievements.map { it.id }.toSet()

        // 1. 第一次完成
        if ("first_completion" !in existingIds && records.isNotEmpty()) {
            newAchievements.add(Achievement("first_completion", "迈出第一步", "完成第一次专注学习", clock.nowMillis()))
        }

        // 2. 连续3天
        if ("streak_3" !in existingIds && currentStreak >= 3) {
            newAchievements.add(Achievement("streak_3", "坚持不懈", "连续完成3天目标", clock.nowMillis()))
        }

        // 3. 连续7天
        if ("streak_7" !in existingIds && currentStreak >= 7) {
            newAchievements.add(Achievement("streak_7", "习惯养成", "连续完成7天目标", clock.nowMillis()))
        }

        // 4. 累计10小时 (600分钟)
        val totalMinutes = records.sumOf { it.studyMinutes }
        if ("total_10_hours" !in existingIds && totalMinutes >= 600) {
            newAchievements.add(Achievement("total_10_hours", "资深学者", "累计学习满10小时", clock.nowMillis()))
        }

        return newAchievements
    }
}
