package com.ngedo.force.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActiveWorkoutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ActiveWorkoutUiState()
    )

    val uiState: StateFlow<ActiveWorkoutUiState> =
        _uiState.asStateFlow()

    private var restTimerJob: Job? = null

    fun startWorkout() {
        restTimerJob?.cancel()

        _uiState.value = ActiveWorkoutUiState(
            isWorkoutStarted = true,
            isWorkoutComplete = false,
            currentExerciseIndex = 0,
            currentSet = 1,
            completedSets = 0,
            completedExercises = 0,
            isResting = false,
            restSecondsRemaining = 0
        )
    }

    fun completeSet(
        totalSets: Int,
        totalExercises: Int,
        restSeconds: Int
    ) {
        val currentState = _uiState.value

        if (currentState.isResting) {
            return
        }

        if (currentState.currentSet < totalSets) {

            startRestTimer(
                restSeconds = restSeconds,
                totalSets = totalSets
            )

            return
        }

        /*
         * Final set of the current exercise.
         */
        val newCompletedSets =
            currentState.completedSets + 1

        if (
            currentState.currentExerciseIndex <
            totalExercises - 1
        ) {

            _uiState.value = currentState.copy(
                currentExerciseIndex =
                    currentState.currentExerciseIndex + 1,

                currentSet = 1,

                completedSets = newCompletedSets,

                completedExercises =
                    currentState.completedExercises + 1,

                isResting = false,

                restSecondsRemaining = 0
            )

            return
        }

        /*
         * Final set of the final exercise.
         */
        completeWorkout(
            completedSets = newCompletedSets,
            totalExercises = totalExercises
        )
    }

    fun skipRest(
        totalSets: Int,
        totalExercises: Int
    ) {
        if (!_uiState.value.isResting) {
            return
        }

        restTimerJob?.cancel()
        restTimerJob = null

        val currentState = _uiState.value

        val newCompletedSets =
            currentState.completedSets + 1

        if (currentState.currentSet < totalSets) {

            _uiState.value = currentState.copy(
                isResting = false,
                restSecondsRemaining = 0,
                currentSet = currentState.currentSet + 1,
                completedSets = newCompletedSets
            )

        } else if (
            currentState.currentExerciseIndex <
            totalExercises - 1
        ) {

            _uiState.value = currentState.copy(
                currentExerciseIndex =
                    currentState.currentExerciseIndex + 1,
                currentSet = 1,
                completedSets = newCompletedSets,
                completedExercises =
                    currentState.completedExercises + 1,
                isResting = false,
                restSecondsRemaining = 0
            )

        } else {

            completeWorkout(
                completedSets = newCompletedSets,
                totalExercises = totalExercises
            )
        }
    }

    fun nextExercise(
        totalExercises: Int
    ) {
        restTimerJob?.cancel()
        restTimerJob = null

        val currentState = _uiState.value

        if (
            currentState.currentExerciseIndex <
            totalExercises - 1
        ) {

            _uiState.value = currentState.copy(
                currentExerciseIndex =
                    currentState.currentExerciseIndex + 1,

                currentSet = 1,

                isResting = false,

                restSecondsRemaining = 0
            )
        }
    }

    private fun startRestTimer(
        restSeconds: Int,
        totalSets: Int
    ) {
        restTimerJob?.cancel()

        restTimerJob = viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isResting = true,
                restSecondsRemaining = restSeconds
            )

            var remaining = restSeconds

            while (remaining > 0) {

                delay(1_000)

                remaining--

                _uiState.value =
                    _uiState.value.copy(
                        restSecondsRemaining = remaining
                    )
            }

            if (
                _uiState.value.isResting &&
                _uiState.value.currentSet < totalSets
            ) {

                _uiState.value =
                    _uiState.value.copy(
                        isResting = false,
                        restSecondsRemaining = 0,
                        currentSet =
                            _uiState.value.currentSet + 1
                    )
            }
        }
    }

    private fun completeWorkout(
        completedSets: Int,
        totalExercises: Int
    ) {
        restTimerJob?.cancel()
        restTimerJob = null

        _uiState.value = _uiState.value.copy(
            isWorkoutStarted = false,
            isWorkoutComplete = true,
            completedSets = completedSets,
            completedExercises = totalExercises,
            isResting = false,
            restSecondsRemaining = 0
        )
    }

    fun dismissCompletion() {
        _uiState.value = ActiveWorkoutUiState()
    }

    override fun onCleared() {
        restTimerJob?.cancel()
        super.onCleared()
    }
}