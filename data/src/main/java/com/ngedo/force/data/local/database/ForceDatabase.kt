package com.ngedo.force.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ngedo.force.data.local.dao.NutritionDao
import com.ngedo.force.data.local.dao.WorkoutDao
import com.ngedo.force.data.local.dao.WorkoutExerciseDao
import com.ngedo.force.data.local.dao.WorkoutSessionDao
import com.ngedo.force.data.local.dao.WorkoutSetDao
import com.ngedo.force.data.local.entity.NutritionEntryEntity
import com.ngedo.force.data.local.entity.WorkoutEntity
import com.ngedo.force.data.local.entity.WorkoutExerciseEntity
import com.ngedo.force.data.local.entity.WorkoutSessionEntity
import com.ngedo.force.data.local.entity.WorkoutSetEntity
import com.ngedo.force.data.local.entity.NutritionGoalEntity
import com.ngedo.force.data.local.dao.UserProfileDao
import com.ngedo.force.data.local.entity.UserProfileEntity


@Database(
    entities = [
        WorkoutEntity::class,
        WorkoutSessionEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutSetEntity::class,
        NutritionEntryEntity::class,
        NutritionGoalEntity::class,
        UserProfileEntity::class

    ],
    version = 8,
    exportSchema = true
)
abstract class ForceDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    abstract fun workoutSessionDao(): WorkoutSessionDao

    abstract fun workoutExerciseDao(): WorkoutExerciseDao

    abstract fun workoutSetDao(): WorkoutSetDao

    abstract fun nutritionDao(): NutritionDao

    abstract fun userProfileDao(): UserProfileDao
}