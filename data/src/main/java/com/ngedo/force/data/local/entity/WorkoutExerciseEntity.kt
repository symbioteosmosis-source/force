package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_exercises"
)
data class WorkoutExerciseEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val sessionId: Long,

    val exerciseName: String,

    val exerciseOrder: Int,

    val completed: Boolean = false
)