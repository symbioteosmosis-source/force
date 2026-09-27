package com.ngedo.force.feature.workout.plan

data class TodayPlannedWorkout(
    val plannedWorkoutId: Long,
    val workoutName: String,
    val exerciseCount: Int,
    val isCompleted: Boolean
)