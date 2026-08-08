package com.ngedo.force.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ngedo.force.feature.home.HomeScreen
import com.ngedo.force.feature.nutrition.NutritionScreen
import com.ngedo.force.feature.profile.ProfileScreen
import com.ngedo.force.feature.progress.ProgressScreen
import com.ngedo.force.feature.workout.WorkoutScreen

@Composable
fun ForceNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ForceDestination.Home.route
    ) {

        composable(ForceDestination.Home.route) {
            HomeScreen(
                onNavigate = { destination ->
                    navController.navigate(destination.route)
                }
            )
        }

        composable(ForceDestination.Workout.route) {
            WorkoutScreen()
        }

        composable(ForceDestination.Progress.route) {
            ProgressScreen()
        }

        composable(ForceDestination.Nutrition.route) {
            NutritionScreen()
        }

        composable(ForceDestination.Profile.route) {
            ProfileScreen()
        }
    }
}