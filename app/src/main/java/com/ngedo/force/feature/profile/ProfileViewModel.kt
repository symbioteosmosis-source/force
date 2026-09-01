package com.ngedo.force.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngedo.force.data.local.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.ngedo.force.feature.nutrition.NutritionRecommendation
import com.ngedo.force.feature.nutrition.NutritionRecommendationCalculator
import com.ngedo.force.data.local.repository.NutritionRepository

data class ProfileUiState(
    val name: String = "",
    val ageInput: String = "",
    val sex: String = "Male",
    val activityLevel: String = "Moderately Active",
    val weightInput: String = "",
    val heightInput: String = "",

    val trainingGoal: String = "Build Muscle",
    val preferredUnits: String = "Metric",

    val nutritionRecommendation: NutritionRecommendation? = null,
    val nutritionGoalsApplied: Boolean = false,

    val isLoading: Boolean = true,
    val isSaved: Boolean = false,

    val isEditing: Boolean = false


)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val nutritionRepository: NutritionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            ProfileUiState()
        )

    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    init {
        observeProfile()
    }

    private fun observeProfile() {

        viewModelScope.launch {

            userProfileRepository
                .getProfile()
                .collect { profile ->

                    if (profile != null) {

                        val recommendation =
                            calculateNutritionRecommendation(
                                age = profile.age,
                                sex = profile.sex,
                                weightKg = profile.weightKg,
                                heightCm = profile.heightCm,
                                activityLevel = profile.activityLevel,
                                trainingGoal = profile.trainingGoal
                            )

                        _uiState.value =
                            _uiState.value.copy(
                                name = profile.name,

                                ageInput =
                                    profile.age.toString(),

                                sex = profile.sex,

                                activityLevel =
                                    profile.activityLevel,


                                weightInput =
                                    if (
                                        profile.preferredUnits ==
                                        "Imperial"
                                    ) {
                                        formatNumber(
                                            profile.weightKg * 2.20462
                                        )
                                    } else {
                                        formatNumber(
                                            profile.weightKg
                                        )
                                    },

                                heightInput =
                                    if (
                                        profile.preferredUnits ==
                                        "Imperial"
                                    ) {
                                        formatNumber(
                                            profile.heightCm / 2.54
                                        )
                                    } else {
                                        formatNumber(
                                            profile.heightCm
                                        )
                                    },

                                trainingGoal =
                                    profile.trainingGoal,

                                preferredUnits =
                                    profile.preferredUnits,

                                nutritionRecommendation =
                                    recommendation,

                                isLoading = false
                            )

                    } else {

                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false
                            )
                    }
                }
        }
    }

    fun applyRecommendedNutritionGoals() {

        val recommendation =
            _uiState.value.nutritionRecommendation
                ?: return

        viewModelScope.launch {

            nutritionRepository.saveNutritionGoal(
                calorieTarget =
                    recommendation.calories.toDouble(),

                proteinTarget =
                    recommendation.proteinGrams.toDouble(),

                carbsTarget =
                    recommendation.carbsGrams.toDouble(),

                fatTarget =
                    recommendation.fatGrams.toDouble()
            )

            _uiState.value =
                _uiState.value.copy(
                    nutritionGoalsApplied = true
                )
        }
    }

    private fun calculateNutritionRecommendation(
        age: Int,
        sex: String,
        weightKg: Double,
        heightCm: Double,
        activityLevel: String,
        trainingGoal: String
    ): NutritionRecommendation {

        return NutritionRecommendationCalculator.calculate(
            age = age,
            sex = sex,
            weightKg = weightKg,
            heightCm = heightCm,
            activityLevel = activityLevel,
            trainingGoal = trainingGoal
        )
    }

    fun startEditing() {

        _uiState.value =
            _uiState.value.copy(
                isEditing = true,
                isSaved = false
            )
    }

    fun updateName(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                name = value,
                isSaved = false
            )
    }

    fun updateAge(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                ageInput = value,
                isSaved = false
            )
    }

    fun updateSex(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                sex = value,
                isSaved = false
            )
    }

    fun updateActivityLevel(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                activityLevel = value,
                isSaved = false
            )
    }

    fun updateWeight(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                weightInput = value,
                isSaved = false
            )
    }

    fun updateHeight(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                heightInput = value,
                isSaved = false
            )
    }

    fun updateTrainingGoal(
        goal: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                trainingGoal = goal,
                isSaved = false
            )
    }

    fun updatePreferredUnits(
        units: String
    ) {

        val currentState =
            _uiState.value

        if (
            units ==
            currentState.preferredUnits
        ) {
            return
        }

        val currentWeight =
            currentState.weightInput
                .toDoubleOrNull()

        val currentHeight =
            currentState.heightInput
                .toDoubleOrNull()

        val convertedWeight =
            when {

                currentWeight == null ->
                    currentState.weightInput

                currentState.preferredUnits == "Metric" &&
                        units == "Imperial" ->

                    formatNumber(
                        currentWeight * 2.20462
                    )

                currentState.preferredUnits == "Imperial" &&
                        units == "Metric" ->

                    formatNumber(
                        currentWeight / 2.20462
                    )

                else ->
                    currentState.weightInput
            }

        val convertedHeight =
            when {

                currentHeight == null ->
                    currentState.heightInput

                currentState.preferredUnits == "Metric" &&
                        units == "Imperial" ->

                    formatNumber(
                        currentHeight / 2.54
                    )

                currentState.preferredUnits == "Imperial" &&
                        units == "Metric" ->

                    formatNumber(
                        currentHeight * 2.54
                    )

                else ->
                    currentState.heightInput
            }

        _uiState.value =
            currentState.copy(
                preferredUnits = units,
                weightInput = convertedWeight,
                heightInput = convertedHeight,
                isSaved = false
            )
    }

    fun saveProfile() {

        val currentState =
            _uiState.value

        val name =
            currentState.name.trim()

        val age =
            currentState.ageInput
                .toIntOrNull()
                ?: return

        val enteredWeight =
            currentState.weightInput
                .toDoubleOrNull()
                ?: return

        val enteredHeight =
            currentState.heightInput
                .toDoubleOrNull()
                ?: return

        val weightKg =
            if (
                currentState.preferredUnits ==
                "Imperial"
            ) {
                enteredWeight / 2.20462
            } else {
                enteredWeight
            }

        val heightCm =
            if (
                currentState.preferredUnits ==
                "Imperial"
            ) {
                enteredHeight * 2.54
            } else {
                enteredHeight
            }

        if (
            name.isBlank() ||
            age <= 0 ||
            weightKg <= 0.0 ||
            heightCm <= 0.0
        ) {
            return
        }

        val recommendation =
            calculateNutritionRecommendation(
                age = age,
                sex = currentState.sex,
                weightKg = weightKg,
                heightCm = heightCm,
                activityLevel = currentState.activityLevel,
                trainingGoal = currentState.trainingGoal
            )

        viewModelScope.launch {

            userProfileRepository.saveProfile(
                name = name,
                age = age,
                sex = currentState.sex,
                activityLevel =
                    currentState.activityLevel,
                weightKg = weightKg,
                heightCm = heightCm,
                trainingGoal =
                    currentState.trainingGoal,
                preferredUnits =
                    currentState.preferredUnits
            )

            _uiState.value =
                _uiState.value.copy(
                    nutritionRecommendation =
                        recommendation,
                    nutritionGoalsApplied = false,
                    isSaved = true,
                    isEditing = false
                )
        }
    }

    private fun formatNumber(
        value: Double
    ): String {

        return if (
            value % 1.0 == 0.0
        ) {
            value.toInt().toString()
        } else {
            String.format(
                java.util.Locale.US,
                "%.1f",
                value
            )
        }
    }
}