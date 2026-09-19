package com.ngedo.force.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ngedo.force.data.local.entity.FavoriteExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteExerciseDao {

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun addFavorite(
        favorite: FavoriteExerciseEntity
    )

    @Delete
    suspend fun removeFavorite(
        favorite: FavoriteExerciseEntity
    )

    @Query(
        """
        SELECT *
        FROM favorite_exercises
        ORDER BY createdAt DESC
        """
    )
    fun observeFavorites():
            Flow<List<FavoriteExerciseEntity>>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM favorite_exercises
            WHERE exerciseName = :exerciseName
        )
        """
    )
    suspend fun isFavorite(
        exerciseName: String
    ): Boolean
}