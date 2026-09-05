package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "saved_foods",
    indices = [
        Index(
            value = ["normalizedName"],
            unique = true
        )
    ]
)
data class SavedFoodEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /*
     * Display name entered by the user.
     *
     * Example:
     * Chicken Breast
     */
    val foodName: String,

    /*
     * Normalized version of the name used to
     * prevent duplicate saved foods.
     *
     * Example:
     * "Chicken Breast" -> "chicken breast"
     */
    val normalizedName: String,

    /*
     * Saved nutrition values.
     *
     * These values can later be copied into
     * Breakfast, Lunch, Dinner or Snack.
     */
    val calories: Double,

    val proteinGrams: Double,

    val carbsGrams: Double,

    val fatGrams: Double,

    /*
     * Last time this saved food was created
     * or updated.
     *
     * This will later allow recently used foods
     * to appear higher in suggestions.
     */
    val updatedAt: Long
)