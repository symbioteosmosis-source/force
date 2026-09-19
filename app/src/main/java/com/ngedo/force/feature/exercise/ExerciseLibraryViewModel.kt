package com.ngedo.force.feature.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngedo.force.data.local.entity.ExerciseEntity
import com.ngedo.force.data.local.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ngedo.force.data.local.repository.WorkoutSessionRepository

data class ExerciseLibraryUiState(
    val exercises: List<ExerciseEntity> = emptyList(),
    val favoriteExercises: Set<String> = emptySet(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val favoriteMessage: String? = null
)

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            ExerciseLibraryUiState()
        )

    val uiState: StateFlow<ExerciseLibraryUiState> =
        _uiState.asStateFlow()

    init {

        viewModelScope.launch {

            exerciseRepository.seedExercisesIfNeeded()

            observeExercises()
        }

        observeFavorites()
    }

    private suspend fun observeExercises() {

        exerciseRepository
            .observeAllExercises()
            .collectLatest { exercises ->

                _uiState.value =
                    _uiState.value.copy(
                        exercises = exercises,
                        isLoading = false
                    )
            }
    }

    private fun observeFavorites() {

        viewModelScope.launch {

            workoutSessionRepository
                .observeFavoriteExercises()
                .collectLatest { favorites ->

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

                _uiState.value =
                    _uiState.value.copy(
                        favoriteMessage =
                            "$exerciseName removed from Favorites"
                    )

            } else {

                workoutSessionRepository
                    .addFavoriteExercise(
                        exerciseName
                    )

                _uiState.value =
                    _uiState.value.copy(
                        favoriteMessage =
                            "$exerciseName added to Favorites"
                    )
            }
        }
    }

    fun clearFavoriteMessage() {

        _uiState.value =
            _uiState.value.copy(
                favoriteMessage = null
            )
    }
    fun updateSearchQuery(
        query: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                searchQuery = query
            )
    }

}