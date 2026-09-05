package com.ngedo.force.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ngedo.force.data.local.entity.WorkoutSetEntity
import com.ngedo.force.data.local.model.ExercisePersonalRecord
import kotlinx.coroutines.flow.Flow
import com.ngedo.force.data.local.model.ExerciseHistoricalSet

@Dao
interface WorkoutSetDao {

    /*
     * =========================================================
     * INSERT SET
     * =========================================================
     */

    @Insert
    suspend fun insertSet(
        set: WorkoutSetEntity
    ): Long


    /*
     * =========================================================
     * GET SETS FOR CURRENT EXERCISE
     * =========================================================
     */

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


    /*
     * =========================================================
     * GET SET BY ID
     * =========================================================
     */

    @Query(
        """
        SELECT * FROM workout_sets
        WHERE id = :id
        """
    )
    suspend fun getSetById(
        id: Long
    ): WorkoutSetEntity?


    /*
     * =========================================================
     * GET SETS ONCE
     * =========================================================
     *
     * Used by workout history/details.
     */

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


    /*
     * =========================================================
     * PREVIOUS BEST WEIGHT
     * =========================================================
     *
     * Returns only the highest historical weight value.
     *
     * Kept because it is already used by the existing
     * Previous Best feature.
     */

    @Query(
        """
        SELECT MAX(weight)
        FROM workout_sets ws
        INNER JOIN workout_exercises we
            ON ws.workoutExerciseId = we.id
        WHERE we.exerciseName = :exerciseName
        """
    )
    suspend fun getPreviousBestWeight(
        exerciseName: String
    ): Double?


    /*
     * =========================================================
     * PREVIOUS BEST REPS
     * =========================================================
     */

    @Query(
        """
        SELECT MAX(reps)
        FROM workout_sets ws
        INNER JOIN workout_exercises we
            ON ws.workoutExerciseId = we.id
        WHERE we.exerciseName = :exerciseName
        """
    )
    suspend fun getPreviousBestReps(
        exerciseName: String
    ): Int?


    /*
     * =========================================================
     * PREVIOUS BEST SET
     * =========================================================
     *
     * Used when an exercise STARTS.
     *
     * IMPORTANT:
     * The current exercise ID IS excluded here because
     * "Previous Best" should refer to performance before
     * the current exercise session.
     */

    @Query(
        """
        SELECT ws.*
        FROM workout_sets ws
        INNER JOIN workout_exercises we
            ON ws.workoutExerciseId = we.id
        WHERE we.exerciseName = :exerciseName
          AND we.id != :currentExerciseId
          AND ws.weight > 0
        ORDER BY
            ws.weight DESC,
            ws.reps DESC
        LIMIT 1
        """
    )
    suspend fun getPreviousBestSet(
        exerciseName: String,
        currentExerciseId: Long
    ): WorkoutSetEntity?


    /*
     * =========================================================
     * PERSONAL RECORDS — HEAVIEST SET
     * =========================================================
     *
     * Returns the actual historical SET that produced
     * the highest weight for every exercise.
     *
     * If two sets have the same weight,
     * the one with more reps wins.
     */

    @Query(
        """
        SELECT
            we.exerciseName AS exerciseName,
            ws.weight AS highestWeight,
            ws.reps AS repsAtHighestWeight,
            NULL AS highestReps,
            NULL AS weightAtHighestReps
        FROM workout_sets ws
        INNER JOIN workout_exercises we
            ON ws.workoutExerciseId = we.id
        WHERE ws.id = (
            SELECT ws2.id
            FROM workout_sets ws2
            INNER JOIN workout_exercises we2
                ON ws2.workoutExerciseId = we2.id
            WHERE we2.exerciseName = we.exerciseName
              AND ws2.weight > 0
            ORDER BY
                ws2.weight DESC,
                ws2.reps DESC
            LIMIT 1
        )
        GROUP BY we.exerciseName
        ORDER BY we.exerciseName ASC
        """
    )
    suspend fun getHighestWeightRecords():
            List<ExercisePersonalRecord>


    /*
     * =========================================================
     * PERSONAL RECORDS — REP RECORD
     * =========================================================
     *
     * Returns the actual historical SET that produced
     * the highest reps for every exercise.
     *
     * If two sets have the same reps,
     * the heavier set wins.
     */

    @Query(
        """
        SELECT
            we.exerciseName AS exerciseName,
            NULL AS highestWeight,
            NULL AS repsAtHighestWeight,
            ws.reps AS highestReps,
            ws.weight AS weightAtHighestReps
        FROM workout_sets ws
        INNER JOIN workout_exercises we
            ON ws.workoutExerciseId = we.id
        WHERE ws.id = (
            SELECT ws2.id
            FROM workout_sets ws2
            INNER JOIN workout_exercises we2
                ON ws2.workoutExerciseId = we2.id
            WHERE we2.exerciseName = we.exerciseName
              AND ws2.reps > 0
            ORDER BY
                ws2.reps DESC,
                ws2.weight DESC
            LIMIT 1
        )
        GROUP BY we.exerciseName
        ORDER BY we.exerciseName ASC
        """
    )
    suspend fun getHighestRepRecords():
            List<ExercisePersonalRecord>


    /*
     * =========================================================
     * LIVE PR CHECK — HEAVIEST SET
     * =========================================================
     *
     * IMPORTANT:
     * We DO NOT exclude the current exercise here.
     *
     * checkPersonalRecord() runs BEFORE the new set is inserted,
     * therefore previously completed sets from TODAY must be
     * included.
     *
     * Example:
     *
     * Historical PR = 60 kg
     * Today's Set 1 = 70 kg  -> PR
     * Today's Set 2 = 65 kg
     *
     * Set 2 must compare against 70 kg, not 60 kg.
     */

    @Query(
        """
        SELECT ws.*
        FROM workout_sets ws
        INNER JOIN workout_exercises we
            ON ws.workoutExerciseId = we.id
        WHERE we.exerciseName = :exerciseName
          AND ws.weight > 0
        ORDER BY
            ws.weight DESC,
            ws.reps DESC
        LIMIT 1
        """
    )
    suspend fun getPreviousHighestWeightSet(
        exerciseName: String
    ): WorkoutSetEntity?


    /*
     * =========================================================
     * LIVE PR CHECK — REP RECORD
     * =========================================================
     *
     * Includes previously completed sets from the
     * current workout.
     *
     * Highest reps wins.
     * If reps tie, the heavier weight wins.
     */

    @Query(
        """
        SELECT ws.*
        FROM workout_sets ws
        INNER JOIN workout_exercises we
            ON ws.workoutExerciseId = we.id
        WHERE we.exerciseName = :exerciseName
          AND ws.reps > 0
        ORDER BY
            ws.reps DESC,
            ws.weight DESC
        LIMIT 1
        """
    )
    suspend fun getPreviousHighestRepSet(
        exerciseName: String
    ): WorkoutSetEntity?


    /*
 * =========================================================
 * PREVIOUS BEST SET WITH DATE
 * =========================================================
 *
 * Returns the all-time best historical set BEFORE
 * the current exercise, including when it happened.
 */
    @Query(
        """
    SELECT
        ws.weight AS weight,
        ws.reps AS reps,
        s.startedAt AS performedAt
    FROM workout_sets ws
    INNER JOIN workout_exercises we
        ON ws.workoutExerciseId = we.id
    INNER JOIN workout_sessions s
        ON we.sessionId = s.id
    WHERE we.exerciseName = :exerciseName
      AND we.id != :currentExerciseId
      AND ws.weight > 0
      AND ws.reps > 0
    ORDER BY
        ws.weight DESC,
        ws.reps DESC,
        s.startedAt DESC
    LIMIT 1
    """
    )
    suspend fun getPreviousBestSetWithDate(
        exerciseName: String,
        currentExerciseId: Long
    ): ExerciseHistoricalSet?

    /*
 * =========================================================
 * MOST RECENT PERFORMANCE
 * =========================================================
 *
 * Returns the latest valid historical working set
 * from the most recently trained session.
 *
 * Current exercise is excluded so today's sets
 * cannot affect return-after-break calculations.
 */
    @Query(
        """
    SELECT
        ws.weight AS weight,
        ws.reps AS reps,
        s.startedAt AS performedAt
    FROM workout_sets ws
    INNER JOIN workout_exercises we
        ON ws.workoutExerciseId = we.id
    INNER JOIN workout_sessions s
        ON we.sessionId = s.id
    WHERE we.exerciseName = :exerciseName
      AND we.id != :currentExerciseId
      AND ws.weight > 0
      AND ws.reps > 0
    ORDER BY
        s.startedAt DESC,
        ws.setNumber DESC
    LIMIT 1
    """
    )
    suspend fun getMostRecentPerformance(
        exerciseName: String,
        currentExerciseId: Long
    ): ExerciseHistoricalSet?
}