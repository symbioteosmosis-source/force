package com.ngedo.force.data.local.repository

import com.ngedo.force.data.local.dao.WorkoutExerciseDao
import com.ngedo.force.data.local.dao.WorkoutSessionDao
import com.ngedo.force.data.local.dao.WorkoutSetDao
import com.ngedo.force.data.local.entity.WorkoutExerciseEntity
import com.ngedo.force.data.local.entity.WorkoutSessionEntity
import com.ngedo.force.data.local.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow
import com.ngedo.force.data.local.model.ExercisePersonalRecord
import com.ngedo.force.data.local.model.PersonalRecordResult
import com.ngedo.force.data.local.model.ExerciseHistoricalSet

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

    suspend fun getPersonalRecords():
            List<ExercisePersonalRecord> {

        val weightRecords =
            workoutSetDao.getHighestWeightRecords()

        val repRecords =
            workoutSetDao.getHighestRepRecords()

        /*
         * Collect every exercise that has either
         * a weight record or a rep record.
         */
        val exerciseNames =
            (
                    weightRecords.map {
                        it.exerciseName
                    } +
                            repRecords.map {
                                it.exerciseName
                            }
                    )
                .distinct()
                .sorted()

        /*
         * Merge the two real historical sets into
         * one Personal Record object per exercise.
         */
        return exerciseNames.map { exerciseName ->

            val weightRecord =
                weightRecords.firstOrNull {
                    it.exerciseName == exerciseName
                }

            val repRecord =
                repRecords.firstOrNull {
                    it.exerciseName == exerciseName
                }

            ExercisePersonalRecord(
                exerciseName = exerciseName,

                highestWeight =
                    weightRecord?.highestWeight,

                repsAtHighestWeight =
                    weightRecord?.repsAtHighestWeight,

                highestReps =
                    repRecord?.highestReps,

                weightAtHighestReps =
                    repRecord?.weightAtHighestReps
            )
        }
    }
    suspend fun checkPersonalRecord(
        exerciseName: String,
        newWeight: Double,
        newReps: Int
    ): PersonalRecordResult {

        val previousBestWeightSet =
            workoutSetDao.getPreviousHighestWeightSet(
                exerciseName = exerciseName
            )

        val previousBestRepSet =
            workoutSetDao.getPreviousHighestRepSet(
                exerciseName = exerciseName
            )

        val isNewWeightRecord =
            when {

                newWeight <= 0.0 ->
                    false

                previousBestWeightSet == null ->
                    true

                newWeight >
                        previousBestWeightSet.weight ->
                    true

                newWeight ==
                        previousBestWeightSet.weight &&
                        newReps >
                        previousBestWeightSet.reps ->
                    true

                else ->
                    false
            }

        val isNewRepRecord =
            when {

                newReps <= 0 ->
                    false

                previousBestRepSet == null ->
                    true

                newReps >
                        previousBestRepSet.reps ->
                    true

                newReps ==
                        previousBestRepSet.reps &&
                        newWeight >
                        previousBestRepSet.weight ->
                    true

                else ->
                    false
            }

        return PersonalRecordResult(
            isNewWeightRecord = isNewWeightRecord,
            isNewRepRecord = isNewRepRecord,

            previousHighestWeight =
                previousBestWeightSet?.weight,

            previousRepsAtHighestWeight =
                previousBestWeightSet?.reps,

            previousHighestReps =
                previousBestRepSet?.reps,

            previousWeightAtHighestReps =
                previousBestRepSet?.weight
        )
    }

    suspend fun getPreviousBestSetWithDate(
        exerciseName: String,
        currentExerciseId: Long
    ): ExerciseHistoricalSet? {

        return workoutSetDao.getPreviousBestSetWithDate(
            exerciseName = exerciseName,
            currentExerciseId = currentExerciseId
        )
    }

    suspend fun getMostRecentPerformance(
        exerciseName: String,
        currentExerciseId: Long
    ): ExerciseHistoricalSet? {

        return workoutSetDao.getMostRecentPerformance(
            exerciseName = exerciseName,
            currentExerciseId = currentExerciseId
        )
    }

}