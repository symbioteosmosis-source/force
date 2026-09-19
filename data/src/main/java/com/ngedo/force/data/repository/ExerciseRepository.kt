package com.ngedo.force.data.local.repository

import com.ngedo.force.data.local.dao.ExerciseDao
import com.ngedo.force.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import com.ngedo.force.data.local.seed.ExerciseSeedData

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {

    fun observeAllExercises():
            Flow<List<ExerciseEntity>> {
        return exerciseDao.observeAllExercises()
    }

    fun searchExercises(
        query: String
    ): Flow<List<ExerciseEntity>> {
        return exerciseDao.searchExercises(query)
    }

    fun getExercisesByMuscle(
        muscle: String
    ): Flow<List<ExerciseEntity>> {
        return exerciseDao.getExercisesByMuscle(muscle)
    }

    fun getExercisesByEquipment(
        equipment: String
    ): Flow<List<ExerciseEntity>> {
        return exerciseDao.getExercisesByEquipment(equipment)
    }

    suspend fun getExerciseById(
        exerciseId: String
    ): ExerciseEntity? {
        return exerciseDao.getExerciseById(exerciseId)
    }

    suspend fun getExerciseByName(
        exerciseName: String
    ): ExerciseEntity? {
        return exerciseDao.getExerciseByName(
            exerciseName
        )
    }

    suspend fun insertExercises(
        exercises: List<ExerciseEntity>
    ) {
        exerciseDao.insertExercises(exercises)
    }

    suspend fun insertExercise(
        exercise: ExerciseEntity
    ) {
        exerciseDao.insertExercise(exercise)
    }

    suspend fun seedExercisesIfNeeded() {

        exerciseDao.insertExercises(
            ExerciseSeedData.exercises
        )
    }

    suspend fun getExerciseCount(): Int {
        return exerciseDao.getExerciseCount()
    }
}