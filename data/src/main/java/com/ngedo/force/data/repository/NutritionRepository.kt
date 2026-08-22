package com.ngedo.force.data.local.repository

import com.ngedo.force.data.local.dao.NutritionDao
import com.ngedo.force.data.local.entity.NutritionEntryEntity
import kotlinx.coroutines.flow.Flow
import com.ngedo.force.data.local.entity.NutritionGoalEntity

class NutritionRepository(
    private val nutritionDao: NutritionDao
) {

    /*
 * -------------------------------------------------
 * NUTRITION GOALS
 * -------------------------------------------------
 */

    fun getNutritionGoal():
            Flow<NutritionGoalEntity?> {

        return nutritionDao.getNutritionGoal()
    }


    suspend fun saveNutritionGoal(
        calorieTarget: Double,
        proteinTarget: Double,
        carbsTarget: Double,
        fatTarget: Double
    ) {

        nutritionDao.saveNutritionGoal(
            NutritionGoalEntity(
                id = 1,
                calorieTarget = calorieTarget,
                proteinTarget = proteinTarget,
                carbsTarget = carbsTarget,
                fatTarget = fatTarget,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
    suspend fun addEntry(
        date: Long,
        mealType: String,
        foodName: String,
        calories: Double,
        proteinGrams: Double,
        carbsGrams: Double,
        fatGrams: Double
    ): Long {

        return nutritionDao.insertEntry(
            NutritionEntryEntity(
                date = date,
                mealType = mealType,
                foodName = foodName,
                calories = calories,
                proteinGrams = proteinGrams,
                carbsGrams = carbsGrams,
                fatGrams = fatGrams,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteEntry(
        entry: NutritionEntryEntity
    ) {
        nutritionDao.deleteEntry(
            entry
        )
    }

    fun getEntriesForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<List<NutritionEntryEntity>> {

        return nutritionDao.getEntriesForDay(
            startOfDay = startOfDay,
            endOfDay = endOfDay
        )
    }

    fun getEntriesForMeal(
        startOfDay: Long,
        endOfDay: Long,
        mealType: String
    ): Flow<List<NutritionEntryEntity>> {

        return nutritionDao.getEntriesForMeal(
            startOfDay = startOfDay,
            endOfDay = endOfDay,
            mealType = mealType
        )
    }

    fun getCaloriesForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<Double> {

        return nutritionDao.getCaloriesForDay(
            startOfDay = startOfDay,
            endOfDay = endOfDay
        )
    }

    fun getProteinForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<Double> {

        return nutritionDao.getProteinForDay(
            startOfDay = startOfDay,
            endOfDay = endOfDay
        )
    }

    fun getCarbsForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<Double> {

        return nutritionDao.getCarbsForDay(
            startOfDay = startOfDay,
            endOfDay = endOfDay
        )
    }

    fun getFatForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<Double> {

        return nutritionDao.getFatForDay(
            startOfDay = startOfDay,
            endOfDay = endOfDay
        )
    }

    suspend fun updateEntry(
        entry: NutritionEntryEntity
    ) {
        nutritionDao.updateEntry(
            entry
        )
    }
}