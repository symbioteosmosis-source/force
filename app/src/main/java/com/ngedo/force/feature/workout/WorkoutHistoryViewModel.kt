package com.ngedo.force.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngedo.force.data.local.entity.WorkoutSessionEntity
import com.ngedo.force.data.local.repository.WorkoutSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.YearMonth

@HiltViewModel
class WorkoutHistoryViewModel @Inject constructor(
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    /*
     * =========================================================
     * SELECTED DATE
     * =========================================================
     */

    private val _selectedDate =
        MutableStateFlow(
            LocalDate.now()
        )

    val selectedDate:
            StateFlow<LocalDate> =
        _selectedDate.asStateFlow()


    /*
     * =========================================================
     * COMPLETED SESSIONS FOR SELECTED DATE
     * =========================================================
     */

    private val _completedSessions =
        MutableStateFlow<
                List<WorkoutSessionEntity>
                >(
            emptyList()
        )
    private val _workoutDates =
        MutableStateFlow<Set<LocalDate>>(
            emptySet()
        )
    private val _sessionPendingDelete =
        MutableStateFlow<WorkoutSessionEntity?>(
            null
        )

    val sessionPendingDelete:
            StateFlow<WorkoutSessionEntity?> =
        _sessionPendingDelete.asStateFlow()

    val workoutDates:
            StateFlow<Set<LocalDate>> =
        _workoutDates.asStateFlow()

    private var workoutDatesJob:
            Job? = null

    val completedSessions:
            StateFlow<
                    List<WorkoutSessionEntity>
                    > =
        _completedSessions.asStateFlow()


    /*
     * =========================================================
     * DATE OBSERVATION
     * =========================================================
     */

    private var sessionHistoryJob:
            Job? = null


    init {
        observeSelectedDate()
        observeWorkoutDates()
    }


    /*
     * =========================================================
     * PREVIOUS DAY
     * =========================================================
     */

    fun previousDay() {

        _selectedDate.value =
            _selectedDate.value.minusDays(1)
    }


    /*
     * =========================================================
     * NEXT DAY
     * =========================================================
     */

    fun nextDay() {

        val nextDate =
            _selectedDate.value.plusDays(1)

        /*
         * Do not navigate into future dates.
         */
        if (
            !nextDate.isAfter(
                LocalDate.now()
            )
        ) {

            _selectedDate.value =
                nextDate
        }
    }


    /*
     * =========================================================
     * SELECT DATE
     * =========================================================
     *
     * Later the calendar picker will use this.
     */

    fun selectDate(
        date: LocalDate
    ) {

        if (
            !date.isAfter(
                LocalDate.now()
            )
        ) {

            _selectedDate.value =
                date
        }
    }


    /*
     * =========================================================
     * LOAD HISTORY FOR SELECTED DATE
     * =========================================================
     */

    private fun observeSelectedDate() {

        viewModelScope.launch {

            _selectedDate.collectLatest { date ->

                loadSessionsForDate(
                    date = date
                )
            }
        }
    }


    private fun loadSessionsForDate(
        date: LocalDate
    ) {

        sessionHistoryJob?.cancel()

        sessionHistoryJob =
            viewModelScope.launch {

                val zoneId =
                    ZoneId.systemDefault()

                val startTime =
                    date
                        .atStartOfDay(
                            zoneId
                        )
                        .toInstant()
                        .toEpochMilli()

                val endTime =
                    date
                        .plusDays(1)
                        .atStartOfDay(
                            zoneId
                        )
                        .toInstant()
                        .toEpochMilli()

                workoutSessionRepository
                    .getCompletedSessionsForDate(
                        startTime = startTime,
                        endTime = endTime
                    )
                    .collectLatest { sessions ->

                        _completedSessions.value =
                            sessions
                    }
            }
    }

    private fun observeWorkoutDates() {

        viewModelScope.launch {

            _selectedDate.collectLatest { date ->

                loadWorkoutDatesForMonth(
                    month =
                        YearMonth.from(date)
                )
            }
        }
    }


    fun loadWorkoutDatesForMonth(
        month: java.time.YearMonth
    ) {

        workoutDatesJob?.cancel()

        workoutDatesJob =
            viewModelScope.launch {

                val zoneId =
                    ZoneId.systemDefault()

                val firstDayOfMonth =
                    month.atDay(1)

                val firstDayOfNextMonth =
                    month
                        .plusMonths(1)
                        .atDay(1)

                val startTime =
                    firstDayOfMonth
                        .atStartOfDay(zoneId)
                        .toInstant()
                        .toEpochMilli()

                val endTime =
                    firstDayOfNextMonth
                        .atStartOfDay(zoneId)
                        .toInstant()
                        .toEpochMilli()

                workoutSessionRepository
                    .getCompletedWorkoutDates(
                        startTime = startTime,
                        endTime = endTime
                    )
                    .collectLatest { timestamps ->

                        _workoutDates.value =
                            timestamps
                                .map { timestamp ->

                                    Instant
                                        .ofEpochMilli(timestamp)
                                        .atZone(zoneId)
                                        .toLocalDate()
                                }
                                .toSet()
                    }
            }
    }

    fun requestDeleteSession(
        session: WorkoutSessionEntity
    ) {

        _sessionPendingDelete.value =
            session
    }


    fun cancelDeleteSession() {

        _sessionPendingDelete.value =
            null
    }


    fun confirmDeleteSession() {

        val session =
            _sessionPendingDelete.value
                ?: return

        viewModelScope.launch {

            workoutSessionRepository
                .deleteWorkoutSession(
                    sessionId = session.id
                )

            _sessionPendingDelete.value =
                null
        }
    }
}