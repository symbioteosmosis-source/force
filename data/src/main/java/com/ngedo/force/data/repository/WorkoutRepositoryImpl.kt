package com.ngedo.force.data.repository

import com.ngedo.force.data.local.dao.WorkoutDao
import com.ngedo.force.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao
) : WorkoutRepository {

    override fun getAllWorkouts(): Flow<List<WorkoutEntity>> {
        return workoutDao.getAllWorkouts()
    }

    override suspend fun getWorkoutById(id: Long): WorkoutEntity? {
        return workoutDao.getWorkoutById(id)
    }

    override suspend fun insertWorkout(workout: WorkoutEntity) {
        workoutDao.insertWorkout(workout)
    }

    override suspend fun updateWorkout(workout: WorkoutEntity) {
        workoutDao.updateWorkout(workout)
    }

    override suspend fun deleteWorkout(workout: WorkoutEntity) {
        workoutDao.deleteWorkout(workout)
    }
}