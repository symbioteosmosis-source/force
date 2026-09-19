package com.ngedo.force.feature.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngedo.force.data.local.entity.ExerciseEntity
import com.ngedo.force.data.local.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ngedo.force.data.local.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.collect

data class ExerciseDetailUiState(
    val exercise: ExerciseEntity? = null,
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false
)

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val exerciseRepository: ExerciseRepository,
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {
    private val exerciseId: String =
        savedStateHandle["exerciseId"] ?: ""

    private val _uiState =
        MutableStateFlow(
            ExerciseDetailUiState()
        )

    val uiState: StateFlow<ExerciseDetailUiState> =
        _uiState.asStateFlow()

    init {
        loadExercise()
        observeFavoriteState()
    }

    private fun loadExercise() {

        viewModelScope.launch {

            val exercise =
                exerciseRepository.getExerciseById(
                    exerciseId
                )

            _uiState.value =
                ExerciseDetailUiState(
                    exercise = exercise,
                    isLoading = false
                )
        }
    }

    private fun observeFavoriteState() {

        viewModelScope.launch {

            workoutSessionRepository
                .observeFavoriteExercises()
                .collect { favorites ->

                    val exerciseName =
                        _uiState.value
                            .exercise
                            ?.name

                    if (exerciseName != null) {

                        _uiState.value =
                            _uiState.value.copy(
                                isFavorite =
                                    favorites.any {
                                        it.exerciseName == exerciseName
                                    }
                            )
                    }
                }
        }
    }

    fun toggleFavorite() {

        val exerciseName =
            _uiState.value
                .exercise
                ?.name
                ?: return

        val isFavorite =
            _uiState.value.isFavorite

        viewModelScope.launch {

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
}