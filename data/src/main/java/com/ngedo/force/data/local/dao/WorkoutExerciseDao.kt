package com.ngedo.force.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ngedo.force.data.local.entity.WorkoutExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutExerciseDao {

    @Insert
    suspend fun insertExercise(
        exercise: WorkoutExerciseEntity
    ): Long

    @Query(
        """
        SELECT * FROM workout_exercises
        WHERE sessionId = :sessionId
        ORDER BY exerciseOrder ASC
        """
    )
    fun getExercisesForSession(
        sessionId: Long
    ): Flow<List<WorkoutExerciseEntity>>

    @Query(
        """
        SELECT * FROM workout_exercises
        WHERE id = :id
        """
    )
    suspend fun getExerciseById(
        id: Long
    ): WorkoutExerciseEntity?

    @Query(
        """
        UPDATE workout_exercises
        SET completed = :completed
        WHERE id = :exerciseId
        """
    )
    suspend fun updateCompleted(
        exerciseId: Long,
        completed: Boolean
    )
    @Query(
        """
    UPDATE workout_exercises
    SET completed = 1
    WHERE id = :exerciseId
    """
    )
    suspend fun markExerciseCompleted(
        exerciseId: Long
    )

    @Query(
        """
    DELETE FROM workout_exercises
    WHERE sessionId = :sessionId
    """
    )
    suspend fun deleteExercisesForSession(
        sessionId: Long
    )
}