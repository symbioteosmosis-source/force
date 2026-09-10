package com.ngedo.force.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ngedo.force.data.local.entity.WorkoutSessionEntity
import com.ngedo.force.designsystem.ForceColors
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.requiredSize


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.style.TextAlign
import java.time.YearMonth

@Composable
fun WorkoutHistoryScreen(
    onSessionClick: (Long) -> Unit,
    viewModel: WorkoutHistoryViewModel = hiltViewModel()
) {

    val sessions by
    viewModel.completedSessions.collectAsState()

    val selectedDate by
    viewModel.selectedDate.collectAsState()

    val sessionPendingDelete by
    viewModel.sessionPendingDelete.collectAsState()

    val workoutDates by
    viewModel.workoutDates.collectAsState()

    var showCalendar by
    remember {
        mutableStateOf(false)
    }

    var calendarMonth by
    remember(selectedDate) {
        mutableStateOf(
            YearMonth.from(selectedDate)
        )
    }

    val today =
        LocalDate.now()

    val canGoForward =
        selectedDate.isBefore(today)

    if (showCalendar) {

        ForceWorkoutCalendarDialog(
            selectedDate = selectedDate,
            displayedMonth = calendarMonth,
            workoutDates = workoutDates,

            onPreviousMonth = {

                val previousMonth =
                    calendarMonth.minusMonths(1)

                calendarMonth =
                    previousMonth

                viewModel
                    .loadWorkoutDatesForMonth(
                        previousMonth
                    )
            },

            onNextMonth = {

                val nextMonth =
                    calendarMonth.plusMonths(1)

                if (
                    !nextMonth.isAfter(
                        YearMonth.now()
                    )
                ) {

                    calendarMonth =
                        nextMonth

                    viewModel
                        .loadWorkoutDatesForMonth(
                            nextMonth
                        )
                }
            },

            onDateSelected = { date ->

                viewModel.selectDate(date)

                showCalendar = false
            },

            onDismiss = {
                showCalendar = false
            }
        )
    }

    if (sessionPendingDelete != null) {

        AlertDialog(
            onDismissRequest = {
                viewModel.cancelDeleteSession()
            },

            title = {
                Text(
                    text = "Delete Workout?",
                    color = ForceColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    text =
                        "This workout and all of its exercises and sets will be permanently removed from your history.",
                    color = ForceColors.TextSecondary
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        viewModel.confirmDeleteSession()
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        viewModel.cancelDeleteSession()
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = ForceColors.TextSecondary
                    )
                }
            },

            containerColor = ForceColors.Surface
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ForceColors.Background
            )
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        Text(
            text = "Workout History",
            color = ForceColors.TextPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )


        /*
         * =====================================================
         * DATE NAVIGATION
         * =====================================================
         */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ForceColors.Surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(
                    horizontal = 8.dp,
                    vertical = 6.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = {
                    viewModel.previousDay()
                }
            ) {

                Icon(
                    imageVector =
                        Icons.Outlined.ChevronLeft,
                    contentDescription =
                        "Previous day",
                    tint =
                        ForceColors.TextPrimary
                )
            }


            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text =
                        formatSelectedDate(
                            selectedDate
                        ),
                    color =
                        ForceColors.TextPrimary,
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                TextButton(
                    onClick = {

                        calendarMonth =
                            YearMonth.from(
                                selectedDate
                            )

                        viewModel
                            .loadWorkoutDatesForMonth(
                                calendarMonth
                            )

                        showCalendar = true
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined.CalendarMonth,
                        contentDescription =
                            "Open calendar",
                        tint =
                            ForceColors.Primary
                    )

                    Text(
                        text = " Calendar",
                        color =
                            ForceColors.Primary,
                        style =
                            MaterialTheme.typography.labelMedium
                    )
                }
            }


            IconButton(
                onClick = {
                    viewModel.nextDay()
                },
                enabled = canGoForward
            ) {

                Icon(
                    imageVector =
                        Icons.Outlined.ChevronRight,
                    contentDescription =
                        "Next day",
                    tint =
                        if (canGoForward) {
                            ForceColors.TextPrimary
                        } else {
                            ForceColors.TextSecondary.copy(
                                alpha = 0.35f
                            )
                        }
                )
            }
        }


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        /*
         * =====================================================
         * SELECTED DAY HISTORY
         * =====================================================
         */

        if (sessions.isEmpty()) {

            Text(
                text =
                    if (selectedDate == today) {
                        "No completed workouts today."
                    } else {
                        "No completed workouts on this day."
                    },
                color =
                    ForceColors.TextSecondary
            )

        } else {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = sessions,
                    key = { session ->
                        session.id
                    }
                ) { session ->

                    WorkoutHistoryCard(
                        session = session,

                        onClick = {
                            onSessionClick(
                                session.id
                            )
                        },

                        onDelete = {
                            viewModel.requestDeleteSession(
                                session
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun WorkoutHistoryCard(
    session: WorkoutSessionEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = ForceColors.Surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
        ) {

            Text(
                text =
                    formatWorkoutDate(
                        session.startedAt
                    ),
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    formatWorkoutDuration(
                        session.durationSeconds
                    ),
                color = ForceColors.Primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Completed",
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            TextButton(
                onClick = onDelete
            ) {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ForceWorkoutCalendarDialog(
    selectedDate: LocalDate,
    displayedMonth: YearMonth,
    workoutDates: Set<LocalDate>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {

    val today =
        LocalDate.now()

    val daysInMonth =
        displayedMonth.lengthOfMonth()

    val firstDay =
        displayedMonth.atDay(1)

    /*
     * Monday = 0
     * Tuesday = 1
     * ...
     * Sunday = 6
     */
    val leadingEmptyDays =
        firstDay.dayOfWeek.value - 1

    val calendarCells =
        List<LocalDate?>(
            leadingEmptyDays
        ) {
            null
        } +
                (1..daysInMonth).map { day ->
                    displayedMonth.atDay(day)
                }
    val calendarRowCount = (calendarCells.size + 6) / 7

    val canGoNextMonth =
        displayedMonth.isBefore(
            YearMonth.now()
        )

    AlertDialog(
        onDismissRequest = onDismiss,

        confirmButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Close",
                    color = ForceColors.Primary
                )
            }
        },

        title = {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                IconButton(
                    onClick =
                        onPreviousMonth
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined.ChevronLeft,
                        contentDescription =
                            "Previous month",
                        tint =
                            ForceColors.TextPrimary
                    )
                }

                Text(
                    text =
                        displayedMonth.format(
                            DateTimeFormatter.ofPattern(
                                "MMMM yyyy",
                                Locale.getDefault()
                            )
                        ),
                    color =
                        ForceColors.TextPrimary,
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                IconButton(
                    onClick =
                        onNextMonth,
                    enabled =
                        canGoNextMonth
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined.ChevronRight,
                        contentDescription =
                            "Next month",
                        tint =
                            if (canGoNextMonth) {
                                ForceColors.TextPrimary
                            } else {
                                ForceColors.TextSecondary
                                    .copy(alpha = 0.3f)
                            }
                    )
                }
            }
        },

        text = {

            Column(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                /*
                 * Week headings
                 */

                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    listOf(
                        "M",
                        "T",
                        "W",
                        "T",
                        "F",
                        "S",
                        "S"
                    ).forEach { day ->

                        Text(
                            text = day,
                            modifier =
                                Modifier.weight(1f),
                            color =
                                ForceColors.TextSecondary,
                            style =
                                MaterialTheme.typography.labelMedium,
                            textAlign =
                                TextAlign.Center
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                LazyVerticalGrid(
                    columns =
                        GridCells.Fixed(7),
                    modifier =
                        Modifier.height(
                            (calendarRowCount * 46).dp
                        ),
                    userScrollEnabled =
                        false
                ) {

                    items(
                        items = calendarCells
                    ) { date ->

                        if (date == null) {

                            Spacer(
                                modifier =
                                    Modifier.size(40.dp)
                            )

                        } else {

                            WorkoutCalendarDay(
                                date = date,
                                isSelected =
                                    date == selectedDate,
                                hasWorkout =
                                    date in workoutDates,
                                enabled =
                                    !date.isAfter(today),
                                onClick = {
                                    onDateSelected(date)
                                }
                            )
                        }
                    }
                }
            }
        },

        containerColor =
            ForceColors.Surface
    )
}

@Composable
private fun WorkoutCalendarDay(
    date: LocalDate,
    isSelected: Boolean,
    hasWorkout: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {

    /*
     * The outer Box belongs to the grid cell.
     * It may be wider than the date circle.
     */
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        contentAlignment = Alignment.Center
    ) {

        /*
         * requiredSize prevents LazyVerticalGrid
         * from stretching the date indicator.
         */
        Box(
            modifier = Modifier
                .requiredSize(38.dp)
                .background(
                    color =
                        when {

                            isSelected ->
                                ForceColors.Primary

                            hasWorkout ->
                                ForceColors.Primary.copy(
                                    alpha = 0.18f
                                )

                            else ->
                                ForceColors.Background
                        },
                    shape = CircleShape
                )
                .clickable(
                    enabled = enabled
                ) {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = date.dayOfMonth.toString(),
                color =
                    when {

                        !enabled ->
                            ForceColors.TextSecondary.copy(
                                alpha = 0.3f
                            )

                        isSelected ->
                            ForceColors.Background

                        hasWorkout ->
                            ForceColors.Primary

                        else ->
                            ForceColors.TextPrimary
                    },
                style =
                    MaterialTheme.typography.bodyMedium,
                fontWeight =
                    if (
                        isSelected ||
                        hasWorkout
                    ) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatSelectedDate(
    date: LocalDate
): String {

    val formatter =
        DateTimeFormatter.ofPattern(
            "dd MMMM yyyy",
            Locale.getDefault()
        )

    return date.format(
        formatter
    )
}


private fun formatWorkoutDate(
    timestamp: Long
): String {

    val formatter =
        SimpleDateFormat(
            "dd MMM yyyy • HH:mm",
            Locale.getDefault()
        )

    return formatter.format(
        Date(timestamp)
    )
}


private fun formatWorkoutDuration(
    seconds: Long
): String {

    val minutes =
        seconds / 60

    val remainingSeconds =
        seconds % 60

    return if (minutes > 0) {

        "$minutes min $remainingSeconds sec"

    } else {

        "$remainingSeconds sec"
    }
}