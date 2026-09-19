package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "planned_workouts",

    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index("planId"),
        Index("scheduledDate")
    ]
)
data class PlannedWorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val planId: Long,

    val name: String,

    val weekNumber: Int,

    val scheduledDate: Long,

    val isCompleted: Boolean = false,

    val completedSessionId: Long? = null
)