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
import com.ngedo.force.data.local.dao.NutritionDao
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ngedo.force.data.local.repository.NutritionRepository

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    private val MIGRATION_2_3 =
        object : Migration(2, 3) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                CREATE TABLE IF NOT EXISTS nutrition_entries (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    date INTEGER NOT NULL,
                    mealType TEXT NOT NULL,
                    foodName TEXT NOT NULL,
                    calories REAL NOT NULL,
                    proteinGrams REAL NOT NULL,
                    carbsGrams REAL NOT NULL,
                    fatGrams REAL NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
                )
            }
        }

    private val MIGRATION_3_4 =
        object : Migration(3, 4) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                CREATE TABLE IF NOT EXISTS nutrition_goals (
                    id INTEGER NOT NULL,
                    calorieTarget REAL NOT NULL,
                    proteinTarget REAL NOT NULL,
                    carbsTarget REAL NOT NULL,
                    fatTarget REAL NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
                )
            }
        }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ForceDatabase {
        return Room.databaseBuilder(
            context,
            ForceDatabase::class.java,
            "force_database"
        )
            .addMigrations(
                MIGRATION_2_3,
                MIGRATION_3_4
            )
            .build()
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
    fun provideNutritionDao(
        database: ForceDatabase
    ): NutritionDao {
        return database.nutritionDao()
    }

    @Provides
    @Singleton
    fun provideNutritionRepository(
        nutritionDao: NutritionDao
    ): NutritionRepository {

        return NutritionRepository(
            nutritionDao = nutritionDao
        )
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