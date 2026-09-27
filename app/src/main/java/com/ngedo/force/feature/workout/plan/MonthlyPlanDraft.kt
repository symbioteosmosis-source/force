package com.ngedo.force.feature.workout.plan

data class MonthlyPlanDraft(
    val planName: String = "",
    val goal: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val trainingDays: Set<Int> = emptySet(),
    val workoutDays: List<PlanWorkoutDay> = emptyList(),
    val isConfiguringWorkouts: Boolean = false
)