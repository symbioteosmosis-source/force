package com.ngedo.force.data.local.repository

import com.ngedo.force.data.local.dao.UserProfileDao
import com.ngedo.force.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class UserProfileRepository(
    private val userProfileDao: UserProfileDao
) {

    fun getProfile():
            Flow<UserProfileEntity?> {

        return userProfileDao.getProfile()
    }

    suspend fun saveProfile(
        name: String,
        age: Int,
        sex: String,
        activityLevel: String,
        weightKg: Double,
        heightCm: Double,
        trainingGoal: String,
        preferredUnits: String
    ) {

        userProfileDao.saveProfile(
            UserProfileEntity(
                id = 1,
                name = name,
                age = age,
                sex = sex,
                activityLevel = activityLevel,
                weightKg = weightKg,
                heightCm = heightCm,
                trainingGoal = trainingGoal,
                preferredUnits = preferredUnits,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}