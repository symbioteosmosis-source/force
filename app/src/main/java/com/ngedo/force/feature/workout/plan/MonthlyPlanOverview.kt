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

@Composable
fun MonthlyPlanOverview(
    planName: String,
    goal: String,
    workoutDays: List<PlanWorkoutDay>,
    todayWorkout: TodayPlannedWorkout?,
    onBack: () -> Unit,
    onEditPlan: () -> Unit,
    onViewTodayWorkout: () -> Unit,
    onStartWorkout: (Long) -> Unit,
    onOpenDay: (PlanWorkoutDay) -> Unit
) {

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
                            Modifier.padding(18.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
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

                                OutlinedButton(
                                    onClick = onViewTodayWorkout,
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                ) {

                                    Text(
                                        text = "VIEW WORKOUT",
                                        color = ForceColors.TextPrimary,
                                        fontWeight = FontWeight.Bold
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
                                            .fillMaxWidth()
                                            .height(50.dp),
                                    colors =
                                        androidx.compose.material3.ButtonDefaults
                                            .buttonColors(
                                                containerColor =
                                                    ForceColors.Primary
                                            )
                                ) {

                                    Text(
                                        text = "START WORKOUT",
                                        color = ForceColors.Background,
                                        fontWeight = FontWeight.Bold
                                    )
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
                    items = workoutDays,
                    key = { it.day }
                ) { workoutDay ->

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