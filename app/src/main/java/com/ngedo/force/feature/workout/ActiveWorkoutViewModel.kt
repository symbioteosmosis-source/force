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

    fun completeSet(
        totalSets: Int,
        totalExercises: Int
    ) {
        val currentState = _uiState.value

        if (currentState.currentSet < totalSets) {
            _uiState.value = currentState.copy(
                currentSet = currentState.currentSet + 1
            )
        } else if (
            currentState.currentExerciseIndex <
            totalExercises - 1
        ) {
            _uiState.value = currentState.copy(
                currentExerciseIndex =
                    currentState.currentExerciseIndex + 1,
                currentSet = 1
            )
        }
    }

    fun nextExercise(totalExercises: Int) {
        val currentState = _uiState.value

        if (currentState.currentExerciseIndex < totalExercises - 1) {
            _uiState.value = currentState.copy(
                currentExerciseIndex =
                    currentState.currentExerciseIndex + 1,
                currentSet = 1
            )
        }
    }
}