package com.example.selfmanagement.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfmanagement.core.AppClock
import com.example.selfmanagement.domain.model.DailyTask
import com.example.selfmanagement.domain.model.StudySession
import com.example.selfmanagement.domain.model.StudySessionStatus
import com.example.selfmanagement.domain.repository.StudySessionRepository
import com.example.selfmanagement.domain.repository.TaskRepository
import com.example.selfmanagement.domain.usecase.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FocusUiState(
    val task: DailyTask? = null,
    val session: StudySession? = null,
    val elapsedSeconds: Long = 0,
    val remainingSeconds: Long = 0,
    val progress: Float = 0f,
    val isFinished: Boolean = false
)

class FocusViewModel(
    private val taskId: String,
    private val taskRepository: TaskRepository,
    private val sessionRepository: StudySessionRepository,
    private val startFocusUseCase: StartFocusUseCase,
    private val pauseFocusUseCase: PauseFocusUseCase,
    private val resumeFocusUseCase: ResumeFocusUseCase,
    private val completeFocusUseCase: CompleteFocusUseCase,
    private val calculateFocusTimeUseCase: CalculateFocusTimeUseCase,
    private val clock: AppClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadTask()
        observeSession()
    }

    private fun loadTask() {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId)
            _uiState.update { it.copy(task = task) }
        }
    }

    private fun observeSession() {
        sessionRepository.observeSession(taskId)
            .onEach { session ->
                _uiState.update { it.copy(session = session) }
                // 状态变化时强制计算一次
                calculateCurrentTime(session)
                
                if (session?.status == StudySessionStatus.RUNNING) {
                    startTimerLoop()
                } else {
                    stopTimerLoop()
                }
                
                if (session?.status == StudySessionStatus.COMPLETED) {
                    _uiState.update { it.copy(isFinished = true) }
                }
            }.launchIn(viewModelScope)
    }

    private fun startTimerLoop() {
        if (timerJob?.isActive == true) return
        timerJob = viewModelScope.launch {
            while (true) {
                calculateCurrentTime(_uiState.value.session)
                delay(500)
            }
        }
    }

    private fun stopTimerLoop() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun calculateCurrentTime(session: StudySession?) {
        val task = _uiState.value.task ?: return
        
        val totalElapsedMillis = calculateFocusTimeUseCase(session, clock.nowMillis())
        val elapsedSeconds = totalElapsedMillis / 1000
        val targetSeconds = task.durationMinutes * 60L
        val remainingSeconds = (targetSeconds - elapsedSeconds).coerceAtLeast(0)
        val progress = if (targetSeconds > 0) elapsedSeconds.toFloat() / targetSeconds else 0f

        _uiState.update { 
            it.copy(
                elapsedSeconds = elapsedSeconds,
                remainingSeconds = remainingSeconds,
                progress = progress.coerceIn(0f, 1f)
            )
        }

        // 自动完成逻辑
        if (elapsedSeconds >= targetSeconds && session?.status == StudySessionStatus.RUNNING) {
            complete()
        }
    }

    fun start() { viewModelScope.launch { startFocusUseCase(taskId) } }
    fun pause() { 
        viewModelScope.launch { 
            pauseFocusUseCase(taskId)
            // 暂停后立即重新计算一次，确保 UI 冻结在正确时间
            calculateCurrentTime(sessionRepository.getActiveSession(taskId))
        } 
    }
    fun resume() { viewModelScope.launch { resumeFocusUseCase(taskId) } }
    fun complete() { viewModelScope.launch { completeFocusUseCase(taskId) } }
}
