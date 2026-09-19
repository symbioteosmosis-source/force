package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "planned_exercises",

    foreignKeys = [
        ForeignKey(
            entity = PlannedWorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["plannedWorkoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index("plannedWorkoutId"),
        Index("exerciseId")
    ]
)
data class PlannedExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val plannedWorkoutId: Long,

    val exerciseId: String,

    val exerciseName: String,

    val target: String,

    val exerciseOrder: Int,

    val sets: Int,

    val reps: String,

    val restSeconds: Int
)