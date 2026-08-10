package com.ngedo.force.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngedo.force.data.local.repository.WorkoutSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ActiveWorkoutUiState()
    )

    val uiState: StateFlow<ActiveWorkoutUiState> =
        _uiState.asStateFlow()

    private var restTimerJob: Job? = null
    fun updateCurrentWeight(weight: String) {
        _uiState.value = _uiState.value.copy(
            currentWeight = weight
        )
    }

    fun updateCurrentReps(reps: String) {
        _uiState.value = _uiState.value.copy(
            currentReps = reps
        )
    }

    fun startWorkout(
        exerciseNames: List<String>
    ) {
        restTimerJob?.cancel()

        viewModelScope.launch {

            val startedAt =
                System.currentTimeMillis()

            val sessionId =
                workoutSessionRepository.startSession(
                    workoutId = 0L,
                    startedAt = startedAt
                )

            val exerciseIds =
                exerciseNames.mapIndexed { index, name ->

                    workoutSessionRepository.addExercise(
                        sessionId = sessionId,
                        exerciseName = name,
                        exerciseOrder = index
                    )
                }

            _uiState.value =
                ActiveWorkoutUiState(
                    isWorkoutStarted = true,
                    sessionId = sessionId,
                    sessionStartedAt = startedAt,
                    exerciseIds = exerciseIds,
                    isSessionReady = true
                )
        }
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

        val exerciseId =
            currentState.exerciseIds
                .getOrNull(currentState.currentExerciseIndex)

        val reps =
            currentState.currentReps
                .toIntOrNull()

        val weight =
            currentState.currentWeight
                .toDoubleOrNull()

        /*
         * We cannot save the set unless the current
         * exercise, reps and weight are valid.
         */
        if (
            exerciseId == null ||
            reps == null ||
            weight == null
        ) {
            return
        }

        viewModelScope.launch {

            /*
             * Save this exact set before progressing.
             *
             * Weight belongs to the SET, not individual reps.
             * This also allows future drop sets.
             */
            workoutSessionRepository.addSet(
                workoutExerciseId = exerciseId,
                setNumber = currentState.currentSet,
                reps = reps,
                weight = weight
            )

            if (currentState.currentSet >= totalSets) {
                workoutSessionRepository.completeExercise(
                    exerciseId = exerciseId
                )
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
                    val completedAt =
                        System.currentTimeMillis()

                    val sessionStartedAt =
                        currentState.sessionStartedAt

                    val sessionId =
                        currentState.sessionId

                    if (
                        sessionStartedAt != null &&
                        sessionId != null
                    ) {

                        val durationSeconds =
                            (
                                    completedAt - sessionStartedAt
                                    ) / 1_000L

                        workoutSessionRepository.completeSession(
                            sessionId = sessionId,
                            completedAt = completedAt,
                            durationSeconds = durationSeconds
                        )
                    }

                    _uiState.value = currentState.copy(
                        completedSets = updatedCompletedSets,
                        isWorkoutComplete = true,
                        isResting = false,
                        restSecondsRemaining = 0
                    )

                    return@launch
                }

                /*
                 * Move to the next exercise.
                 */
                _uiState.value = currentState.copy(
                    completedSets = updatedCompletedSets,
                    currentExerciseIndex =
                        currentState.currentExerciseIndex + 1,
                    currentSet = 1,
                    currentWeight = "",
                    currentReps = "",
                    isResting = false,
                    restSecondsRemaining = 0
                )

                return@launch
            }

            /*
             * There are more sets remaining.
             *
             * The completed set is already saved,
             * so now start the rest timer.
             */
            startRestTimer(
                seconds = restSeconds,
                nextSet = currentState.currentSet + 1,
                completedSets = updatedCompletedSets
            )
        }
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
                currentSet = currentState.currentSet + 1,
                currentWeight = "",
                currentReps = ""
            )

        } else if (
            currentState.currentExerciseIndex <
            totalExercises - 1
        ) {

            _uiState.value = _uiState.value.copy(
                currentExerciseIndex =
                    currentState.currentExerciseIndex + 1,
                currentSet = 1,
                currentWeight = "",
                currentReps = ""
            )
        }
    }

    fun nextExercise(totalExercises: Int) {
        val currentState = _uiState.value

        if (currentState.isResting) {
            return
        }

        /*
         * If this is the last exercise,
         * Next Exercise means finish the workout.
         */
        if (
            currentState.currentExerciseIndex >=
            totalExercises - 1
        ) {

            val completedAt =
                System.currentTimeMillis()

            val sessionStartedAt =
                currentState.sessionStartedAt

            val sessionId =
                currentState.sessionId

            viewModelScope.launch {

                if (
                    sessionStartedAt != null &&
                    sessionId != null
                ) {

                    val durationSeconds =
                        (
                                completedAt - sessionStartedAt
                                ) / 1_000L

                    workoutSessionRepository.completeSession(
                        sessionId = sessionId,
                        completedAt = completedAt,
                        durationSeconds = durationSeconds
                    )
                }

                _uiState.value = currentState.copy(
                    isWorkoutComplete = true,
                    isResting = false,
                    restSecondsRemaining = 0
                )
            }

            return
        }

        /*
         * Otherwise move to the next exercise.
         */
        _uiState.value = currentState.copy(
            currentExerciseIndex =
                currentState.currentExerciseIndex + 1,
            currentSet = 1,
            currentWeight = "",
            currentReps = "",
            isResting = false,
            restSecondsRemaining = 0
        )
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
                    currentWeight = "",
                    currentReps = "",
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