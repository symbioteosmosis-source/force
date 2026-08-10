package com.ngedo.force.navigation

sealed class ForceDestination(val route: String) {

    object Home : ForceDestination("home")

    object Workout : ForceDestination("workout")

    object Progress : ForceDestination("progress")

    object Nutrition : ForceDestination("nutrition")

    object Profile : ForceDestination("profile")

    object AiCoach : ForceDestination("ai_coach")
}