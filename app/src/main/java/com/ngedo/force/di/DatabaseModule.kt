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
import com.ngedo.force.data.local.dao.UserProfileDao
import com.ngedo.force.data.local.repository.UserProfileRepository


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

    private val MIGRATION_4_5 =
        object : Migration(4, 5) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                CREATE TABLE IF NOT EXISTS user_profile (
                    id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    weightKg REAL NOT NULL,
                    heightCm REAL NOT NULL,
                    trainingGoal TEXT NOT NULL,
                    preferredUnits TEXT NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
                )
            }
        }

    private val MIGRATION_5_6 =
        object : Migration(5, 6) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                ALTER TABLE user_profile
                ADD COLUMN age INTEGER NOT NULL DEFAULT 18
                """.trimIndent()
                )
            }
        }

    private val MIGRATION_6_7 =
        object : Migration(6, 7) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                ALTER TABLE user_profile
                ADD COLUMN sex TEXT NOT NULL DEFAULT 'Male'
                """.trimIndent()
                )
            }
        }

    private val MIGRATION_7_8 =
        object : Migration(7, 8) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                ALTER TABLE user_profile
                ADD COLUMN activityLevel TEXT NOT NULL DEFAULT 'Moderately Active'
                """.trimIndent()
                )
            }
        }

    private val MIGRATION_8_9 =
        object : Migration(8, 9) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                CREATE TABLE IF NOT EXISTS saved_foods (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    foodName TEXT NOT NULL,
                    normalizedName TEXT NOT NULL,
                    calories REAL NOT NULL,
                    proteinGrams REAL NOT NULL,
                    carbsGrams REAL NOT NULL,
                    fatGrams REAL NOT NULL,
                    updatedAt INTEGER NOT NULL
                )
                """.trimIndent()
                )

                database.execSQL(
                    """
                CREATE UNIQUE INDEX IF NOT EXISTS
                index_saved_foods_normalizedName
                ON saved_foods(normalizedName)
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
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
                MIGRATION_8_9
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideUserProfileRepository(
        userProfileDao: UserProfileDao
    ): UserProfileRepository {

        return UserProfileRepository(
            userProfileDao = userProfileDao
        )
    }

    @Provides
    fun provideUserProfileDao(
        database: ForceDatabase
    ): UserProfileDao {
        return database.userProfileDao()
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