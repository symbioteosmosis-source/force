package com.ngedo.force.data.local.model

data class ExercisePersonalRecord(
    val exerciseName: String,

    val highestWeight: Double?,
    val repsAtHighestWeight: Int?,

    val highestReps: Int?,
    val weightAtHighestReps: Double?
)