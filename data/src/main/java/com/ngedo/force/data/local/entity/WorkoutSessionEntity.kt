package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sessions"
)
data class WorkoutSessionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val workoutId: Long,

    val startedAt: Long,

    val completedAt: Long? = null,

    val durationSeconds: Long = 0,

    val isCompleted: Boolean = false
)