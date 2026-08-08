package com.ngedo.force.feature.workout

data class ActiveWorkoutUiState(
    val isWorkoutStarted: Boolean = false,
    val isWorkoutComplete: Boolean = false,
    val currentExerciseIndex: Int = 0,
    val currentSet: Int = 1,
    val completedSets: Int = 0,
    val isResting: Boolean = false,
    val restSecondsRemaining: Int = 0
)