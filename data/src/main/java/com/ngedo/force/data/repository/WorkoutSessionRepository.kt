package com.ngedo.force.data.local.repository

import com.ngedo.force.data.local.dao.WorkoutExerciseDao
import com.ngedo.force.data.local.dao.WorkoutSessionDao
import com.ngedo.force.data.local.dao.WorkoutSetDao
import com.ngedo.force.data.local.entity.WorkoutExerciseEntity
import com.ngedo.force.data.local.entity.WorkoutSessionEntity
import com.ngedo.force.data.local.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

class WorkoutSessionRepository(
    private val workoutSessionDao: WorkoutSessionDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutSetDao: WorkoutSetDao
) {

    suspend fun startSession(
        workoutId: Long,
        startedAt: Long
    ): Long {

        return workoutSessionDao.insertSession(
            WorkoutSessionEntity(
                workoutId = workoutId,
                startedAt = startedAt
            )
        )
    }

    suspend fun addExercise(
        sessionId: Long,
        exerciseName: String,
        exerciseOrder: Int
    ): Long {

        return workoutExerciseDao.insertExercise(
            WorkoutExerciseEntity(
                sessionId = sessionId,
                exerciseName = exerciseName,
                exerciseOrder = exerciseOrder
            )
        )
    }

    suspend fun addSet(
        workoutExerciseId: Long,
        setNumber: Int,
        reps: Int,
        weight: Double
    ): Long {

        return workoutSetDao.insertSet(
            WorkoutSetEntity(
                workoutExerciseId = workoutExerciseId,
                setNumber = setNumber,
                reps = reps,
                weight = weight
            )
        )
    }

    suspend fun completeSession(
        sessionId: Long,
        completedAt: Long,
        durationSeconds: Long
    ) {

        workoutSessionDao.completeSession(
            sessionId = sessionId,
            completedAt = completedAt,
            durationSeconds = durationSeconds
        )
    }

    fun getAllSessions(): Flow<List<WorkoutSessionEntity>> {
        return workoutSessionDao.getAllSessions()
    }

    fun getExercisesForSession(
        sessionId: Long
    ): Flow<List<WorkoutExerciseEntity>> {

        return workoutExerciseDao.getExercisesForSession(
            sessionId
        )
    }

    fun getSetsForExercise(
        workoutExerciseId: Long
    ): Flow<List<WorkoutSetEntity>> {

        return workoutSetDao.getSetsForExercise(
            workoutExerciseId
        )
    }
    suspend fun getSetsForExerciseOnce(
        workoutExerciseId: Long
    ): List<WorkoutSetEntity> {

        return workoutSetDao.getSetsForExerciseOnce(
            workoutExerciseId
        )
    }
    suspend fun getPreviousBestSet(
        exerciseName: String,
        currentExerciseId: Long
    ): WorkoutSetEntity? {

        return workoutSetDao.getPreviousBestSet(
            exerciseName = exerciseName,
            currentExerciseId = currentExerciseId
        )
    }
    suspend fun completeExercise(
        exerciseId: Long
    ) {
        workoutExerciseDao.markExerciseCompleted(
            exerciseId
        )
    }
    suspend fun getPreviousBestWeight(
        exerciseName: String
    ): Double? {

        return workoutSetDao.getPreviousBestWeight(
            exerciseName
        )
    }

    suspend fun getPreviousBestReps(
        exerciseName: String
    ): Int? {

        return workoutSetDao.getPreviousBestReps(
            exerciseName
        )
    }
}