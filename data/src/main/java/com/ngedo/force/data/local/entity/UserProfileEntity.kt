package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profile"
)
data class UserProfileEntity(

    /*
     * FORCE only needs one active profile
     * for the MVP.
     */
    @PrimaryKey
    val id: Int = 1,

    val name: String,
    val age: Int,
    val sex: String,
    val activityLevel: String,
    /*
     * Body measurements
     */
    val weightKg: Double,

    val heightCm: Double,

    /*
     * Primary training goal.
     *
     * Expected values for MVP:
     * Lose Fat
     * Build Muscle
     * Maintain
     * Strength
     */
    val trainingGoal: String,

    /*
     * Metric
     * Imperial
     */
    val preferredUnits: String,

    val updatedAt: Long
)