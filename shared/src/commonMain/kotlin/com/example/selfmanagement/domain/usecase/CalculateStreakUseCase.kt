package com.example.selfmanagement.domain.usecase

import com.example.selfmanagement.domain.model.GrowthRecord
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.minus

data class StreakResult(
    val currentStreak: Int,
    val bestStreak: Int
)

class CalculateStreakUseCase {
    operator fun invoke(records: List<GrowthRecord>, today: String): StreakResult {
        if (records.isEmpty()) return StreakResult(0, 0)
        
        val dates = records.filter { it.studyMinutes > 0 }
            .map { it.date }
            .distinct()
            .map { LocalDate.parse(it) }
            .sortedDescending()

        if (dates.isEmpty()) return StreakResult(0, 0)

        // Current Streak
        var current = 0
        val todayDate = LocalDate.parse(today)
        val yesterdayDate = todayDate.minus(1, DateTimeUnit.DAY)
        
        if (dates.first() != todayDate && dates.first() != yesterdayDate) {
            current = 0
        } else {
            var checkDate = dates.first()
            for (date in dates) {
                if (date == checkDate) {
                    current++
                    checkDate = date.minus(1, DateTimeUnit.DAY)
                } else {
                    break
                }
            }
        }

        // Best Streak
        var best = 0
        var tempBest = 0
        val reversedDates = dates.sorted() // ASC
        if (reversedDates.isNotEmpty()) {
            var expectedDate: LocalDate? = null
            for (date in reversedDates) {
                if (expectedDate == null || date == expectedDate) {
                    tempBest++
                } else {
                    best = maxOf(best, tempBest)
                    tempBest = 1
                }
                expectedDate = date.plus(1, DateTimeUnit.DAY)
            }
        }
        best = maxOf(best, tempBest)

        return StreakResult(current, best)
    }
}
