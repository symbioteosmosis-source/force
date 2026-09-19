package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_exercises"
)
data class FavoriteExerciseEntity(
    @PrimaryKey
    val exerciseName: String,
    val createdAt: Long
)