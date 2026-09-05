package com.ngedo.force.feature.workout

data class TrainingRecommendation(
    val suggestedWeight: Double?,
    val suggestedReps: Int?,
    val reason: String,
    val recommendationType: TrainingRecommendationType
)

enum class TrainingRecommendationType {
    NO_HISTORY,
    RETURNING_AFTER_BREAK,
    RAMP_UP,
    MATCH_PREVIOUS,
    PROGRESSION,
    REDUCE_LOAD
}