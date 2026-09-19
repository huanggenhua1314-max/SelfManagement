package com.example.selfmanagement.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfmanagement.domain.model.Goal
import com.example.selfmanagement.domain.model.GoalCategory
import com.example.selfmanagement.domain.usecase.CreateGoalUseCase
import com.example.selfmanagement.domain.usecase.UpdateGoalUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateGoalViewModel(
    private val createGoalUseCase: CreateGoalUseCase,
    private val updateGoalUseCase: UpdateGoalUseCase
) : ViewModel() {

    private var initialGoal: Goal? = null

    private val _category = MutableStateFlow(GoalCategory.ENGLISH)
    val category: StateFlow<GoalCategory> = _category.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _targetDays = MutableStateFlow(90)
    val targetDays: StateFlow<Int> = _targetDays.asStateFlow()

    private val _dailyMinutes = MutableStateFlow(30)
    val dailyMinutes: StateFlow<Int> = _dailyMinutes.asStateFlow()

    private val _preferredHour = MutableStateFlow<Int?>(20)
    val preferredHour: StateFlow<Int?> = _preferredHour.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    fun loadInitialGoal(goal: Goal) {
        initialGoal = goal
        _category.value = goal.category
        _title.value = goal.title
        _targetDays.value = goal.targetDays
        _dailyMinutes.value = goal.dailyMinutes
        _preferredHour.value = goal.preferredHour
    }

    fun updateCategory(category: GoalCategory) { _category.value = category }
    fun updateTitle(title: String) { _title.value = title }
    fun updateTargetDays(days: Int) { _targetDays.value = days }
    fun updateDailyMinutes(minutes: Int) { _dailyMinutes.value = minutes }
    fun updatePreferredHour(hour: Int?) { _preferredHour.value = hour }

    fun submit() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = if (initialGoal == null) {
                createGoalUseCase(
                    userId = "local-user",
                    category = _category.value,
                    title = _title.value,
                    targetDays = _targetDays.value,
                    dailyMinutes = _dailyMinutes.value,
                    preferredHour = _preferredHour.value
                )
            } else {
                updateGoalUseCase(
                    currentGoal = initialGoal!!,
                    title = _title.value,
                    targetDays = _targetDays.value,
                    dailyMinutes = _dailyMinutes.value,
                    preferredHour = _preferredHour.value
                )
            }
            
            _isLoading.value = false
            if (result.isSuccess) {
                _isSuccess.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }
}
