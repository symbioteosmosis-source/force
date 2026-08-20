package com.ngedo.force.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngedo.force.data.local.entity.WorkoutSetEntity
import com.ngedo.force.data.local.repository.WorkoutSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


data class WorkoutHistoryExerciseUi(
    val exerciseId: Long,
    val exerciseName: String,
    val sets: List<WorkoutSetEntity>
)


data class WorkoutHistoryDetailUiState(
    val isLoading: Boolean = false,
    val exercises: List<WorkoutHistoryExerciseUi> = emptyList()
)


@HiltViewModel
class WorkoutHistoryDetailViewModel @Inject constructor(
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            WorkoutHistoryDetailUiState()
        )

    val uiState: StateFlow<WorkoutHistoryDetailUiState> =
        _uiState.asStateFlow()


    fun loadSession(
        sessionId: Long
    ) {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true
                )

            val exercises =
                workoutSessionRepository
                    .getExercisesForSession(
                        sessionId
                    )
                    .first()

            val exerciseDetails =
                exercises.map { exercise ->

                    val sets =
                        workoutSessionRepository
                            .getSetsForExerciseOnce(
                                exercise.id
                            )

                    WorkoutHistoryExerciseUi(
                        exerciseId = exercise.id,
                        exerciseName = exercise.exerciseName,
                        sets = sets
                    )
                }

            _uiState.value =
                WorkoutHistoryDetailUiState(
                    isLoading = false,
                    exercises = exerciseDetails
                )
        }
    }
}