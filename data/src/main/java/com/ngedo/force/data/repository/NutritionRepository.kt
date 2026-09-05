package com.ngedo.force.data.local.repository

import com.ngedo.force.data.local.dao.NutritionDao
import com.ngedo.force.data.local.entity.NutritionEntryEntity
import kotlinx.coroutines.flow.Flow
import com.ngedo.force.data.local.entity.NutritionGoalEntity
import com.ngedo.force.data.local.entity.SavedFoodEntity

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

        val cleanFoodName =
            foodName.trim()

        val normalizedName =
            normalizeFoodName(
                cleanFoodName
            )

        val currentTime =
            System.currentTimeMillis()

        /*
         * -------------------------------------------------
         * SAVE THE DAILY MEAL ENTRY
         * -------------------------------------------------
         */

        val entryId =
            nutritionDao.insertEntry(
                NutritionEntryEntity(
                    date = date,
                    mealType = mealType,
                    foodName = cleanFoodName,
                    calories = calories,
                    proteinGrams = proteinGrams,
                    carbsGrams = carbsGrams,
                    fatGrams = fatGrams,
                    createdAt = currentTime
                )
            )

        /*
         * -------------------------------------------------
         * REMEMBER THE FOOD
         * -------------------------------------------------
         *
         * Only remember a food when it actually has a name.
         *
         * If the food already exists, preserve its database
         * ID and update its nutrition values.
         */

        if (normalizedName.isNotBlank()) {

            val existingFood =
                nutritionDao.getSavedFoodByName(
                    normalizedName = normalizedName
                )

            nutritionDao.saveFood(
                SavedFoodEntity(
                    id = existingFood?.id ?: 0,
                    foodName = cleanFoodName,
                    normalizedName = normalizedName,
                    calories = calories,
                    proteinGrams = proteinGrams,
                    carbsGrams = carbsGrams,
                    fatGrams = fatGrams,
                    updatedAt = currentTime
                )
            )
        }

        return entryId
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

    private fun normalizeFoodName(
        foodName: String
    ): String {

        return foodName
            .trim()
            .lowercase()
            .replace(
                Regex("\\s+"),
                " "
            )
    }

    fun searchSavedFoods(
        query: String
    ): Flow<List<SavedFoodEntity>> {

        return nutritionDao.searchSavedFoods(
            query = normalizeFoodName(query)
        )
    }

    fun getRecentSavedFoods():
            Flow<List<SavedFoodEntity>> {

        return nutritionDao.getRecentSavedFoods()
    }
}