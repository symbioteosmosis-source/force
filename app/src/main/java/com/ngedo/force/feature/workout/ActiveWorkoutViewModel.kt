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
            isWorkoutStarted = true
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

        val updatedCompletedSets =
            currentState.completedSets + 1

        /*
         * If this was the final set of the current exercise,
         * move to the next exercise.
         */
        if (currentState.currentSet >= totalSets) {

            if (
                currentState.currentExerciseIndex >=
                totalExercises - 1
            ) {
                /*
                 * Entire workout is complete.
                 */
                _uiState.value = currentState.copy(
                    completedSets = updatedCompletedSets,
                    isWorkoutComplete = true,
                    isResting = false,
                    restSecondsRemaining = 0
                )

                return
            }

            /*
             * Move to the next exercise.
             */
            _uiState.value = currentState.copy(
                completedSets = updatedCompletedSets,
                currentExerciseIndex =
                    currentState.currentExerciseIndex + 1,
                currentSet = 1,
                isResting = false,
                restSecondsRemaining = 0
            )

            return
        }

        /*
         * There are more sets remaining for this exercise.
         * Start the rest timer before moving to the next set.
         */
        startRestTimer(
            seconds = restSeconds,
            nextSet = currentState.currentSet + 1,
            completedSets = updatedCompletedSets
        )
    }

    fun skipRest(
        totalSets: Int,
        totalExercises: Int
    ) {
        val currentState = _uiState.value

        if (!currentState.isResting) {
            return
        }

        restTimerJob?.cancel()

        _uiState.value = currentState.copy(
            isResting = false,
            restSecondsRemaining = 0
        )

        /*
         * Skip Rest means the set progression continues.
         * The completed set has already been counted.
         */
        if (currentState.currentSet < totalSets) {

            _uiState.value = _uiState.value.copy(
                currentSet =
                    currentState.currentSet + 1
            )

        } else if (
            currentState.currentExerciseIndex <
            totalExercises - 1
        ) {

            _uiState.value = _uiState.value.copy(
                currentExerciseIndex =
                    currentState.currentExerciseIndex + 1,
                currentSet = 1
            )
        }
    }

    fun nextExercise(totalExercises: Int) {
        val currentState = _uiState.value

        if (currentState.isResting) {
            return
        }

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

    fun finishWorkout() {
        restTimerJob?.cancel()

        _uiState.value = ActiveWorkoutUiState()
    }

    private fun startRestTimer(
        seconds: Int,
        nextSet: Int,
        completedSets: Int
    ) {
        restTimerJob?.cancel()

        restTimerJob = viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                completedSets = completedSets,
                isResting = true,
                restSecondsRemaining = seconds
            )

            var remaining = seconds

            while (remaining > 0) {
                delay(1_000)
                remaining--

                _uiState.value =
                    _uiState.value.copy(
                        restSecondsRemaining = remaining
                    )
            }

            _uiState.value =
                _uiState.value.copy(
                    currentSet = nextSet,
                    isResting = false,
                    restSecondsRemaining = 0
                )
        }
    }

    override fun onCleared() {
        restTimerJob?.cancel()
        super.onCleared()
    }
}