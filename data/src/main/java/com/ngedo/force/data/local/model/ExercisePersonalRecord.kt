package com.ngedo.force.data.local.model

data class ExercisePersonalRecord(
    val exerciseName: String,

    val highestWeight: Double?,
    val repsAtHighestWeight: Int?,
    val highestWeightDate: Long? = null,
    val highestWeightSessionId: Long? = null,

    val highestReps: Int?,
    val weightAtHighestReps: Double?,
    val highestRepsDate: Long? = null,
    val highestRepsSessionId: Long? = null
)