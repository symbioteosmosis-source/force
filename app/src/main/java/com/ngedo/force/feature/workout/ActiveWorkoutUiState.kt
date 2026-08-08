package com.ngedo.force.feature.workout

data class ActiveWorkoutUiState(
    val isWorkoutStarted: Boolean = false,
    val currentExerciseIndex: Int = 0,
    val currentSet: Int = 1
)