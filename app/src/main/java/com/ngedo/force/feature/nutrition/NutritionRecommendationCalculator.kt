package com.ngedo.force.feature.nutrition

import kotlin.math.roundToInt

data class NutritionRecommendation(
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int
)

object NutritionRecommendationCalculator {

    fun calculate(
        age: Int,
        sex: String,
        weightKg: Double,
        heightCm: Double,
        activityLevel: String,
        trainingGoal: String
    ): NutritionRecommendation {

        val bmr =
            if (sex == "Male") {
                10.0 * weightKg +
                        6.25 * heightCm -
                        5.0 * age +
                        5.0
            } else {
                10.0 * weightKg +
                        6.25 * heightCm -
                        5.0 * age -
                        161.0
            }

        val activityMultiplier =
            when (activityLevel) {

                "Sedentary" ->
                    1.2

                "Lightly Active" ->
                    1.375

                "Moderately Active" ->
                    1.55

                "Very Active" ->
                    1.725

                else ->
                    1.55
            }

        val maintenanceCalories =
            bmr * activityMultiplier

        val targetCalories =
            when (trainingGoal) {

                "Lose Fat" ->
                    maintenanceCalories * 0.85

                "Build Muscle" ->
                    maintenanceCalories * 1.10

                "Strength" ->
                    maintenanceCalories * 1.05

                else ->
                    maintenanceCalories
            }

        val proteinGrams =
            when (trainingGoal) {

                "Build Muscle",
                "Strength" ->
                    weightKg * 2.0

                "Lose Fat" ->
                    weightKg * 2.2

                else ->
                    weightKg * 1.8
            }

        val fatGrams =
            weightKg * 0.8

        val proteinCalories =
            proteinGrams * 4.0

        val fatCalories =
            fatGrams * 9.0

        val remainingCalories =
            targetCalories -
                    proteinCalories -
                    fatCalories

        val carbsGrams =
            (remainingCalories / 4.0)
                .coerceAtLeast(0.0)

        return NutritionRecommendation(
            calories =
                targetCalories.roundToInt(),

            proteinGrams =
                proteinGrams.roundToInt(),

            carbsGrams =
                carbsGrams.roundToInt(),

            fatGrams =
                fatGrams.roundToInt()
        )
    }
}