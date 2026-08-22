package com.ngedo.force.data.local.model

data class PersonalRecordResult(
    val isNewWeightRecord: Boolean = false,
    val isNewRepRecord: Boolean = false,

    val previousHighestWeight: Double? = null,
    val previousRepsAtHighestWeight: Int? = null,

    val previousHighestReps: Int? = null,
    val previousWeightAtHighestReps: Double? = null
)