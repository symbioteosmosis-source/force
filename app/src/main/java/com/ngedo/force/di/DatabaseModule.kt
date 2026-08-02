package com.ngedo.force.di

import android.content.Context
import androidx.room.Room
import com.ngedo.force.data.local.dao.WorkoutDao
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
}