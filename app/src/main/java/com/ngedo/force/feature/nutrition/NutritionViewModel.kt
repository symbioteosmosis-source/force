package com.ngedo.force.feature.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngedo.force.data.local.entity.NutritionEntryEntity
import com.ngedo.force.data.local.repository.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



data class NutritionUiState(
    val entries: List<NutritionEntryEntity> = emptyList(),

    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,

    val calorieTarget: Double = 2400.0,
    val proteinTarget: Double = 180.0,
    val carbsTarget: Double = 250.0,
    val fatTarget: Double = 75.0,

    val isLoading: Boolean = true,

    val selectedMealType: String = "Breakfast",
    val foodName: String = "",
    val caloriesInput: String = "",
    val proteinInput: String = "",
    val carbsInput: String = "",
    val fatInput: String = "",

    val isGoalEditorVisible: Boolean = false,

    val calorieTargetInput: String = "",
    val proteinTargetInput: String = "",
    val carbsTargetInput: String = "",
    val fatTargetInput: String = "",

    val isAddFoodVisible: Boolean = false,

    val entryPendingDelete: NutritionEntryEntity? = null,
    val entryBeingEdited: NutritionEntryEntity? = null,
    val selectedDate: Long = System.currentTimeMillis()

)

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            NutritionUiState()
        )

    val uiState: StateFlow<NutritionUiState> =
        _uiState.asStateFlow()

    private var entriesJob: Job? = null
    private var caloriesJob: Job? = null
    private var proteinJob: Job? = null
    private var carbsJob: Job? = null
    private var fatJob: Job? = null

    private var nutritionGoalJob: Job? = null

    init {
        observeSelectedDay()
        observeNutritionGoal()
    }

    fun deleteFood(
        entry: NutritionEntryEntity
    ) {
        viewModelScope.launch {
            nutritionRepository.deleteEntry(
                entry
            )
        }
    }

    fun previousDay() {

        val calendar =
            Calendar.getInstance().apply {
                timeInMillis =
                    _uiState.value.selectedDate

                add(
                    Calendar.DAY_OF_YEAR,
                    -1
                )
            }

        _uiState.value =
            _uiState.value.copy(
                selectedDate =
                    calendar.timeInMillis
            )

        observeSelectedDay()
    }


    fun nextDay() {

        val calendar =
            Calendar.getInstance().apply {
                timeInMillis =
                    _uiState.value.selectedDate

                add(
                    Calendar.DAY_OF_YEAR,
                    1
                )
            }

        val today =
            System.currentTimeMillis()

        /*
         * Do not allow navigation into the future.
         */
        if (
            calendar.timeInMillis >
            today
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedDate =
                    calendar.timeInMillis
            )

        observeSelectedDay()
    }

    fun showGoalEditor() {

        val currentState =
            _uiState.value

        _uiState.value =
            currentState.copy(
                isGoalEditorVisible = true,

                calorieTargetInput =
                    formatInputNumber(
                        currentState.calorieTarget
                    ),

                proteinTargetInput =
                    formatInputNumber(
                        currentState.proteinTarget
                    ),

                carbsTargetInput =
                    formatInputNumber(
                        currentState.carbsTarget
                    ),

                fatTargetInput =
                    formatInputNumber(
                        currentState.fatTarget
                    )
            )
    }


    fun requestDeleteFood(
        entry: NutritionEntryEntity
    ) {
        _uiState.value =
            _uiState.value.copy(
                entryPendingDelete = entry
            )
    }

    fun cancelDeleteFood() {
        _uiState.value =
            _uiState.value.copy(
                entryPendingDelete = null
            )
    }

    fun confirmDeleteFood() {

        val entry =
            _uiState.value.entryPendingDelete
                ?: return

        viewModelScope.launch {

            nutritionRepository.deleteEntry(
                entry
            )

            _uiState.value =
                _uiState.value.copy(
                    entryPendingDelete = null
                )
        }
    }
    private fun observeSelectedDay() {

        val startOfDay =
            getStartOfDay(
                _uiState.value.selectedDate
            )

        val endOfDay =
            getStartOfNextDay(
                _uiState.value.selectedDate
            )

        entriesJob?.cancel()
        caloriesJob?.cancel()
        proteinJob?.cancel()
        carbsJob?.cancel()
        fatJob?.cancel()

        entriesJob =
            viewModelScope.launch {

                nutritionRepository
                    .getEntriesForDay(
                        startOfDay = startOfDay,
                        endOfDay = endOfDay
                    )
                    .collect { entries ->

                        _uiState.value =
                            _uiState.value.copy(
                                entries = entries,
                                isLoading = false
                            )
                    }
            }

        caloriesJob =
            viewModelScope.launch {

                nutritionRepository
                    .getCaloriesForDay(
                        startOfDay = startOfDay,
                        endOfDay = endOfDay
                    )
                    .collect { calories ->

                        _uiState.value =
                            _uiState.value.copy(
                                calories = calories
                            )
                    }
            }

        proteinJob =
            viewModelScope.launch {

                nutritionRepository
                    .getProteinForDay(
                        startOfDay = startOfDay,
                        endOfDay = endOfDay
                    )
                    .collect { protein ->

                        _uiState.value =
                            _uiState.value.copy(
                                protein = protein
                            )
                    }
            }

        carbsJob =
            viewModelScope.launch {

                nutritionRepository
                    .getCarbsForDay(
                        startOfDay = startOfDay,
                        endOfDay = endOfDay
                    )
                    .collect { carbs ->

                        _uiState.value =
                            _uiState.value.copy(
                                carbs = carbs
                            )
                    }
            }

        fatJob =
            viewModelScope.launch {

                nutritionRepository
                    .getFatForDay(
                        startOfDay = startOfDay,
                        endOfDay = endOfDay
                    )
                    .collect { fat ->

                        _uiState.value =
                            _uiState.value.copy(
                                fat = fat
                            )
                    }
            }


    }

    private fun observeNutritionGoal() {

        nutritionGoalJob?.cancel()

        nutritionGoalJob =
            viewModelScope.launch {

                nutritionRepository
                    .getNutritionGoal()
                    .collect { goal ->

                        if (goal != null) {

                            _uiState.value =
                                _uiState.value.copy(
                                    calorieTarget =
                                        goal.calorieTarget,

                                    proteinTarget =
                                        goal.proteinTarget,

                                    carbsTarget =
                                        goal.carbsTarget,

                                    fatTarget =
                                        goal.fatTarget
                                )
                        }
                    }
            }
    }

    private fun getStartOfDay(
        timestamp: Long
    ): Long {

        val calendar =
            Calendar.getInstance().apply {

                timeInMillis = timestamp

                set(
                    Calendar.HOUR_OF_DAY,
                    0
                )

                set(
                    Calendar.MINUTE,
                    0
                )

                set(
                    Calendar.SECOND,
                    0
                )

                set(
                    Calendar.MILLISECOND,
                    0
                )
            }

        return calendar.timeInMillis
    }


    private fun getStartOfNextDay(
        timestamp: Long
    ): Long {

        val calendar =
            Calendar.getInstance().apply {

                timeInMillis =
                    getStartOfDay(
                        timestamp
                    )

                add(
                    Calendar.DAY_OF_YEAR,
                    1
                )
            }

        return calendar.timeInMillis
    }


    override fun onCleared() {
        entriesJob?.cancel()
        caloriesJob?.cancel()
        proteinJob?.cancel()
        carbsJob?.cancel()
        fatJob?.cancel()
        nutritionGoalJob?.cancel()

        super.onCleared()
    }

    fun showAddFood() {
        _uiState.value =
            _uiState.value.copy(
                isAddFoodVisible = true
            )
    }

    fun hideAddFood() {

        _uiState.value =
            _uiState.value.copy(
                isAddFoodVisible = false,
                entryBeingEdited = null,

                foodName = "",
                caloriesInput = "",
                proteinInput = "",
                carbsInput = "",
                fatInput = ""
            )
    }
    fun updateMealType(
        mealType: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                selectedMealType = mealType
            )
    }

    fun updateFoodName(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                foodName = value
            )
    }

    fun updateCalories(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                caloriesInput = value
            )
    }

    fun updateProtein(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                proteinInput = value
            )
    }

    fun updateCarbs(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                carbsInput = value
            )
    }

    fun updateFat(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                fatInput = value
            )
    }

    fun saveFood() {

        val currentState =
            _uiState.value

        val foodName =
            currentState.foodName.trim()

        if (foodName.isBlank()) {
            return
        }

        val calories =
            currentState.caloriesInput
                .toDoubleOrNull()
                ?: 0.0

        val protein =
            currentState.proteinInput
                .toDoubleOrNull()
                ?: 0.0

        val carbs =
            currentState.carbsInput
                .toDoubleOrNull()
                ?: 0.0

        val fat =
            currentState.fatInput
                .toDoubleOrNull()
                ?: 0.0

        viewModelScope.launch {

            val entryBeingEdited =
                currentState.entryBeingEdited

            if (entryBeingEdited != null) {

                nutritionRepository.updateEntry(
                    entryBeingEdited.copy(
                        mealType =
                            currentState.selectedMealType,

                        foodName =
                            foodName,

                        calories =
                            calories,

                        proteinGrams =
                            protein,

                        carbsGrams =
                            carbs,

                        fatGrams =
                            fat
                    )
                )

            } else {

                nutritionRepository.addEntry(

                    date = currentState.selectedDate,

                    mealType =
                        currentState.selectedMealType,

                    foodName =
                        foodName,

                    calories =
                        calories,

                    proteinGrams =
                        protein,

                    carbsGrams =
                        carbs,

                    fatGrams =
                        fat
                )
            }

            _uiState.value =
                _uiState.value.copy(
                    entryBeingEdited = null,

                    foodName = "",
                    caloriesInput = "",
                    proteinInput = "",
                    carbsInput = "",
                    fatInput = "",

                    isAddFoodVisible = false
                )
        }
    }


    fun startEditingFood(
        entry: NutritionEntryEntity
    ) {

        _uiState.value =
            _uiState.value.copy(
                entryBeingEdited = entry,

                selectedMealType =
                    entry.mealType,

                foodName =
                    entry.foodName,

                caloriesInput =
                    formatInputNumber(
                        entry.calories
                    ),

                proteinInput =
                    formatInputNumber(
                        entry.proteinGrams
                    ),

                carbsInput =
                    formatInputNumber(
                        entry.carbsGrams
                    ),

                fatInput =
                    formatInputNumber(
                        entry.fatGrams
                    ),

                isAddFoodVisible = true
            )
    }

    fun saveNutritionGoals() {

        val currentState =
            _uiState.value

        val calories =
            currentState.calorieTargetInput
                .toDoubleOrNull()
                ?: return

        val protein =
            currentState.proteinTargetInput
                .toDoubleOrNull()
                ?: return

        val carbs =
            currentState.carbsTargetInput
                .toDoubleOrNull()
                ?: return

        val fat =
            currentState.fatTargetInput
                .toDoubleOrNull()
                ?: return

        if (
            calories <= 0.0 ||
            protein <= 0.0 ||
            carbs <= 0.0 ||
            fat <= 0.0
        ) {
            return
        }

        viewModelScope.launch {

            nutritionRepository.saveNutritionGoal(
                calorieTarget = calories,
                proteinTarget = protein,
                carbsTarget = carbs,
                fatTarget = fat
            )

            _uiState.value =
                _uiState.value.copy(
                    isGoalEditorVisible = false
                )
        }
    }

    fun hideGoalEditor() {

        _uiState.value =
            _uiState.value.copy(
                isGoalEditorVisible = false
            )
    }

    fun updateCalorieTargetInput(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                calorieTargetInput = value
            )
    }

    fun updateProteinTargetInput(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                proteinTargetInput = value
            )
    }

    fun updateCarbsTargetInput(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                carbsTargetInput = value
            )
    }

    fun updateFatTargetInput(
        value: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                fatTargetInput = value
            )
    }

    private fun formatInputNumber(
        value: Double
    ): String {

        return if (
            value % 1.0 == 0.0
        ) {
            value.toInt().toString()
        } else {
            value.toString()
        }
    }
}