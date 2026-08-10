package com.ngedo.force.feature.ai

data class AiCoachUiState(
    val userMessage: String = "",
    val response: String = "",
    val selectedTopic: AiCoachTopic? = null,
    val isThinking: Boolean = false
)

enum class AiCoachTopic {
    WORKOUT,
    PROGRESS,
    NUTRITION,
    RECOVERY
}