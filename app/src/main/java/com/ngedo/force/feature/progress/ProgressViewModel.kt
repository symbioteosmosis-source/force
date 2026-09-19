package com.ngedo.force.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngedo.force.data.local.model.DailyWorkoutVolume
import com.ngedo.force.data.local.model.ExercisePersonalRecord
import com.ngedo.force.data.local.repository.WorkoutSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.ngedo.force.data.local.model.ExerciseStrengthProgress


data class ProgressUiState(
    val isLoading: Boolean = false,
    val personalRecords: List<ExercisePersonalRecord> = emptyList(),
    val workoutVolumeHistory: List<DailyWorkoutVolume> = emptyList(),
    val completedWorkoutCount: Int = 0,
    val selectedStrengthExercise: String? = null,
    val strengthHistory: List<ExerciseStrengthProgress> = emptyList(),
    val favoriteExercises: Set<String> = emptySet(),
    val exerciseSearchQuery: String = "",
    val isExerciseSelectorOpen: Boolean = false
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            ProgressUiState()
        )

    val uiState: StateFlow<ProgressUiState> =
        _uiState.asStateFlow()

    init {
        loadPersonalRecords()
        loadWorkoutVolumeHistory()
        loadCompletedWorkoutCount()
        observeFavoriteExercises()
    }

    fun openExerciseSelector() {
        _uiState.value =
            _uiState.value.copy(
                isExerciseSelectorOpen = true
            )
    }

    fun closeExerciseSelector() {
        _uiState.value =
            _uiState.value.copy(
                isExerciseSelectorOpen = false,
                exerciseSearchQuery = ""
            )
    }
    fun updateExerciseSearchQuery(
        query: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                exerciseSearchQuery = query
            )
    }

    fun toggleFavoriteExercise(
        exerciseName: String
    ) {
        viewModelScope.launch {

            val isFavorite =
                _uiState.value
                    .favoriteExercises
                    .contains(exerciseName)

            if (isFavorite) {
                workoutSessionRepository
                    .removeFavoriteExercise(
                        exerciseName
                    )
            } else {
                workoutSessionRepository
                    .addFavoriteExercise(
                        exerciseName
                    )
            }
        }
    }
    fun loadPersonalRecords() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true
                )

            val records =
                workoutSessionRepository
                    .getPersonalRecords()

            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    personalRecords = records
                )
        }
    }

    fun loadWorkoutVolumeHistory() {

        viewModelScope.launch {

            val zoneId =
                ZoneId.systemDefault()

            val today =
                LocalDate.now()

            val startDate =
                today.minusDays(29)

            val startTime =
                startDate
                    .atStartOfDay(zoneId)
                    .toInstant()
                    .toEpochMilli()

            val endTime =
                today
                    .plusDays(1)
                    .atStartOfDay(zoneId)
                    .toInstant()
                    .toEpochMilli()

            val volumeHistory =
                workoutSessionRepository
                    .getWorkoutVolumeHistory(
                        startTime = startTime,
                        endTime = endTime
                    )

            _uiState.value =
                _uiState.value.copy(
                    workoutVolumeHistory =
                        volumeHistory
                )
        }
    }

    fun loadCompletedWorkoutCount() {
        viewModelScope.launch {

            val zoneId =
                ZoneId.systemDefault()

            val today =
                LocalDate.now()

            val startDate =
                today.minusDays(29)

            val startTime =
                startDate
                    .atStartOfDay(zoneId)
                    .toInstant()
                    .toEpochMilli()

            val endTime =
                today
                    .plusDays(1)
                    .atStartOfDay(zoneId)
                    .toInstant()
                    .toEpochMilli()

            val workoutCount =
                workoutSessionRepository
                    .getCompletedWorkoutCount(
                        startTime = startTime,
                        endTime = endTime
                    )

            _uiState.value =
                _uiState.value.copy(
                    completedWorkoutCount = workoutCount
                )
        }
    }

    fun loadStrengthHistory(
        exerciseName: String
    ) {
        viewModelScope.launch {

            val zoneId =
                ZoneId.systemDefault()

            val today =
                LocalDate.now()

            val startDate =
                today.minusDays(89)

            val startTime =
                startDate
                    .atStartOfDay(zoneId)
                    .toInstant()
                    .toEpochMilli()

            val endTime =
                today
                    .plusDays(1)
                    .atStartOfDay(zoneId)
                    .toInstant()
                    .toEpochMilli()

            val history =
                workoutSessionRepository
                    .getExerciseStrengthHistory(
                        exerciseName = exerciseName,
                        startTime = startTime,
                        endTime = endTime
                    )

            _uiState.value =
                _uiState.value.copy(
                    selectedStrengthExercise = exerciseName,
                    strengthHistory = history,
                    isExerciseSelectorOpen = false,
                    exerciseSearchQuery = ""
                )
        }
    }

    private fun observeFavoriteExercises() {

        viewModelScope.launch {

            workoutSessionRepository
                .observeFavoriteExercises()
                .collect { favorites ->

                    _uiState.value =
                        _uiState.value.copy(
                            favoriteExercises =
                                favorites
                                    .map { it.exerciseName }
                                    .toSet()
                        )
                }
        }
    }
}