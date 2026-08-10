package com.ngedo.force.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ngedo.force.data.local.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSetDao {

    @Insert
    suspend fun insertSet(
        set: WorkoutSetEntity
    ): Long

    @Query(
        """
        SELECT * FROM workout_sets
        WHERE workoutExerciseId = :workoutExerciseId
        ORDER BY setNumber ASC
        """
    )
    fun getSetsForExercise(
        workoutExerciseId: Long
    ): Flow<List<WorkoutSetEntity>>

    @Query(
        """
        SELECT * FROM workout_sets
        WHERE id = :id
        """
    )
    suspend fun getSetById(
        id: Long
    ): WorkoutSetEntity?

    @Query(
        """
        SELECT * FROM workout_sets
        WHERE workoutExerciseId = :workoutExerciseId
        ORDER BY setNumber ASC
        """
    )
    suspend fun getSetsForExerciseOnce(
        workoutExerciseId: Long
    ): List<WorkoutSetEntity>
}