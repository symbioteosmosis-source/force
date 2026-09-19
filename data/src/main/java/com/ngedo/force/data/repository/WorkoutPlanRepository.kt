package com.ngedo.force.data.repository

import com.ngedo.force.data.local.dao.WorkoutPlanDao
import com.ngedo.force.data.local.entity.PlannedExerciseEntity
import com.ngedo.force.data.local.entity.PlannedWorkoutEntity
import com.ngedo.force.data.local.entity.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutPlanRepository @Inject constructor(
    private val workoutPlanDao: WorkoutPlanDao
) {

    fun observeActiveWorkoutPlan():
            Flow<WorkoutPlanEntity?> {

        return workoutPlanDao
            .observeActiveWorkoutPlan()
    }

    suspend fun getActiveWorkoutPlan():
            WorkoutPlanEntity? {

        return workoutPlanDao
            .getActiveWorkoutPlan()
    }

    suspend fun createWorkoutPlan(
        plan: WorkoutPlanEntity
    ): Long {

        workoutPlanDao.deactivateAllPlans()

        return workoutPlanDao
            .insertWorkoutPlan(plan)
    }

    suspend fun deleteWorkoutPlan(
        planId: Long
    ) {
        workoutPlanDao
            .deleteWorkoutPlan(planId)
    }


    // -------------------------
    // PLANNED WORKOUTS
    // -------------------------

    suspend fun addPlannedWorkout(
        workout: PlannedWorkoutEntity
    ): Long {

        return workoutPlanDao
            .insertPlannedWorkout(workout)
    }

    fun observePlannedWorkouts(
        planId: Long
    ): Flow<List<PlannedWorkoutEntity>> {

        return workoutPlanDao
            .observePlannedWorkouts(planId)
    }

    suspend fun getPlannedWorkout(
        plannedWorkoutId: Long
    ): PlannedWorkoutEntity? {

        return workoutPlanDao
            .getPlannedWorkout(
                plannedWorkoutId
            )
    }

    suspend fun getWorkoutForDate(
        planId: Long,
        dayStart: Long,
        dayEnd: Long
    ): PlannedWorkoutEntity? {

        return workoutPlanDao
            .getWorkoutForDate(
                planId = planId,
                dayStart = dayStart,
                dayEnd = dayEnd
            )
    }

    suspend fun markWorkoutCompleted(
        plannedWorkoutId: Long,
        sessionId: Long
    ) {
        workoutPlanDao
            .markWorkoutCompleted(
                plannedWorkoutId = plannedWorkoutId,
                sessionId = sessionId
            )
    }


    // -------------------------
    // PLANNED EXERCISES
    // -------------------------

    suspend fun addPlannedExercise(
        exercise: PlannedExerciseEntity
    ): Long {

        return workoutPlanDao
            .insertPlannedExercise(exercise)
    }

    suspend fun addPlannedExercises(
        exercises: List<PlannedExerciseEntity>
    ) {
        workoutPlanDao
            .insertPlannedExercises(exercises)
    }

    fun observePlannedExercises(
        plannedWorkoutId: Long
    ): Flow<List<PlannedExerciseEntity>> {

        return workoutPlanDao
            .observePlannedExercises(
                plannedWorkoutId
            )
    }

    suspend fun getPlannedExercises(
        plannedWorkoutId: Long
    ): List<PlannedExerciseEntity> {

        return workoutPlanDao
            .getPlannedExercises(
                plannedWorkoutId
            )
    }

    suspend fun replacePlannedExercises(
        plannedWorkoutId: Long,
        exercises: List<PlannedExerciseEntity>
    ) {

        workoutPlanDao
            .deletePlannedExercises(
                plannedWorkoutId
            )

        workoutPlanDao
            .insertPlannedExercises(
                exercises
            )
    }
}