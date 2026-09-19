package com.example.selfmanagement.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.*
import com.example.selfmanagement.domain.repository.StudySessionRepository
import com.example.selfmanagement.domain.repository.TaskRepository
import com.example.selfmanagement.domain.usecase.CalculateFocusTimeUseCase
import com.example.selfmanagement.domain.usecase.CompleteTaskUseCase
import com.example.selfmanagement.domain.usecase.GenerateTodayTasksUseCase
import com.example.selfmanagement.domain.usecase.GetActiveGoalUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val goal: Goal? = null,
    val todayTasks: List<DailyTask> = emptyList(),
    val ongoingSession: StudySession? = null,
    val ongoingTask: DailyTask? = null,
    val completedMinutes: Int = 0,
    val targetMinutes: Int = 0,
    val progress: Float = 0f,
    val error: String? = null
)

class HomeViewModel(
    private val getActiveGoalUseCase: GetActiveGoalUseCase,
    private val taskRepository: TaskRepository,
    private val sessionRepository: StudySessionRepository,
    private val generateTodayTasksUseCase: GenerateTodayTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val calculateFocusTimeUseCase: CalculateFocusTimeUseCase,
    private val clock: AppClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getActiveGoalUseCase("local-user").collect { goal ->
                if (goal != null) {
                    val date = clock.today()
                    generateTodayTasksUseCase(goal, date)
                    observeTasksAndSessions(goal)
                } else {
                    _uiState.update { it.copy(isLoading = false, goal = null) }
                }
            }
        }
    }

    private fun observeTasksAndSessions(goal: Goal) {
        val date = clock.today()
        taskRepository.observeTodayTasks(goal.id, date)
            .onEach { tasks ->
                val completed = tasks.filter { it.status == TaskStatus.COMPLETED }.sumOf { it.durationMinutes }
                val target = goal.dailyMinutes
                val progress = if (target > 0) completed.toFloat() / target else 0f
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        goal = goal,
                        todayTasks = tasks,
                        completedMinutes = completed,
                        targetMinutes = target,
                        progress = progress.coerceIn(0f, 1f)
                    )
                }

                // 寻找正在运行的 Session
                tasks.forEach { task ->
                    if (task.status == TaskStatus.TODO) {
                        val activeSession = sessionRepository.getActiveSession(task.id)
                        if (activeSession != null) {
                            _uiState.update { it.copy(ongoingSession = activeSession, ongoingTask = task) }
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun formatElapsed(session: StudySession): String {
        val seconds = calculateFocusTimeUseCase(session, clock.nowMillis()) / 1000
        val m = seconds / 60
        val s = seconds % 60
        return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    }

    fun completeTask(task: DailyTask) {
        viewModelScope.launch { completeTaskUseCase(task) }
    }
}
