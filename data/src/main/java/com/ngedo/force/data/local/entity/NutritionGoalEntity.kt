package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "nutrition_goals"
)
data class NutritionGoalEntity(

    /*
     * FORCE only needs one active daily nutrition goal
     * for the MVP.
     *
     * We always store it using id = 1.
     */
    @PrimaryKey
    val id: Int = 1,

    val calorieTarget: Double,

    val proteinTarget: Double,

    val carbsTarget: Double,

    val fatTarget: Double,

    val updatedAt: Long
)