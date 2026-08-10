package com.ngedo.force.feature.workout

data class ActiveWorkoutUiState(
    val isWorkoutStarted: Boolean = false,
    val isWorkoutComplete: Boolean = false,

    val sessionId: Long? = null,
    val sessionStartedAt: Long? = null,
    val exerciseIds: List<Long> = emptyList(),
    val isSessionReady: Boolean = false,

    val currentExerciseIndex: Int = 0,
    val currentSet: Int = 1,
    val completedSets: Int = 0,

    val currentWeight: String = "",
    val currentReps: String = "",

    val isResting: Boolean = false,
    val restSecondsRemaining: Int = 0
)