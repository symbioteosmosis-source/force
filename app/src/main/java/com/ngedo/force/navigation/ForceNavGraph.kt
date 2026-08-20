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

@Composable
fun ForceNavGraph() {

    val navController =
        rememberNavController()

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
                ForceDestination.WorkoutHistory.route &&
                currentRoute !=
                ForceDestination.WorkoutHistoryDetail.route


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
                    }
                )
            }


            /*
             * -------------------------------------------------
             * WORKOUT
             * -------------------------------------------------
             */

            composable(
                route =
                    ForceDestination.Workout.route
            ) {

                WorkoutScreen()
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