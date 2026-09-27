package com.ngedo.force.feature.workout.plan

data class MonthlyPlanUiState(
    val planName: String = "",
    val goal: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val trainingDays: Set<Int> = emptySet(),
    val workoutDays: List<PlanWorkoutDay> = emptyList(),
    val selectedWorkoutDay: Int? = null,

    val isConfiguringWorkouts: Boolean = false,
    val isEditingPlan: Boolean = false,

    val activePlanId: Long? = null,
    val hasActivePlan: Boolean = false,
    val hasDraft: Boolean = false,
    val isDraftRecoveryPending: Boolean = false,
    val showSavedConfirmation: Boolean = false,

    val dateRangeWarning: String? = null,

    val todayWorkout: TodayPlannedWorkout? = null,

    val isLoadingPlan: Boolean = true,

    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)