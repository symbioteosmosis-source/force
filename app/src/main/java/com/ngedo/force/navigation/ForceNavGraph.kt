package com.ngedo.force.navigation

import androidx.compose.foundation.layout.fillMaxSize
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
import com.ngedo.force.feature.home.HomeScreen
import com.ngedo.force.feature.nutrition.NutritionScreen
import com.ngedo.force.feature.profile.ProfileScreen
import com.ngedo.force.feature.progress.ProgressScreen
import com.ngedo.force.feature.workout.WorkoutScreen
import androidx.compose.foundation.layout.padding
@Composable
fun ForceNavGraph() {

    val navController = rememberNavController()

    val navBackStackEntry by navController
        .currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route

    val selectedIndex = when (currentRoute) {

        ForceDestination.Home.route -> 0

        ForceDestination.Workout.route -> 1

        ForceDestination.Progress.route -> 2

        ForceDestination.Nutrition.route -> 3

        ForceDestination.Profile.route -> 4

        else -> 0
    }

    val showBottomBar =
        currentRoute != ForceDestination.Workout.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ForceColors.Background,

        bottomBar = {

            if (showBottomBar) {

                ForceBottomBar(
                    selectedIndex = selectedIndex,

                    onItemSelected = { index ->

                        val destination =
                            when (index) {

                                0 -> ForceDestination.Home

                                1 -> ForceDestination.Workout

                                2 -> ForceDestination.Progress

                                3 -> ForceDestination.Nutrition

                                4 -> ForceDestination.Profile

                                else -> ForceDestination.Home
                            }

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

        NavHost(
            navController = navController,
            startDestination = ForceDestination.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            composable(
                route = ForceDestination.Home.route
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

            composable(
                route = ForceDestination.Workout.route
            ) {
                WorkoutScreen()
            }

            composable(
                route = ForceDestination.Progress.route
            ) {
                ProgressScreen()
            }

            composable(
                route = ForceDestination.Nutrition.route
            ) {
                NutritionScreen()
            }

            composable(
                route = ForceDestination.Profile.route
            ) {
                ProfileScreen()
            }
        }
    }
}