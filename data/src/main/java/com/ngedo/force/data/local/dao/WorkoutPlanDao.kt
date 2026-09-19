package com.ngedo.force.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ngedo.force.data.local.entity.PlannedExerciseEntity
import com.ngedo.force.data.local.entity.PlannedWorkoutEntity
import com.ngedo.force.data.local.entity.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {

    // -------------------------
    // WORKOUT PLAN
    // -------------------------

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertWorkoutPlan(
        plan: WorkoutPlanEntity
    ): Long

    @Query(
        """
        SELECT * FROM workout_plans
        WHERE isActive = 1
        ORDER BY createdAt DESC
        LIMIT 1
        """
    )
    fun observeActiveWorkoutPlan():
            Flow<WorkoutPlanEntity?>

    @Query(
        """
        SELECT * FROM workout_plans
        WHERE isActive = 1
        ORDER BY createdAt DESC
        LIMIT 1
        """
    )
    suspend fun getActiveWorkoutPlan():
            WorkoutPlanEntity?

    @Query(
        """
        UPDATE workout_plans
        SET isActive = 0
        """
    )
    suspend fun deactivateAllPlans()

    @Query(
        """
        DELETE FROM workout_plans
        WHERE id = :planId
        """
    )
    suspend fun deleteWorkoutPlan(
        planId: Long
    )


    // -------------------------
    // PLANNED WORKOUT
    // -------------------------

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertPlannedWorkout(
        workout: PlannedWorkoutEntity
    ): Long

    @Query(
        """
        SELECT * FROM planned_workouts
        WHERE planId = :planId
        ORDER BY scheduledDate ASC
        """
    )
    fun observePlannedWorkouts(
        planId: Long
    ): Flow<List<PlannedWorkoutEntity>>

    @Query(
        """
        SELECT * FROM planned_workouts
        WHERE id = :plannedWorkoutId
        LIMIT 1
        """
    )
    suspend fun getPlannedWorkout(
        plannedWorkoutId: Long
    ): PlannedWorkoutEntity?

    @Query(
        """
        SELECT * FROM planned_workouts
        WHERE planId = :planId
        AND scheduledDate >= :dayStart
        AND scheduledDate < :dayEnd
        ORDER BY scheduledDate ASC
        LIMIT 1
        """
    )
    suspend fun getWorkoutForDate(
        planId: Long,
        dayStart: Long,
        dayEnd: Long
    ): PlannedWorkoutEntity?

    @Query(
        """
        UPDATE planned_workouts
        SET isCompleted = 1,
            completedSessionId = :sessionId
        WHERE id = :plannedWorkoutId
        """
    )
    suspend fun markWorkoutCompleted(
        plannedWorkoutId: Long,
        sessionId: Long
    )


    // -------------------------
    // PLANNED EXERCISES
    // -------------------------

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertPlannedExercise(
        exercise: PlannedExerciseEntity
    ): Long

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertPlannedExercises(
        exercises: List<PlannedExerciseEntity>
    )

    @Query(
        """
        SELECT * FROM planned_exercises
        WHERE plannedWorkoutId = :plannedWorkoutId
        ORDER BY exerciseOrder ASC
        """
    )
    fun observePlannedExercises(
        plannedWorkoutId: Long
    ): Flow<List<PlannedExerciseEntity>>

    @Query(
        """
        SELECT * FROM planned_exercises
        WHERE plannedWorkoutId = :plannedWorkoutId
        ORDER BY exerciseOrder ASC
        """
    )
    suspend fun getPlannedExercises(
        plannedWorkoutId: Long
    ): List<PlannedExerciseEntity>

    @Query(
        """
        DELETE FROM planned_exercises
        WHERE plannedWorkoutId = :plannedWorkoutId
        """
    )
    suspend fun deletePlannedExercises(
        plannedWorkoutId: Long
    )
}