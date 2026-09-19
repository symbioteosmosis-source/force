package com.ngedo.force.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ngedo.force.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertExercises(
        exercises: List<ExerciseEntity>
    )

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertExercise(
        exercise: ExerciseEntity
    )

    @Query(
        """
        SELECT *
        FROM exercises
        ORDER BY name ASC
        """
    )
    fun observeAllExercises():
            Flow<List<ExerciseEntity>>

    @Query(
        """
        SELECT *
        FROM exercises
        WHERE name LIKE '%' || :query || '%'
           OR primaryMuscle LIKE '%' || :query || '%'
           OR equipment LIKE '%' || :query || '%'
        ORDER BY name ASC
        """
    )
    fun searchExercises(
        query: String
    ): Flow<List<ExerciseEntity>>

    @Query(
        """
        SELECT *
        FROM exercises
        WHERE primaryMuscle = :muscle
        ORDER BY name ASC
        """
    )
    fun getExercisesByMuscle(
        muscle: String
    ): Flow<List<ExerciseEntity>>

    @Query(
        """
        SELECT *
        FROM exercises
        WHERE equipment = :equipment
        ORDER BY name ASC
        """
    )
    fun getExercisesByEquipment(
        equipment: String
    ): Flow<List<ExerciseEntity>>

    @Query(
        """
        SELECT *
        FROM exercises
        WHERE id = :exerciseId
        LIMIT 1
        """
    )
    suspend fun getExerciseById(
        exerciseId: String
    ): ExerciseEntity?

    @Query(
        """
    SELECT COUNT(*)
    FROM exercises
    """
    )
    suspend fun getExerciseCount(): Int

    @Query(
        """
    SELECT * FROM exercises
    WHERE LOWER(name) = LOWER(:exerciseName)
    LIMIT 1
    """
    )
    suspend fun getExerciseByName(
        exerciseName: String
    ): ExerciseEntity?
}