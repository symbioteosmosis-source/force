package com.ngedo.force.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ngedo.force.data.local.dao.WorkoutDao
import com.ngedo.force.data.local.entity.WorkoutEntity

@Database(
    entities = [WorkoutEntity::class],
    version = 1,
    exportSchema = true
)
abstract class ForceDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
}