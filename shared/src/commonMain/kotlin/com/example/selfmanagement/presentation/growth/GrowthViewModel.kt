package com.example.selfmanagement.presentation.growth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.Achievement
import com.example.selfmanagement.domain.usecase.CalculateLevelUseCase
import com.example.selfmanagement.domain.usecase.CalculateStreakUseCase
import com.example.selfmanagement.domain.repository.GrowthRepository
import kotlinx.coroutines.flow.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

data class GrowthUiState(
    val totalStudyMinutes: Int = 0,
    val totalXp: Int = 0,
    val level: Int = 1,
    val xpInLevel: Int = 0,
    val xpThreshold: Int = 100,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val weeklyCompletion: List<Boolean> = emptyList(), // Last 7 days
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = true
)

class GrowthViewModel(
    private val userId: String,
    private val growthRepository: GrowthRepository,
    private val calculateLevelUseCase: CalculateLevelUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val clock: AppClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrowthUiState())
    val uiState: StateFlow<GrowthUiState> = _uiState.asStateFlow()

    init {
        val recordsFlow = growthRepository.observeAllRecords(userId)
        val achievementsFlow = growthRepository.observeAchievements()

        combine(recordsFlow, achievementsFlow) { records, achievements ->
            val totalXp = records.sumOf { it.xp }
            val totalMinutes = records.sumOf { it.studyMinutes }
            val level = calculateLevelUseCase(totalXp)
            val currentLevelXp = calculateLevelUseCase.getXpThreshold(level)
            val nextLevelXp = calculateLevelUseCase.getXpThreshold(level + 1)
            
            val streakResult = calculateStreakUseCase(records, clock.today())
            
            val today = LocalDate.parse(clock.today())
            val weekly = (0..6).map { i ->
                val date = today.minus(i, DateTimeUnit.DAY).toString()
                records.any { it.date == date && it.studyMinutes > 0 }
            }.reversed()

            GrowthUiState(
                totalStudyMinutes = totalMinutes,
                totalXp = totalXp,
                level = level,
                xpInLevel = totalXp - currentLevelXp,
                xpThreshold = nextLevelXp - currentLevelXp,
                currentStreak = streakResult.currentStreak,
                bestStreak = streakResult.bestStreak,
                weeklyCompletion = weekly,
                achievements = achievements,
                isLoading = false
            )
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }
}
