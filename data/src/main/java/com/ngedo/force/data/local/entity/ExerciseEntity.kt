package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises"
)
data class ExerciseEntity(
    @PrimaryKey
    val id: String,

    val name: String,

    val primaryMuscle: String,

    val secondaryMuscles: String,

    val equipment: String,

    val movementType: String,

    val difficulty: String,

    val instructions: String,

    val mediaPath: String? = null,

    val isCustom: Boolean = false
)