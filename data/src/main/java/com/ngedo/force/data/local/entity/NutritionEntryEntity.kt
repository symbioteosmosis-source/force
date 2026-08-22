package com.ngedo.force.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "nutrition_entries"
)
data class NutritionEntryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /*
     * Date the food belongs to.
     *
     * We store the start of the day as a timestamp.
     * This will allow us to retrieve all food
     * consumed on a particular day.
     */
    val date: Long,

    /*
     * Breakfast
     * Lunch
     * Dinner
     * Snack
     */
    val mealType: String,

    /*
     * Example:
     * Chicken Breast
     * Rice
     * Eggs
     */
    val foodName: String,

    /*
     * Nutrition values for the amount eaten.
     */
    val calories: Double,

    val proteinGrams: Double,

    val carbsGrams: Double,

    val fatGrams: Double,

    /*
     * When this entry was actually created.
     */
    val createdAt: Long
)