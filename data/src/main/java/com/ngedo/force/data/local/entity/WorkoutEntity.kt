package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val category: String,

    val durationMinutes: Int,

    val caloriesBurned: Int,

    val difficulty: String,

    val bodyPart: String,

    val createdAt: Long = System.currentTimeMillis()
)