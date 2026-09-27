package com.ngedo.force.feature.workout.plan

data class PlanExercise(
    val exerciseId: String,
    val exerciseName: String,
    val target: String,
    val sets: Int = 3,
    val reps: String = "8-12",
    val restSeconds: Int = 90
)