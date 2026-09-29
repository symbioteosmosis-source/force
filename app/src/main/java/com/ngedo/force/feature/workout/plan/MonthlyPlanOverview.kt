package com.ngedo.force.feature.workout.plan

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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ngedo.force.designsystem.ForceColors
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MonthlyPlanOverview(
    planName: String,
    goal: String,
    workoutDays: List<PlanWorkoutDay>,

    selectedWeek: Int,
    totalWeeks: Int,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,

    todayWorkout: TodayPlannedWorkout?,
    onBack: () -> Unit,
    onEditPlan: () -> Unit,
    onViewTodayWorkout: () -> Unit,
    onStartWorkout: (Long) -> Unit,
    onOpenDay: (PlanWorkoutDay) -> Unit
){

    val scheduleDateFormatter =
        SimpleDateFormat(
            "EEE, dd MMM yyyy",
            Locale.getDefault()
        )



    val weekRangeFormatter =
        SimpleDateFormat(
            "dd MMM",
            Locale.getDefault()
        )

    val selectedWeekDates =
        workoutDays
            .filter {
                it.weekNumber == selectedWeek
            }
            .mapNotNull {
                it.scheduledDate
            }

    val selectedWeekRange =
        if (selectedWeekDates.isNotEmpty()) {

            val calendar =
                java.util.Calendar.getInstance().apply {
                    timeInMillis =
                        selectedWeekDates.minOrNull()!!
                }

            val dayOfWeek =
                calendar.get(
                    java.util.Calendar.DAY_OF_WEEK
                )

            val daysFromMonday =
                when (dayOfWeek) {
                    java.util.Calendar.SUNDAY -> 6
                    else ->
                        dayOfWeek -
                                java.util.Calendar.MONDAY
                }

            calendar.add(
                java.util.Calendar.DAY_OF_MONTH,
                -daysFromMonday
            )

            val weekStart =
                calendar.timeInMillis

            calendar.add(
                java.util.Calendar.DAY_OF_MONTH,
                6
            )

            val weekEnd =
                calendar.timeInMillis

            "${weekRangeFormatter.format(weekStart)} – " +
                    weekRangeFormatter.format(weekEnd)

        } else {
            null
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ForceColors.Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = planName.uppercase(),
                    color = ForceColors.TextPrimary,
                    style =
                        MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = onBack
                ) {
                    Text("BACK")
                }
            }

            if (goal.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = goal,
                    color = ForceColors.TextSecondary,
                    style =
                        MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            todayWorkout?.let { workout ->

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ForceColors.Surface,
                    shape =
                        MaterialTheme.shapes.large
                ) {

                    Column(
                        modifier =
                            Modifier.padding(14.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            text = "TODAY'S WORKOUT",
                            color = ForceColors.Primary,
                            style =
                                MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = workout.workoutName,
                            color = ForceColors.TextPrimary,
                            style =
                                MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text =
                                "${workout.exerciseCount} exercises",
                            color =
                                ForceColors.TextSecondary
                        )

                        when {

                            workout.isCompleted -> {

                                Text(
                                    text = "✓ COMPLETED",
                                    color = ForceColors.Primary,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }

                            workout.exerciseCount > 0 -> {

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth(),

                                    horizontalArrangement =
                                        Arrangement.spacedBy(10.dp)
                                ) {

                                    OutlinedButton(
                                        onClick =
                                            onViewTodayWorkout,

                                        modifier =
                                            Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                    ) {

                                        Text(
                                            text = "VIEW",
                                            color =
                                                ForceColors.TextPrimary,
                                            fontWeight =
                                                FontWeight.Bold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            onStartWorkout(
                                                workout.plannedWorkoutId
                                            )
                                        },

                                        modifier =
                                            Modifier
                                                .weight(1f)
                                                .height(44.dp),

                                        colors =
                                            androidx.compose.material3
                                                .ButtonDefaults
                                                .buttonColors(
                                                    containerColor =
                                                        ForceColors.Primary
                                                )
                                    ) {

                                        Text(
                                            text = "START",
                                            color =
                                                ForceColors.Background,
                                            fontWeight =
                                                FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            else -> {

                                Text(
                                    text =
                                        "NOT PROGRAMMED YET",
                                    color =
                                        ForceColors.TextSecondary,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                OutlinedButton(
                    onClick = onPreviousWeek,
                    enabled = selectedWeek > 1
                ) {
                    Text("‹")
                }

                Column(
                    horizontalAlignment =
                        androidx.compose.ui.Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            "WEEK $selectedWeek OF $totalWeeks",
                        color =
                            ForceColors.TextPrimary,
                        fontWeight =
                            FontWeight.Bold
                    )

                    selectedWeekRange?.let { range ->

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = range.uppercase(),
                            color = ForceColors.Primary,
                            style =
                                MaterialTheme.typography.labelMedium,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                OutlinedButton(
                    onClick = onNextWeek,
                    enabled =
                        selectedWeek < totalWeeks
                ) {
                    Text("›")
                }
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Text(
                text = "YOUR SCHEDULE",
                color = ForceColors.TextSecondary,
                style =
                    MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items =
                        workoutDays.filter {
                            it.weekNumber ==
                                    selectedWeek
                        },

                    key = { workoutDay ->
                        workoutDay.plannedWorkoutId
                            ?: "${workoutDay.weekNumber}-${workoutDay.day}"
                    }
                ){ workoutDay ->

                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        color = ForceColors.Surface,
                        shape =
                            MaterialTheme.shapes.large
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(16.dp)
                        ) {

                            Column {

                                workoutDay.scheduledDate?.let { scheduledDate ->

                                    Text(
                                        text =
                                            scheduleDateFormatter
                                                .format(scheduledDate)
                                                .uppercase(),
                                        color = ForceColors.Primary,
                                        style =
                                            MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(
                                        modifier = Modifier.height(4.dp)
                                    )
                                }

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth(),

                                    horizontalArrangement =
                                        Arrangement.SpaceBetween
                                ) {

                                    Text(
                                        text =
                                            workoutDay.name.uppercase(),
                                        color =
                                            ForceColors.TextPrimary,
                                        style =
                                            MaterialTheme.typography.titleMedium,
                                        fontWeight =
                                            FontWeight.Bold
                                    )

                                    if (workoutDay.isCompleted) {

                                        Text(
                                            text = "✓ COMPLETED",
                                            color = ForceColors.Success,
                                            style =
                                                MaterialTheme.typography.labelMedium,
                                            fontWeight =
                                                FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(6.dp)
                            )

                            Text(
                                text =
                                    if (
                                        workoutDay.exercises.isEmpty()
                                    ) {
                                        "Not programmed yet"
                                    } else {
                                        "${workoutDay.exercises.size} exercises"
                                    },
                                color =
                                    ForceColors.TextSecondary
                            )

                            if (
                                workoutDay.exercises.isNotEmpty()
                            ) {

                                Spacer(
                                    modifier =
                                        Modifier.height(6.dp)
                                )

                                Text(
                                    text =
                                        workoutDay.exercises
                                            .take(3)
                                            .joinToString(" • ") {
                                                it.exerciseName
                                            },
                                    color =
                                        ForceColors.TextSecondary,
                                    style =
                                        MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )

                            OutlinedButton(
                                onClick = {
                                    onOpenDay(
                                        workoutDay
                                    )
                                },
                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {

                                Text("VIEW")
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedButton(
                onClick = onEditPlan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {

                Text(
                    text = "EDIT PLAN",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}