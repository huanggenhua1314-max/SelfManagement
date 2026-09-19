package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.GrowthRecord
import com.example.selfmanagement.domain.model.StudySession
import com.example.selfmanagement.domain.repository.GrowthRepository
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class RecordGrowthUseCase(
    private val growthRepository: GrowthRepository,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val calculateAchievementUseCase: CalculateAchievementUseCase,
    private val clock: AppClock
) {
    suspend operator fun invoke(userId: String, session: StudySession): Result<Unit> {
        if (growthRepository.isSourceSettled(session.id)) {
            return Result.success(Unit)
        }

        val minutes = (session.durationSeconds / 60).toInt()
        if (minutes <= 0) return Result.success(Unit)

        val record = GrowthRecord(
            id = Random.nextLong().toString(),
            userId = userId,
            date = clock.today(),
            studyMinutes = minutes,
            xp = minutes,
            sourceId = session.id
        )

        growthRepository.saveRecord(record)

        val allRecords = growthRepository.observeAllRecords(userId).first()
        val existingAchievements = growthRepository.observeAchievements().first()
        val streakResult = calculateStreakUseCase(allRecords, clock.today())
        
        val newAchievements = calculateAchievementUseCase(allRecords, streakResult.currentStreak, existingAchievements)
        newAchievements.forEach {
            growthRepository.saveAchievement(it)
        }

        return Result.success(Unit)
    }
}
