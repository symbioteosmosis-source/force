package com.ngedo.force.data.repository

import com.ngedo.force.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {

    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    suspend fun getWorkoutById(id: Long): WorkoutEntity?

    suspend fun insertWorkout(workout: WorkoutEntity)

    suspend fun updateWorkout(workout: WorkoutEntity)

    suspend fun deleteWorkout(workout: WorkoutEntity)
}