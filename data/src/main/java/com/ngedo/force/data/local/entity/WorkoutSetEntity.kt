package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sets"
)
data class WorkoutSetEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val workoutExerciseId: Long,

    val setNumber: Int,

    val reps: Int,

    val weight: Double
)