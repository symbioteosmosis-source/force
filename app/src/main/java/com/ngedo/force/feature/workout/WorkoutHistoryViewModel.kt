package com.ngedo.force.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngedo.force.data.local.entity.WorkoutSessionEntity
import com.ngedo.force.data.local.repository.WorkoutSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class WorkoutHistoryViewModel @Inject constructor(
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    val completedSessions: StateFlow<List<WorkoutSessionEntity>> =
        workoutSessionRepository
            .getAllSessions()
            .map { sessions ->

                sessions.filter { session ->
                    session.isCompleted
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = 5_000
                ),
                initialValue = emptyList()
            )
}