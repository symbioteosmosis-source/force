package com.ngedo.force.di

import android.content.Context
import androidx.room.Room
import com.ngedo.force.data.local.dao.WorkoutDao
import com.ngedo.force.data.local.dao.WorkoutSessionDao
import com.ngedo.force.data.local.dao.WorkoutExerciseDao
import com.ngedo.force.data.local.dao.WorkoutSetDao
import com.ngedo.force.data.local.repository.WorkoutSessionRepository
import com.ngedo.force.data.local.database.ForceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ForceDatabase {
        return Room.databaseBuilder(
            context,
            ForceDatabase::class.java,
            "force_database"
        ).build()
    }

    @Provides
    fun provideWorkoutDao(
        database: ForceDatabase
    ): WorkoutDao {
        return database.workoutDao()
    }
    @Provides
    fun provideWorkoutSessionDao(
        database: ForceDatabase
    ): WorkoutSessionDao {
        return database.workoutSessionDao()
    }

    @Provides
    fun provideWorkoutExerciseDao(
        database: ForceDatabase
    ): WorkoutExerciseDao {
        return database.workoutExerciseDao()
    }

    @Provides
    fun provideWorkoutSetDao(
        database: ForceDatabase
    ): WorkoutSetDao {
        return database.workoutSetDao()
    }
    @Provides
    @Singleton
    fun provideWorkoutSessionRepository(
        workoutSessionDao: WorkoutSessionDao,
        workoutExerciseDao: WorkoutExerciseDao,
        workoutSetDao: WorkoutSetDao
    ): WorkoutSessionRepository {
        return WorkoutSessionRepository(
            workoutSessionDao = workoutSessionDao,
            workoutExerciseDao = workoutExerciseDao,
            workoutSetDao = workoutSetDao
        )
    }
}