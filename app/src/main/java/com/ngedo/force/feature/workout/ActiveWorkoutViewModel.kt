package com.ngedo.force.feature.workout

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActiveWorkoutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ActiveWorkoutUiState()
    )

    val uiState: StateFlow<ActiveWorkoutUiState> =
        _uiState.asStateFlow()

    fun startWorkout() {
        _uiState.value = _uiState.value.copy(
            isWorkoutStarted = true,
            currentExerciseIndex = 0,
            currentSet = 1
        )
    }

    fun completeSet() {
        _uiState.value = _uiState.value.copy(
            currentSet = _uiState.value.currentSet + 1
        )
    }

    fun nextExercise() {
        _uiState.value = _uiState.value.copy(
            currentExerciseIndex =
                _uiState.value.currentExerciseIndex + 1,
            currentSet = 1
        )
    }
}