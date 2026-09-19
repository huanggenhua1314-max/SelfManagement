package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.domain.model.CompanionProfile
import com.example.selfmanagement.domain.model.Goal
import kotlin.math.abs

data class MatchResult(
    val profile: CompanionProfile,
    val score: Int,
    val reasons: List<String>
)

class MatchCompanionUseCase {
    operator fun invoke(goal: Goal, candidates: List<CompanionProfile>): List<MatchResult> {
        return candidates.map { candidate ->
            var score = 0
            val reasons = mutableListOf<String>()

            // 1. 目标相同
            if (candidate.goalCategory == goal.category) {
                score += 50
                reasons.add("目标一致")
            }

            // 2. 学习时间接近 (preferredHour)
            val myHour = goal.preferredHour ?: 20
            if (abs(candidate.preferredHour - myHour) <= 1) {
                score += 30
                reasons.add("学习时间接近")
            }

            // 3. 每日时长接近 (dailyMinutes)
            if (abs(candidate.dailyMinutes - goal.dailyMinutes) <= 10) {
                score += 20
                reasons.add("每日时长接近")
            }

            MatchResult(candidate, score, reasons)
        }.sortedByDescending { it.score }
    }
}
