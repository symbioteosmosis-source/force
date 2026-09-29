package com.ngedo.force.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ngedo.force.designsystem.ForceColors
import com.ngedo.force.designsystem.components.navigation.ForceBottomBar
import com.ngedo.force.feature.ai.AiCoachScreen
import com.ngedo.force.feature.home.HomeScreen
import com.ngedo.force.feature.nutrition.NutritionScreen
import com.ngedo.force.feature.profile.ProfileScreen
import com.ngedo.force.feature.progress.ProgressScreen
import com.ngedo.force.feature.workout.WorkoutHistoryScreen
import com.ngedo.force.feature.workout.WorkoutScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ngedo.force.feature.workout.WorkoutHistoryDetailScreen
import com.ngedo.force.feature.exercise.ExerciseLibraryScreen
import com.ngedo.force.feature.exercise.ExerciseDetailScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.ngedo.force.feature.workout.plan.MonthlyPlanScreen
import com.ngedo.force.feature.workout.plan.MonthlyPlanViewModel
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun ForceNavGraph() {

    val navController =
        rememberNavController()

    val monthlyPlanViewModel =
        hiltViewModel<MonthlyPlanViewModel>()

    val monthlyPlanState by
    monthlyPlanViewModel
        .uiState
        .collectAsState()

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry
            ?.destination
            ?.route


    /*
     * =========================================================
     * SELECTED BOTTOM NAVIGATION ITEM
     * =========================================================
     */

    val selectedIndex =
        when (currentRoute) {

            ForceDestination.Home.route -> 0

            ForceDestination.Workout.route -> 1

            ForceDestination.Progress.route -> 2

            ForceDestination.Nutrition.route -> 3

            ForceDestination.Profile.route -> 4

            else -> 0
        }


    /*
     * =========================================================
     * BOTTOM BAR VISIBILITY
     * =========================================================
     *
     * Hide the bottom navigation while:
     *
     * - Performing a workout
     * - Viewing workout history
     */

    val showBottomBar =
        currentRoute !=
                ForceDestination.Workout.route &&
                currentRoute !=
                ForceDestination.ExerciseLibrary.route &&
                currentRoute !=
                ForceDestination.ExerciseDetail.route &&
                currentRoute !=
                ForceDestination.WorkoutHistory.route &&
                currentRoute !=
                ForceDestination.WorkoutHistoryDetail.route &&
                currentRoute !=
                ForceDestination.MonthlyPlan.route &&
                currentRoute !=
                "plan_exercise_library/{day}" &&
                currentRoute !=
                "plan_exercise_detail/{day}/{exerciseId}"



    /*
     * =========================================================
     * APP SCAFFOLD
     * =========================================================
     */

    Scaffold(
        modifier =
            Modifier.fillMaxSize(),

        containerColor =
            ForceColors.Background,

        bottomBar = {

            if (showBottomBar) {

                ForceBottomBar(
                    selectedIndex =
                        selectedIndex,

                    onItemSelected = { index ->

                        val destination =
                            when (index) {

                                0 ->
                                    ForceDestination.Home

                                1 ->
                                    ForceDestination.Workout

                                2 ->
                                    ForceDestination.Progress

                                3 ->
                                    ForceDestination.Nutrition

                                4 ->
                                    ForceDestination.Profile

                                else ->
                                    ForceDestination.Home
                            }


                        /*
                         * Navigate between the main
                         * bottom navigation destinations.
                         */

                        if (
                            destination ==
                            ForceDestination.Workout
                        ) {

                            navController.navigate(
                                ForceDestination.Workout.baseRoute
                            ) {

                                popUpTo(
                                    ForceDestination.Home.route
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }

                            monthlyPlanState
                                .todayWorkout
                                ?.plannedWorkoutId
                                ?.let { plannedWorkoutId ->

                                    navController
                                        .getBackStackEntry(
                                            ForceDestination.Workout.baseRoute
                                        )
                                        .savedStateHandle[
                                        "preview_planned_workout_id"
                                    ] = plannedWorkoutId
                                }

                        } else {

                            navController.navigate(
                                destination.route
                            ) {

                                popUpTo(
                                    ForceDestination.Home.route
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->


        /*
         * =====================================================
         * NAVIGATION HOST
         * =====================================================
         */

        NavHost(
            navController =
                navController,

            startDestination =
                ForceDestination.Home.route,

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    innerPadding
                )
        ) {


            /*
             * -------------------------------------------------
             * HOME
             * -------------------------------------------------
             */

            composable(
                route =
                    ForceDestination.Home.route
            ) {

                HomeScreen(
                    onNavigate = { destination ->

                        navController.navigate(
                            destination.route
                        ) {
                            launchSingleTop = true
                        }
                    },

                    onViewTodayWorkout = { plannedWorkoutId ->

                        navController.navigate(
                            ForceDestination.Workout.baseRoute
                        ) {
                            launchSingleTop = true
                        }

                        navController
                            .getBackStackEntry(
                                ForceDestination.Workout.baseRoute
                            )
                            .savedStateHandle[
                            "preview_planned_workout_id"
                        ] = plannedWorkoutId
                    },

                    onStartTodayWorkout = { plannedWorkoutId ->

                        navController.navigate(
                            ForceDestination.Workout
                                .createRoute(plannedWorkoutId)
                        )
                    }
                )
            }


            /*
             * -------------------------------------------------
             * WORKOUT
             * -------------------------------------------------
             */

            composable(
                route = ForceDestination.Workout.route,

                arguments = listOf(
                    navArgument(
                        "plannedWorkoutId"
                    ) {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->

                val routePlannedWorkoutId =
                    backStackEntry.arguments
                        ?.getLong("plannedWorkoutId")
                        ?.takeIf { it > 0L }

                val plannedWorkoutId by
                backStackEntry
                    .savedStateHandle
                    .getStateFlow<Long?>(
                        "planned_workout_id",
                        null
                    )
                    .collectAsState()

                val previewPlannedWorkoutId by
                backStackEntry
                    .savedStateHandle
                    .getStateFlow<Long?>(
                        "preview_planned_workout_id",
                        null
                    )
                    .collectAsState()

                val exercisesToAdd by
                backStackEntry
                    .savedStateHandle
                    .getStateFlow(
                        "exercises_to_add",
                        emptyList<String>()
                    )
                    .collectAsState()

                val exerciseToRemove by
                backStackEntry
                    .savedStateHandle
                    .getStateFlow<String?>(
                        "exercise_to_remove",
                        null
                    )
                    .collectAsState()

                WorkoutScreen(

                    plannedWorkoutId =
                        routePlannedWorkoutId
                            ?: plannedWorkoutId,

                    previewPlannedWorkoutId =
                        previewPlannedWorkoutId,

                    onWorkoutFinished = {

                        navController.navigate(
                            ForceDestination.Home.route
                        ) {
                            popUpTo(
                                ForceDestination.Home.route
                            ) {
                                inclusive = false
                            }

                            launchSingleTop = true
                        }
                    },

                    onPlannedWorkoutLoaded = {

                        backStackEntry
                            .savedStateHandle
                            .remove<Long>(
                                "planned_workout_id"
                            )
                    },

                    exercisesToAdd =
                        exercisesToAdd,

                    onExerciseAdded = {

                        backStackEntry
                            .savedStateHandle
                            .remove<List<String>>(
                                "exercises_to_add"
                            )
                    },

                    exerciseToRemove =
                        exerciseToRemove,

                    onExerciseRemoved = {

                        backStackEntry
                            .savedStateHandle
                            .remove<String>(
                                "exercise_to_remove"
                            )
                    },

                    onExerciseLibraryClick = {

                        navController.navigate(
                            ForceDestination.ExerciseLibrary.route
                        )
                    },

                    onMonthlyPlanClick = {

                        navController.navigate(
                            ForceDestination.MonthlyPlan.route
                        )
                    },

                    onExerciseDetailsClick = { exerciseId ->

                        navController.navigate(
                            ForceDestination
                                .ExerciseDetail
                                .createRoute(exerciseId)
                        )
                    },

                    onPlannedExerciseNamesChanged = { exerciseNames ->

                        backStackEntry
                            .savedStateHandle[
                            "planned_exercise_names"
                        ] = exerciseNames
                    }
                )
            }

            /*
 * -------------------------------------------------
 * EXERCISE LIBRARY
 * -------------------------------------------------
 */

            composable(
                route =
                    ForceDestination.ExerciseLibrary.route
            ) { backStackEntry ->

                val workoutEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(
                            ForceDestination.Workout.baseRoute
                        )
                    }

                val addedExerciseNames by
                workoutEntry.savedStateHandle
                    .getStateFlow(
                        "planned_exercise_names",
                        emptyList<String>()
                    )
                    .collectAsState()

                ExerciseLibraryScreen(

                    addedExerciseNames =
                        addedExerciseNames.toSet(),

                    onAddExercise = { exercise ->

                        val pendingExercises =
                            workoutEntry
                                .savedStateHandle
                                .get<List<String>>(
                                    "exercises_to_add"
                                )
                                ?: emptyList()

                        workoutEntry
                            .savedStateHandle[
                            "exercises_to_add"
                        ] =
                            (
                                    pendingExercises +
                                            exercise.name
                                    ).distinct()

                        workoutEntry
                            .savedStateHandle[
                            "planned_exercise_names"
                        ] =
                            (
                                    addedExerciseNames +
                                            exercise.name
                                    ).distinct()
                    },
                    onRemoveExercise = { exercise ->

                        workoutEntry
                            .savedStateHandle[
                            "exercise_to_remove"
                        ] =
                            exercise.name

                        workoutEntry
                            .savedStateHandle[
                            "planned_exercise_names"
                        ] =
                            addedExerciseNames.filterNot { exerciseName ->

                                exerciseName.equals(
                                    exercise.name,
                                    ignoreCase = true
                                )
                            }
                    },

                    onExerciseClick = { exercise ->

                        navController.navigate(
                            ForceDestination
                                .ExerciseDetail
                                .createRoute(
                                    exercise.id
                                )
                        )
                    }
                )
            }

            composable(
                route =
                    ForceDestination.ExerciseDetail.route,

                arguments = listOf(
                    navArgument(
                        "exerciseId"
                    ) {
                        type =
                            NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val workoutEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(
                            ForceDestination.Workout.baseRoute
                        )
                    }

                val addedExerciseNames by
                workoutEntry
                    .savedStateHandle
                    .getStateFlow(
                        "planned_exercise_names",
                        emptyList<String>()
                    )
                    .collectAsState()

                ExerciseDetailScreen(

                    onBack = {
                        navController.popBackStack()
                    },

                    addedExerciseNames =
                        addedExerciseNames.toSet(),

                    onUseInWorkout = { exercise ->

                        val pendingExercises =
                            workoutEntry
                                .savedStateHandle
                                .get<List<String>>(
                                    "exercises_to_add"
                                )
                                ?: emptyList()

                        workoutEntry
                            .savedStateHandle[
                            "exercises_to_add"
                        ] =
                            (
                                    pendingExercises +
                                            exercise.name
                                    ).distinct()

                        workoutEntry
                            .savedStateHandle[
                            "planned_exercise_names"
                        ] =
                            (
                                    addedExerciseNames +
                                            exercise.name
                                    ).distinct()

                        // Do NOT popBackStack().
                        // User stays on Exercise Detail.
                    },
                    onRemoveFromWorkout = { exercise ->

                        workoutEntry
                            .savedStateHandle[
                            "exercise_to_remove"
                        ] =
                            exercise.name

                        workoutEntry
                            .savedStateHandle[
                            "planned_exercise_names"
                        ] =
                            addedExerciseNames.filterNot { exerciseName ->

                                exerciseName.equals(
                                    exercise.name,
                                    ignoreCase = true
                                )
                            }
                    }
                )
            }

            /*
             * -------------------------------------------------
             * MONTHLY PLAN
             * -------------------------------------------------
             */

            composable(
                route = ForceDestination.MonthlyPlan.route
            ) {

                MonthlyPlanScreen(
                    onBack = {
                        navController.popBackStack()
                    },

                    onAddExercises = { day ->

                        navController.navigate(
                            "plan_exercise_library/$day"
                        )
                    },

                    onExerciseDetails = { exerciseId, day ->

                        navController.navigate(
                            "plan_exercise_detail/$day/$exerciseId"
                        )
                    },

                    onStartWorkout = { plannedWorkoutId ->

                        navController
                            .getBackStackEntry(
                                ForceDestination.Workout.baseRoute
                            )
                            .savedStateHandle[
                            "planned_workout_id"
                        ] = plannedWorkoutId

                        navController.popBackStack(
                            route =
                                ForceDestination.Workout.baseRoute,
                            inclusive = false
                        )
                    }
                )

            }

            composable(
                route = "plan_exercise_library/{day}",

                arguments = listOf(
                    navArgument("day") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val day =
                    backStackEntry.arguments
                        ?.getInt("day")
                        ?: return@composable

                val monthlyPlanEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(
                            ForceDestination.MonthlyPlan.route
                        )
                    }

                val planViewModel =
                    hiltViewModel<MonthlyPlanViewModel>(
                        monthlyPlanEntry
                    )

                val planState by
                planViewModel.uiState.collectAsState()

                val workoutDay =
                    planState.selectedPlannedWorkoutId
                        ?.let { plannedWorkoutId ->

                            planState.workoutDays
                                .firstOrNull {
                                    it.plannedWorkoutId ==
                                            plannedWorkoutId
                                }
                        }
                        ?: planState.workoutDays
                            .firstOrNull {
                                it.day == day &&
                                        it.weekNumber ==
                                        planState.selectedWeek
                            }

                val addedExerciseNames =
                    workoutDay
                        ?.exercises
                        ?.map {
                            it.exerciseName
                        }
                        ?.toSet()
                        ?: emptySet()

                ExerciseLibraryScreen(

                    addedExerciseNames =
                        addedExerciseNames,

                    onAddExercise = { exercise ->

                        planViewModel.addExerciseToSelectedDay(
                            exerciseId = exercise.id,
                            exerciseName = exercise.name,
                            target = exercise.primaryMuscle
                        )
                    },

                    onRemoveExercise = { exercise ->

                        planViewModel.removeExerciseFromSelectedDay(
                            exerciseId = exercise.id
                        )
                    },

                    onExerciseClick = { exercise ->

                        navController.navigate(
                            "plan_exercise_detail/$day/${exercise.id}"
                        )
                    }
                )
            }

            composable(
                route =
                    "plan_exercise_detail/{day}/{exerciseId}",

                arguments = listOf(

                    navArgument("day") {
                        type = NavType.IntType
                    },

                    navArgument("exerciseId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val day =
                    backStackEntry.arguments
                        ?.getInt("day")
                        ?: return@composable

                val monthlyPlanEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(
                            ForceDestination.MonthlyPlan.route
                        )
                    }

                val planViewModel =
                    hiltViewModel<MonthlyPlanViewModel>(
                        monthlyPlanEntry
                    )

                val planState by
                planViewModel.uiState.collectAsState()

                val workoutDay =
                    planState.selectedPlannedWorkoutId
                        ?.let { plannedWorkoutId ->

                            planState.workoutDays
                                .firstOrNull {
                                    it.plannedWorkoutId ==
                                            plannedWorkoutId
                                }
                        }
                        ?: planState.workoutDays
                            .firstOrNull {
                                it.day == day &&
                                        it.weekNumber ==
                                        planState.selectedWeek
                            }

                val addedExerciseNames =
                    workoutDay
                        ?.exercises
                        ?.map {
                            it.exerciseName
                        }
                        ?.toSet()
                        ?: emptySet()

                ExerciseDetailScreen(

                    onBack = {
                        navController.popBackStack()
                    },

                    addedExerciseNames =
                        addedExerciseNames,

                    onUseInWorkout = { exercise ->

                        planViewModel.addExerciseToSelectedDay(
                            exerciseId = exercise.id,
                            exerciseName = exercise.name,
                            target = exercise.primaryMuscle
                        )
                    },

                    onRemoveFromWorkout = { exercise ->

                        planViewModel.removeExerciseFromSelectedDay(
                            exerciseId = exercise.id
                        )
                    }
                )
            }

            /*
             * -------------------------------------------------
             * WORKOUT HISTORY
             * -------------------------------------------------
             */

            composable(
                route =
                    ForceDestination.WorkoutHistory.route
            ) {

                WorkoutHistoryScreen(
                    onSessionClick = { sessionId ->

                        navController.navigate(
                            ForceDestination
                                .WorkoutHistoryDetail
                                .createRoute(
                                    sessionId
                                )
                        )
                    }
                )
            }

            composable(
                route =
                    ForceDestination
                        .WorkoutHistoryDetail
                        .route,

                arguments = listOf(
                    navArgument(
                        "sessionId"
                    ) {
                        type =
                            NavType.LongType
                    }
                )
            ) { backStackEntry ->

                val sessionId =
                    backStackEntry
                        .arguments
                        ?.getLong(
                            "sessionId"
                        )
                        ?: return@composable

                WorkoutHistoryDetailScreen(
                    sessionId =
                        sessionId,

                    onBack = {
                        navController
                            .popBackStack()
                    }
                )
            }


            /*
             * -------------------------------------------------
             * PROGRESS
             * -------------------------------------------------
             */

            composable(
                route =
                    ForceDestination.Progress.route
            ) {

                ProgressScreen(
                    onWorkoutHistoryClick = {

                        navController.navigate(
                            ForceDestination.WorkoutHistory.route
                        )
                    },

                    onPersonalRecordClick = { sessionId ->

                        navController.navigate(
                            ForceDestination
                                .WorkoutHistoryDetail
                                .createRoute(
                                    sessionId
                                )
                        )
                    }
                )
            }


            /*
             * -------------------------------------------------
             * NUTRITION
             * -------------------------------------------------
             */

            composable(
                route =
                    ForceDestination.Nutrition.route
            ) {

                NutritionScreen()
            }


            /*
             * -------------------------------------------------
             * PROFILE
             * -------------------------------------------------
             */

            composable(
                route =
                    ForceDestination.Profile.route
            ) {

                ProfileScreen()
            }


            /*
             * -------------------------------------------------
             * AI COACH
             * -------------------------------------------------
             */

            composable(
                route =
                    ForceDestination.AiCoach.route
            ) {

                AiCoachScreen(
                    onBack = {

                        navController.popBackStack()
                    }
                )
            }
        }
    }
}