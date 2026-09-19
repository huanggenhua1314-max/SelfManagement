package com.example.selfmanagement.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.*
import com.example.selfmanagement.domain.repository.*
import com.example.selfmanagement.domain.usecase.CalculateLevelUseCase
import com.example.selfmanagement.domain.usecase.CalculateStreakUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val userId: String,
    private val userRepository: UserRepository,
    private val goalRepository: GoalRepository,
    private val growthRepository: GrowthRepository,
    private val companionRepository: CompanionRepository,
    private val settingsRepository: SettingsRepository,
    private val calculateLevelUseCase: CalculateLevelUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val clock: AppClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        // Individual observers to ensure one slow flow doesn't block others
        
        userRepository.observeUser(userId)
            .onEach { user ->
                if (user == null) {
                    userRepository.saveUser(User(userId, "用户"))
                } else {
                    _uiState.update { it.copy(nickname = user.nickname, avatarUrl = user.avatarUrl, isLoading = false) }
                }
            }.launchIn(viewModelScope)

        goalRepository.observeActiveGoal(userId)
            .onEach { goal ->
                _uiState.update { it.copy(activeGoal = goal) }
            }.launchIn(viewModelScope)

        growthRepository.observeAllRecords(userId)
            .onEach { records ->
                val totalXp = records.sumOf { it.xp }
                val level = calculateLevelUseCase(totalXp)
                val streakResult = calculateStreakUseCase(records, clock.today())
                val totalMinutes = records.sumOf { it.studyMinutes }
                
                _uiState.update { it.copy(
                    level = level,
                    totalXp = totalXp,
                    currentStreak = streakResult.currentStreak,
                    bestStreak = streakResult.bestStreak,
                    totalStudyMinutes = totalMinutes
                )}
            }.launchIn(viewModelScope)

        settingsRepository.observeThemeMode(userId)
            .onEach { theme ->
                _uiState.update { it.copy(themeMode = theme) }
            }.launchIn(viewModelScope)

        settingsRepository.observeNotificationsEnabled(userId)
            .onEach { enabled ->
                _uiState.update { it.copy(notificationsEnabled = enabled) }
            }.launchIn(viewModelScope)

        companionRepository.observeCurrentCompanion(userId)
            .flatMapLatest { companion ->
                if (companion != null) {
                    companionRepository.observeCompanionProfile(companion.companionUserId)
                } else {
                    flowOf(null)
                }
            }
            .onEach { profile ->
                _uiState.update { it.copy(companionProfile = profile) }
            }
            .launchIn(viewModelScope)
    }

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            userRepository.updateNickname(userId, nickname)
        }
    }

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.updateThemeMode(userId, mode)
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateNotificationsEnabled(userId, enabled)
        }
    }
}
