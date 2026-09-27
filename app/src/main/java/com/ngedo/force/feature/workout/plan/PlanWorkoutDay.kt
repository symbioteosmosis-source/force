package com.ngedo.force.feature.workout.plan

data class PlanWorkoutDay(
    val day: Int,
    val name: String,
    val exercises: List<PlanExercise> = emptyList()
)