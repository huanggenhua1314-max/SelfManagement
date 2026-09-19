package com.example.selfmanagement.presentation.companion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.*
import com.example.selfmanagement.domain.repository.CompanionRepository
import com.example.selfmanagement.domain.repository.GoalRepository
import com.example.selfmanagement.domain.repository.TaskRepository
import com.example.selfmanagement.domain.usecase.MatchCompanionUseCase
import com.example.selfmanagement.domain.usecase.MatchResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CompanionUiState(
    val activeCompanion: Companion? = null,
    val companionProfile: CompanionProfile? = null,
    val matchResults: List<MatchResult> = emptyList(),
    val hasActiveGoal: Boolean = true,
    val isLoading: Boolean = true,
    val error: String? = null
)

class CompanionViewModel(
    private val userId: String,
    private val companionRepository: CompanionRepository,
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository,
    private val matchCompanionUseCase: MatchCompanionUseCase,
    private val clock: AppClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompanionUiState())
    val uiState: StateFlow<CompanionUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        // 1. 响应式观察当前搭子及详情
        companionRepository.observeCurrentCompanion(userId)
            .flatMapLatest { companion ->
                if (companion != null) {
                    companionRepository.observeCompanionProfile(companion.companionUserId)
                        .map { profile -> profile to companion }
                } else {
                    flowOf(null to null)
                }
            }
            .onEach { (profile, companion) ->
                _uiState.update { it.copy(
                    activeCompanion = companion,
                    companionProfile = profile,
                    // 如果已经有搭子，则不再显示 Loading
                    isLoading = if (companion != null) false else it.isLoading
                )}
            }
            .launchIn(viewModelScope)

        // 2. 响应式观察目标并加载候选人
        goalRepository.observeActiveGoal(userId)
            .flatMapLatest { goal ->
                if (goal == null) {
                    println("[CompanionVM] No active goal found for $userId")
                    _uiState.update { it.copy(hasActiveGoal = false, isLoading = false, matchResults = emptyList()) }
                    flowOf(emptyList<MatchResult>())
                } else {
                    println("[CompanionVM] Loading candidates for goal: ${goal.title}")
                    _uiState.update { it.copy(hasActiveGoal = true, isLoading = true) }
                    companionRepository.findCandidates(goal)
                        .map { candidates ->
                            matchCompanionUseCase(goal, candidates)
                        }
                        .catch { e ->
                            println("[CompanionVM] Candidate loading error: ${e.message}")
                            _uiState.update { it.copy(isLoading = false, error = "加载候选人失败") }
                            emit(emptyList())
                        }
                }
            }
            .onEach { results ->
                println("[CompanionVM] Candidates received: ${results.size}")
                _uiState.update { it.copy(matchResults = results, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun becomeCompanion(companionUserId: String) {
        viewModelScope.launch {
            val goal = goalRepository.observeActiveGoal(userId).first() 
            if (goal == null) {
                _uiState.update { it.copy(error = "请先设置目标") }
                return@launch
            }
            companionRepository.becomeCompanion(userId, companionUserId, goal.id)
        }
    }

    suspend fun findFirstTodoTaskId(): String? {
        val goal = goalRepository.observeActiveGoal(userId).first() ?: return null
        val tasks = taskRepository.getTodayTasks(goal.id, clock.today())
        return tasks.find { it.status == TaskStatus.TODO }?.id
    }

    fun simulateTogetherStart() {
        viewModelScope.launch {
            val companionId = _uiState.value.activeCompanion?.companionUserId ?: return@launch
            delay(2000)
            companionRepository.simulateCompanionStatusChange(companionId, CompanionActivityStatus.FOCUSING)
        }
    }

    fun simulateTogetherComplete() {
        viewModelScope.launch {
            val companionId = _uiState.value.activeCompanion?.companionUserId ?: return@launch
            companionRepository.simulateCompanionStatusChange(companionId, CompanionActivityStatus.COMPLETED)
        }
    }
}
