package com.ngedo.force.feature.workout

object TrainingRecommendationCalculator {

    private const val MILLIS_PER_DAY =
        24L * 60L * 60L * 1000L

    fun calculate(
        previousBestWeight: Double?,
        previousBestReps: Int?,
        mostRecentWeight: Double?,
        mostRecentReps: Int?,
        lastPerformedAt: Long?,
        currentSet: Int,
        totalSets: Int,
        currentSessionWeight: Double? = null,
        currentSessionReps: Int? = null,
        currentTime: Long = System.currentTimeMillis()
    ): TrainingRecommendation? {
        if (
            previousBestWeight == null ||
            previousBestReps == null ||
            previousBestWeight <= 0.0 ||
            previousBestReps <= 0
        ) {
            return null
        }

        val safeTotalSets =
            totalSets.coerceAtLeast(1)

        val safeCurrentSet =
            currentSet.coerceIn(
                1,
                safeTotalSets
            )

        val daysSinceLastPerformed =
            lastPerformedAt?.let { performedAt ->

                val elapsed =
                    (currentTime - performedAt)
                        .coerceAtLeast(0L)

                elapsed / MILLIS_PER_DAY
            }

        val referenceWeight =
            mostRecentWeight
                ?.takeIf { it > 0.0 }
                ?: previousBestWeight

        val referenceReps =
            mostRecentReps
                ?.takeIf { it > 0 }
                ?: previousBestReps

        val isReturningAfterBreak =
            daysSinceLastPerformed != null &&
                    daysSinceLastPerformed >= 14

        /*
         * Position of this set within the exercise.
         *
         * Examples:
         *
         * 3 sets → 0.0, 0.5, 1.0
         * 4 sets → 0.0, 0.33, 0.67, 1.0
         * 5 sets → 0.0, 0.25, 0.5, 0.75, 1.0
         */
        val setProgress =
            if (safeTotalSets == 1) {
                1.0
            } else {
                (safeCurrentSet - 1).toDouble() /
                        (safeTotalSets - 1).toDouble()
            }

        val percentage =
            if (isReturningAfterBreak) {

                /*
                 * Return session:
                 * 40% → maximum 85%
                 */
                0.40 + (0.45 * setProgress)

            } else {

                /*
                 * Normal session:
                 * 40% → maximum 100%
                 */
                0.40 + (0.60 * setProgress)
            }

        val recommendationType =
            when {

                isReturningAfterBreak &&
                        safeCurrentSet == safeTotalSets ->
                    TrainingRecommendationType.RETURNING_AFTER_BREAK

                safeCurrentSet < safeTotalSets ->
                    TrainingRecommendationType.RAMP_UP

                else ->
                    TrainingRecommendationType.MATCH_PREVIOUS
            }

        val reason =
            when {

                isReturningAfterBreak ->
                    "It has been $daysSinceLastPerformed days since you last trained this exercise. FORCE is rebuilding the load gradually and will keep this return session below your previous working level."

                safeCurrentSet == 1 ->
                    "Start lighter to prepare for the heavier working sets."

                safeCurrentSet < safeTotalSets ->
                    "Increase gradually toward your recent working load."

                else ->
                    "You have reached your recent working level. Match previous performance before attempting progression."
            }

        val calculatedWeight =
            roundWeight(
                referenceWeight * percentage
            )

        val validCurrentSessionWeight =
            currentSessionWeight
                ?.takeIf { it > 0.0 }

        val suggestedWeight =
            if (validCurrentSessionWeight != null) {
                maxOf(
                    calculatedWeight,
                    validCurrentSessionWeight
                )
            } else {
                calculatedWeight
            }

        val validCurrentSessionReps =
            currentSessionReps
                ?.takeIf { it > 0 }

        val suggestedReps =
            if (safeCurrentSet == 1) {

                // First set is always the warm-up target.
                10

            } else if (
                safeCurrentSet > 2 &&
                validCurrentSessionReps != null
            ) {

                // From Set 3 onward, respect performance
                // achieved in the previous set today.
                maxOf(
                    referenceReps,
                    validCurrentSessionReps
                )

            } else {

                // Set 2 should use the historical working-set
                // rep target instead of inheriting the
                // 10-rep warm-up.
                referenceReps
            }

        return TrainingRecommendation(
            suggestedWeight = suggestedWeight,
            suggestedReps = suggestedReps,
            reason = reason,
            recommendationType = recommendationType
        )
    }

    private fun roundWeight(
        weight: Double
    ): Double {
        return kotlin.math.round(weight / 2.5) * 2.5
    }
}