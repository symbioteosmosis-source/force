package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_plans"
)
data class WorkoutPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,

    val goal: String,

    val startDate: Long,

    val endDate: Long,

    val isActive: Boolean = true,

    val createdAt: Long
)