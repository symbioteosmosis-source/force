package com.ngedo.force.feature.workout

data class PlannedWorkoutExercise(
    val exerciseId: String? = null,
    val name: String,
    val target: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int
)