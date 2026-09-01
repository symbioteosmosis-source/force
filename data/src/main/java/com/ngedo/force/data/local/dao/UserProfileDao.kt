package com.ngedo.force.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ngedo.force.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun saveProfile(
        profile: UserProfileEntity
    )

    @Query(
        """
        SELECT *
        FROM user_profile
        WHERE id = 1
        LIMIT 1
        """
    )
    fun getProfile():
            Flow<UserProfileEntity?>
}