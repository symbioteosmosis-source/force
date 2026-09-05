package com.ngedo.force.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ngedo.force.data.local.entity.NutritionEntryEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Update
import com.ngedo.force.data.local.entity.NutritionGoalEntity
import androidx.room.OnConflictStrategy
import com.ngedo.force.data.local.entity.SavedFoodEntity

@Dao
interface NutritionDao {



    /*
     * -------------------------------------------------
     * ADD FOOD ENTRY
     * -------------------------------------------------
     */

    @Insert
    suspend fun insertEntry(
        entry: NutritionEntryEntity
    ): Long

    @Update
    suspend fun updateEntry(
        entry: NutritionEntryEntity
    )

    /*
     * -------------------------------------------------
     * DELETE FOOD ENTRY
     * -------------------------------------------------
     */

    @Delete
    suspend fun deleteEntry(
        entry: NutritionEntryEntity
    )


    /*
     * -------------------------------------------------
     * GET ALL ENTRIES FOR ONE DAY
     * -------------------------------------------------
     *
     * startOfDay:
     * 00:00:00
     *
     * endOfDay:
     * 23:59:59
     */

    @Query(
        """
        SELECT *
        FROM nutrition_entries
        WHERE date >= :startOfDay
          AND date < :endOfDay
        ORDER BY createdAt ASC
        """
    )
    fun getEntriesForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<List<NutritionEntryEntity>>


    /*
     * -------------------------------------------------
     * GET ENTRIES FOR A MEAL
     * -------------------------------------------------
     */

    @Query(
        """
        SELECT *
        FROM nutrition_entries
        WHERE date >= :startOfDay
          AND date < :endOfDay
          AND mealType = :mealType
        ORDER BY createdAt ASC
        """
    )
    fun getEntriesForMeal(
        startOfDay: Long,
        endOfDay: Long,
        mealType: String
    ): Flow<List<NutritionEntryEntity>>


    /*
     * -------------------------------------------------
     * DAILY CALORIES
     * -------------------------------------------------
     */

    @Query(
        """
        SELECT COALESCE(SUM(calories), 0)
        FROM nutrition_entries
        WHERE date >= :startOfDay
          AND date < :endOfDay
        """
    )
    fun getCaloriesForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<Double>


    /*
     * -------------------------------------------------
     * DAILY PROTEIN
     * -------------------------------------------------
     */

    @Query(
        """
        SELECT COALESCE(SUM(proteinGrams), 0)
        FROM nutrition_entries
        WHERE date >= :startOfDay
          AND date < :endOfDay
        """
    )
    fun getProteinForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<Double>


    /*
     * -------------------------------------------------
     * DAILY CARBS
     * -------------------------------------------------
     */

    @Query(
        """
        SELECT COALESCE(SUM(carbsGrams), 0)
        FROM nutrition_entries
        WHERE date >= :startOfDay
          AND date < :endOfDay
        """
    )
    fun getCarbsForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<Double>


    /*
     * -------------------------------------------------
     * DAILY FAT
     * -------------------------------------------------
     */

    @Query(
        """
        SELECT COALESCE(SUM(fatGrams), 0)
        FROM nutrition_entries
        WHERE date >= :startOfDay
          AND date < :endOfDay
        """
    )
    fun getFatForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<Double>

    /*
 * -------------------------------------------------
 * SAVE / UPDATE NUTRITION GOAL
 * -------------------------------------------------
 */

    @Insert(
        onConflict = androidx.room.OnConflictStrategy.REPLACE
    )
    suspend fun saveNutritionGoal(
        goal: NutritionGoalEntity
    )


    /*
     * -------------------------------------------------
     * GET ACTIVE NUTRITION GOAL
     * -------------------------------------------------
     */

    @Query(
        """
    SELECT *
    FROM nutrition_goals
    WHERE id = 1
    LIMIT 1
    """
    )
    fun getNutritionGoal():
            Flow<NutritionGoalEntity?>


    /*
 * -------------------------------------------------
 * SAVED / REMEMBERED FOODS
 * -------------------------------------------------
 */

    /*
     * Save a new food or replace the existing food
     * with the same normalized name.
     *
     * Example:
     *
     * "Chicken Breast"
     * "chicken breast"
     *
     * both resolve to the same remembered food.
     */
    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun saveFood(
        food: SavedFoodEntity
    ): Long


    /*
     * Find an exact remembered food.
     *
     * Used before saving so FORCE can preserve/update
     * an existing remembered food.
     */
    @Query(
        """
    SELECT *
    FROM saved_foods
    WHERE normalizedName = :normalizedName
    LIMIT 1
    """
    )
    suspend fun getSavedFoodByName(
        normalizedName: String
    ): SavedFoodEntity?


    /*
     * Search remembered foods while the user types.
     *
     * Example:
     *
     * User types "chi"
     *
     * Chicken Breast
     * Chicken Wrap
     * Chicken Salad
     *
     * Recently updated/used foods appear first.
     */
    @Query(
        """
    SELECT *
    FROM saved_foods
    WHERE normalizedName LIKE '%' || :query || '%'
    ORDER BY updatedAt DESC
    LIMIT 8
    """
    )
    fun searchSavedFoods(
        query: String
    ): Flow<List<SavedFoodEntity>>


    /*
     * Show recently remembered foods when the food
     * field is empty.
     *
     * This will make frequently/recently used foods
     * accessible without typing.
     */
    @Query(
        """
    SELECT *
    FROM saved_foods
    ORDER BY updatedAt DESC
    LIMIT 8
    """
    )
    fun getRecentSavedFoods():
            Flow<List<SavedFoodEntity>>
}