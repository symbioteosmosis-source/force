package com.ngedo.force.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ngedo.force.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {

    @Insert
    suspend fun insertSession(
        session: WorkoutSessionEntity
    ): Long

    @Query(
        "SELECT * FROM workout_sessions " +
                "ORDER BY startedAt DESC"
    )
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query(
        "SELECT * FROM workout_sessions " +
                "WHERE id = :id"
    )
    suspend fun getSessionById(
        id: Long
    ): WorkoutSessionEntity?

    @Query(
        """
        UPDATE workout_sessions
        SET completedAt = :completedAt,
            durationSeconds = :durationSeconds,
            isCompleted = :isCompleted
        WHERE id = :sessionId
        """
    )
    suspend fun completeSession(
        sessionId: Long,
        completedAt: Long,
        durationSeconds: Long,
        isCompleted: Boolean = true
    )
}