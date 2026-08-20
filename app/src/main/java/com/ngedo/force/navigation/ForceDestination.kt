package com.ngedo.force.navigation

sealed class ForceDestination(val route: String) {

    object Home : ForceDestination("home")

    object Workout : ForceDestination("workout")

    object WorkoutHistory : ForceDestination("workout_history")

    object WorkoutHistoryDetail : ForceDestination(
            "workout_history_detail/{sessionId}"
        ) {

        fun createRoute(
            sessionId: Long
        ): String {

            return "workout_history_detail/$sessionId"
        }
    }

    object Progress : ForceDestination("progress")

    object Nutrition : ForceDestination("nutrition")

    object Profile : ForceDestination("profile")

    object AiCoach : ForceDestination("ai_coach")
}