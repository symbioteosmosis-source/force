package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "workouts",
    indices = [
        Index(value = ["name"])
    ]
)
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