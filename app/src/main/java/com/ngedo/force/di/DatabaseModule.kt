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
import com.ngedo.force.data.local.dao.FavoriteExerciseDao
import com.ngedo.force.data.local.dao.ExerciseDao
import com.ngedo.force.data.local.dao.WorkoutPlanDao
import com.ngedo.force.data.repository.WorkoutPlanRepository

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

    val MIGRATION_9_10 =
        object : Migration(9, 10) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                CREATE TABLE IF NOT EXISTS `favorite_exercises` (
                    `exerciseName` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    PRIMARY KEY(`exerciseName`)
                )
                """.trimIndent()
                )
            }
        }

    private val MIGRATION_10_11 =
        object : Migration(10, 11) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                CREATE TABLE IF NOT EXISTS `exercises` (
                    `id` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `primaryMuscle` TEXT NOT NULL,
                    `secondaryMuscles` TEXT NOT NULL,
                    `equipment` TEXT NOT NULL,
                    `movementType` TEXT NOT NULL,
                    `difficulty` TEXT NOT NULL,
                    `instructions` TEXT NOT NULL,
                    `mediaPath` TEXT,
                    `isCustom` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
                )
            }
        }

    private val MIGRATION_11_12 =
        object : Migration(11, 12) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                CREATE TABLE IF NOT EXISTS `workout_plans` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `goal` TEXT NOT NULL,
                    `startDate` INTEGER NOT NULL,
                    `endDate` INTEGER NOT NULL,
                    `isActive` INTEGER NOT NULL,
                    `createdAt` INTEGER NOT NULL
                )
                """.trimIndent()
                )

                database.execSQL(
                    """
                CREATE TABLE IF NOT EXISTS `planned_workouts` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `planId` INTEGER NOT NULL,
                    `name` TEXT NOT NULL,
                    `weekNumber` INTEGER NOT NULL,
                    `scheduledDate` INTEGER NOT NULL,
                    `isCompleted` INTEGER NOT NULL,
                    `completedSessionId` INTEGER,
                    FOREIGN KEY(`planId`)
                        REFERENCES `workout_plans`(`id`)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE
                )
                """.trimIndent()
                )

                database.execSQL(
                    """
                CREATE INDEX IF NOT EXISTS
                `index_planned_workouts_planId`
                ON `planned_workouts` (`planId`)
                """.trimIndent()
                )

                database.execSQL(
                    """
                CREATE INDEX IF NOT EXISTS
                `index_planned_workouts_scheduledDate`
                ON `planned_workouts` (`scheduledDate`)
                """.trimIndent()
                )

                database.execSQL(
                    """
                CREATE TABLE IF NOT EXISTS `planned_exercises` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `plannedWorkoutId` INTEGER NOT NULL,
                    `exerciseId` TEXT NOT NULL,
                    `exerciseName` TEXT NOT NULL,
                    `target` TEXT NOT NULL,
                    `exerciseOrder` INTEGER NOT NULL,
                    `sets` INTEGER NOT NULL,
                    `reps` TEXT NOT NULL,
                    `restSeconds` INTEGER NOT NULL,
                    FOREIGN KEY(`plannedWorkoutId`)
                        REFERENCES `planned_workouts`(`id`)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE
                )
                """.trimIndent()
                )

                database.execSQL(
                    """
                CREATE INDEX IF NOT EXISTS
                `index_planned_exercises_plannedWorkoutId`
                ON `planned_exercises` (`plannedWorkoutId`)
                """.trimIndent()
                )

                database.execSQL(
                    """
                CREATE INDEX IF NOT EXISTS
                `index_planned_exercises_exerciseId`
                ON `planned_exercises` (`exerciseId`)
                """.trimIndent()
                )
            }
        }

    @Provides
    fun provideExerciseDao(
        database: ForceDatabase
    ): ExerciseDao {
        return database.exerciseDao()
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
                MIGRATION_8_9,
                MIGRATION_9_10,
                MIGRATION_10_11,
                MIGRATION_11_12,
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
    fun provideWorkoutPlanDao(
        database: ForceDatabase
    ): WorkoutPlanDao {
        return database.workoutPlanDao()
    }

    @Provides
    fun provideFavoriteExerciseDao(
        database: ForceDatabase
    ): FavoriteExerciseDao {
        return database.favoriteExerciseDao()
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
        workoutSetDao: WorkoutSetDao,
        favoriteExerciseDao: FavoriteExerciseDao
    ): WorkoutSessionRepository {

        return WorkoutSessionRepository(
            workoutSessionDao = workoutSessionDao,
            workoutExerciseDao = workoutExerciseDao,
            workoutSetDao = workoutSetDao,
            favoriteExerciseDao = favoriteExerciseDao
        )
    }


}