package com.ngedo.force.feature.workout.plan

data class PlanWorkoutDay(
    val day: Int,
    val name: String,
    val exercises: List<PlanExercise> = emptyList(),

    // Null while building a new unsaved plan.
    // Set once this represents a real scheduled workout.
    val plannedWorkoutId: Long? = null,

    // Week inside the plan.
    val weekNumber: Int = 1,

    // Exact scheduled date for saved workouts.
    val scheduledDate: Long? = null,

    // True once this exact scheduled workout has been completed.
    val isCompleted: Boolean = false
)