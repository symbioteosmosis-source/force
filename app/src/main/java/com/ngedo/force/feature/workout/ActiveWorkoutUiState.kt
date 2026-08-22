package com.ngedo.force.feature.workout
import com.ngedo.force.data.local.entity.WorkoutSetEntity
data class ActiveWorkoutUiState(
    val isWorkoutStarted: Boolean = false,
    val isWorkoutComplete: Boolean = false,

    val sessionId: Long? = null,
    val sessionStartedAt: Long? = null,
    val exerciseIds: List<Long> = emptyList(),
    val exerciseNames: List<String> = emptyList(),
    val isSessionReady: Boolean = false,

    val currentExerciseIndex: Int = 0,
    val currentSet: Int = 1,
    val completedSets: Int = 0,

    val currentWeight: String = "",
    val currentReps: String = "",

    val isResting: Boolean = false,
    val restSecondsRemaining: Int = 0,
    val completedSetsForCurrentExercise: List<WorkoutSetEntity> = emptyList(),

    val previousBestWeight: Double? = null,
    val previousBestReps: Int? = null,

    val isNewWeightRecord: Boolean = false,
    val isNewRepRecord: Boolean = false,

    val previousRecordWeight: Double? = null,
    val previousRecordWeightReps: Int? = null,

    val previousRecordReps: Int? = null,
    val previousRecordRepsWeight: Double? = null,

    val newRecordWeight: Double? = null,
    val newRecordReps: Int? = null,

    val pendingWorkoutCompletion: Boolean = false,
    val pendingNextExerciseIndex: Int? = null
    )