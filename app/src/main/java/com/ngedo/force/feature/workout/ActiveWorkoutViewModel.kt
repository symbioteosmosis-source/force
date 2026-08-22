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

    private var setsObserverJob: Job? = null

    private val _uiState = MutableStateFlow(
        ActiveWorkoutUiState()
    )

    val uiState: StateFlow<ActiveWorkoutUiState> =
        _uiState.asStateFlow()

    private fun observeSetsForCurrentExercise(
        exerciseId: Long
    ) {
        setsObserverJob?.cancel()

        setsObserverJob = viewModelScope.launch {

            workoutSessionRepository
                .getSetsForExercise(exerciseId)
                .collect { sets ->

                    _uiState.value =
                        _uiState.value.copy(
                            completedSetsForCurrentExercise = sets
                        )
                }
        }
    }


    private fun loadPreviousBest(
        exerciseId: Long,
        exerciseName: String
    ) {

        viewModelScope.launch {

            val previousBest =
                workoutSessionRepository.getPreviousBestSet(
                    exerciseName = exerciseName,
                    currentExerciseId = exerciseId
                )

            _uiState.value =
                _uiState.value.copy(
                    previousBestWeight =
                        previousBest?.weight,

                    previousBestReps =
                        previousBest?.reps
                )
        }
    }

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
                    exerciseNames = exerciseNames,
                    isSessionReady = true
                )

            exerciseIds.firstOrNull()?.let { exerciseId ->

                observeSetsForCurrentExercise(
                    exerciseId
                )

                loadPreviousBest(
                    exerciseId = exerciseId,
                    exerciseName = exerciseNames.first()
                )
            }
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
                ?: 0

        val weight =
            currentState.currentWeight
                .toDoubleOrNull()
                ?: 0.0

        /*
         * Exercise ID is required because the completed
         * set must belong to an exercise.
         *
         * Weight and reps are optional.
         * Empty fields are stored as 0 so the user can
         * complete the set without entering them.
         */
        if (exerciseId == null) {
            return
        }
        viewModelScope.launch {

            /*
             * Save this exact set before progressing.
             *
             * Weight belongs to the SET, not individual reps.
             * This also allows future drop sets.
             */
            val exerciseName =
                currentState.exerciseNames
                    .getOrNull(
                        currentState.currentExerciseIndex
                    )

            val personalRecordResult =
                if (exerciseName != null) {

                    workoutSessionRepository
                        .checkPersonalRecord(
                            exerciseName = exerciseName,
                            newWeight = weight,
                            newReps = reps
                        )

                } else {
                    null
                }


            workoutSessionRepository.addSet(
                workoutExerciseId = exerciseId,
                setNumber = currentState.currentSet,
                reps = reps,
                weight = weight
            )

            if (personalRecordResult != null) {

                _uiState.value =
                    _uiState.value.copy(
                        isNewWeightRecord =
                            personalRecordResult.isNewWeightRecord,

                        isNewRepRecord =
                            personalRecordResult.isNewRepRecord,

                        previousRecordWeight =
                            personalRecordResult.previousHighestWeight,

                        previousRecordWeightReps =
                            personalRecordResult.previousRepsAtHighestWeight,

                        previousRecordReps =
                            personalRecordResult.previousHighestReps,

                        previousRecordRepsWeight =
                            personalRecordResult.previousWeightAtHighestReps,

                        newRecordWeight = weight,
                        newRecordReps = reps
                    )
            }

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

                    /*
                     * IMPORTANT:
                     *
                     * Do NOT increment currentExerciseIndex here.
                     * We are already on the final exercise.
                     */

                    val hasNewPersonalRecord =
                        personalRecordResult?.isNewWeightRecord == true ||
                                personalRecordResult?.isNewRepRecord == true

                    _uiState.value =
                        _uiState.value.copy(
                            completedSets = updatedCompletedSets,
                            currentWeight = "",
                            currentReps = "",
                            isResting = false,
                            restSecondsRemaining = 0,

                            pendingWorkoutCompletion =
                                hasNewPersonalRecord,

                            isWorkoutComplete =
                                !hasNewPersonalRecord
                        )

                    return@launch
                }

                /*
 * Move to the next exercise.
 */

                val nextExerciseIndex =
                    currentState.currentExerciseIndex + 1

                val hasNewPersonalRecord =
                    personalRecordResult?.isNewWeightRecord == true ||
                            personalRecordResult?.isNewRepRecord == true

                /*
                 * If this final set created a PR,
                 * stay on the current exercise until
                 * the user presses Continue.
                 */
                if (hasNewPersonalRecord) {

                    _uiState.value =
                        _uiState.value.copy(
                            completedSets = updatedCompletedSets,
                            currentWeight = "",
                            currentReps = "",
                            isResting = false,
                            restSecondsRemaining = 0,

                            pendingNextExerciseIndex =
                                nextExerciseIndex,

                            pendingWorkoutCompletion = false,
                            isWorkoutComplete = false
                        )

                    return@launch
                }

                /*
                 * No PR.
                 * Move immediately to the next exercise.
                 */

                val nextExerciseId =
                    currentState.exerciseIds
                        .getOrNull(nextExerciseIndex)

                _uiState.value =
                    _uiState.value.copy(
                        completedSets = updatedCompletedSets,
                        currentExerciseIndex = nextExerciseIndex,
                        currentSet = 1,
                        currentWeight = "",
                        currentReps = "",
                        isResting = false,
                        restSecondsRemaining = 0,
                        completedSetsForCurrentExercise = emptyList(),
                        previousBestWeight = null,
                        previousBestReps = null,
                        pendingNextExerciseIndex = null
                    )

                nextExerciseId?.let { exerciseId ->

                    observeSetsForCurrentExercise(
                        exerciseId
                    )

                    currentState.exerciseNames
                        .getOrNull(nextExerciseIndex)
                        ?.let { exerciseName ->

                            loadPreviousBest(
                                exerciseId = exerciseId,
                                exerciseName = exerciseName
                            )
                        }
                }

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

            val nextExerciseIndex =
                currentState.currentExerciseIndex + 1

            val nextExerciseId =
                currentState.exerciseIds
                    .getOrNull(nextExerciseIndex)

            _uiState.value = _uiState.value.copy(
                currentExerciseIndex = nextExerciseIndex,
                currentSet = 1,
                currentWeight = "",
                currentReps = "",
                completedSetsForCurrentExercise = emptyList()
            )

            nextExerciseId?.let { exerciseId ->

                observeSetsForCurrentExercise(
                    exerciseId
                )

                currentState.exerciseNames
                    .getOrNull(nextExerciseIndex)
                    ?.let { exerciseName ->

                        loadPreviousBest(
                            exerciseId = exerciseId,
                            exerciseName = exerciseName
                        )
                    }
            }
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
        val nextExerciseIndex =
            currentState.currentExerciseIndex + 1

        val nextExerciseId =
            currentState.exerciseIds
                .getOrNull(nextExerciseIndex)

        _uiState.value = currentState.copy(
            currentExerciseIndex = nextExerciseIndex,
            currentSet = 1,
            currentWeight = "",
            currentReps = "",
            isResting = false,
            restSecondsRemaining = 0,
            completedSetsForCurrentExercise = emptyList(),
            previousBestWeight = null,
            previousBestReps = null
        )

        nextExerciseId?.let { exerciseId ->

            observeSetsForCurrentExercise(
                exerciseId
            )

            currentState.exerciseNames
                .getOrNull(nextExerciseIndex)
                ?.let { exerciseName ->

                    loadPreviousBest(
                        exerciseId = exerciseId,
                        exerciseName = exerciseName
                    )
                }
        }
    }


    fun clearPersonalRecordNotification() {

        val currentState =
            _uiState.value

        val shouldCompleteWorkout =
            currentState.pendingWorkoutCompletion

        val nextExerciseIndex =
            currentState.pendingNextExerciseIndex

        /*
         * Final workout PR
         */
        if (shouldCompleteWorkout) {

            _uiState.value =
                currentState.copy(
                    isNewWeightRecord = false,
                    isNewRepRecord = false,

                    previousRecordWeight = null,
                    previousRecordWeightReps = null,

                    previousRecordReps = null,
                    previousRecordRepsWeight = null,

                    newRecordWeight = null,
                    newRecordReps = null,

                    pendingWorkoutCompletion = false,
                    pendingNextExerciseIndex = null,

                    isWorkoutComplete = true
                )

            return
        }

        /*
         * Final set of an exercise PR.
         * Continue to the next exercise.
         */
        if (nextExerciseIndex != null) {

            val nextExerciseId =
                currentState.exerciseIds
                    .getOrNull(nextExerciseIndex)

            _uiState.value =
                currentState.copy(
                    isNewWeightRecord = false,
                    isNewRepRecord = false,

                    previousRecordWeight = null,
                    previousRecordWeightReps = null,

                    previousRecordReps = null,
                    previousRecordRepsWeight = null,

                    newRecordWeight = null,
                    newRecordReps = null,

                    pendingWorkoutCompletion = false,
                    pendingNextExerciseIndex = null,

                    currentExerciseIndex =
                        nextExerciseIndex,

                    currentSet = 1,
                    currentWeight = "",
                    currentReps = "",

                    completedSetsForCurrentExercise =
                        emptyList(),

                    previousBestWeight = null,
                    previousBestReps = null,

                    isResting = false,
                    restSecondsRemaining = 0
                )

            nextExerciseId?.let { exerciseId ->

                observeSetsForCurrentExercise(
                    exerciseId
                )

                currentState.exerciseNames
                    .getOrNull(nextExerciseIndex)
                    ?.let { exerciseName ->

                        loadPreviousBest(
                            exerciseId = exerciseId,
                            exerciseName = exerciseName
                        )
                    }
            }

            return
        }

        /*
         * Normal PR during an exercise.
         */
        _uiState.value =
            currentState.copy(
                isNewWeightRecord = false,
                isNewRepRecord = false,

                previousRecordWeight = null,
                previousRecordWeightReps = null,

                previousRecordReps = null,
                previousRecordRepsWeight = null,

                newRecordWeight = null,
                newRecordReps = null
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
                currentWeight = "",
                currentReps = "",
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
        setsObserverJob?.cancel()
        super.onCleared()
    }
}