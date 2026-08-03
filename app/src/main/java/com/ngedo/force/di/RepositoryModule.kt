package com.ngedo.force.di

import com.ngedo.force.data.repository.WorkoutRepository
import com.ngedo.force.data.repository.WorkoutRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        repository: WorkoutRepositoryImpl
    ): WorkoutRepository
}