package com.ngedo.force.feature.ai

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AiCoachViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        AiCoachUiState()
    )

    val uiState: StateFlow<AiCoachUiState> =
        _uiState.asStateFlow()

    fun selectTopic(topic: AiCoachTopic) {

        val response = when (topic) {

            AiCoachTopic.WORKOUT ->
                "I can help you plan your workout, choose exercises, manage sets and reps, and improve your training progression."

            AiCoachTopic.PROGRESS ->
                "I can analyse your workout history, completed sets, strength improvements, volume and consistency."

            AiCoachTopic.NUTRITION ->
                "I can help you understand nutrition around your training, including meals, protein and post-workout nutrition."

            AiCoachTopic.RECOVERY ->
                "I can help you manage rest, recovery and training frequency so you can keep progressing without unnecessary fatigue."
        }

        _uiState.value = _uiState.value.copy(
            selectedTopic = topic,
            response = response
        )
    }

    fun updateMessage(message: String) {

        _uiState.value = _uiState.value.copy(
            userMessage = message
        )
    }

    fun sendMessage() {

        val message = _uiState.value.userMessage.trim()

        if (message.isEmpty()) return

        val response = generateResponse(message)

        _uiState.value = _uiState.value.copy(
            response = response,
            userMessage = ""
        )
    }

    private fun generateResponse(
        message: String
    ): String {

        val lowerMessage = message.lowercase()

        return when {

            lowerMessage.contains("workout") ||
                    lowerMessage.contains("exercise") ||
                    lowerMessage.contains("train") -> {

                "Based on your question, I can help you build or improve your workout. As FORCE learns your workout history, I'll be able to make recommendations based on your actual performance."
            }

            lowerMessage.contains("progress") ||
                    lowerMessage.contains("stronger") ||
                    lowerMessage.contains("strength") -> {

                "I can help you track strength, completed sets, volume and consistency. Detailed set logging will allow FORCE to give you more accurate progress recommendations."
            }

            lowerMessage.contains("food") ||
                    lowerMessage.contains("nutrition") ||
                    lowerMessage.contains("protein") ||
                    lowerMessage.contains("meal") -> {

                "I can help with training-related nutrition guidance. Later, FORCE will combine your nutrition information with your workout history for more personalised recommendations."
            }

            lowerMessage.contains("rest") ||
                    lowerMessage.contains("recovery") ||
                    lowerMessage.contains("sore") -> {

                "Recovery is an important part of progression. FORCE will eventually use your training volume, workout frequency and recovery history to provide personalised recommendations."
            }

            else -> {

                "I'm your FORCE AI Coach. Ask me about workouts, exercises, progress, nutrition or recovery."
            }
        }
    }
}