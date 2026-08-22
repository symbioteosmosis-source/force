package com.ngedo.force.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngedo.force.data.local.model.ExercisePersonalRecord
import com.ngedo.force.data.local.repository.WorkoutSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProgressUiState(
    val isLoading: Boolean = false,
    val personalRecords: List<ExercisePersonalRecord> = emptyList()
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
                ProgressUiState(
                    isLoading = false,
                    personalRecords = records
                )
        }
    }
}