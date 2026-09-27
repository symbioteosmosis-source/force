package com.ngedo.force.feature.workout.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun WorkoutDaysSetup(
    planName: String,
    workoutDays: List<PlanWorkoutDay>,
    isSaving: Boolean,
    onBack: () -> Unit,
    onAddExercises: (PlanWorkoutDay) -> Unit,
    onSavePlan: () -> Unit
){

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ForceColors.Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Text(
                text =
                    if (planName.isBlank()) {
                        "BUILD YOUR PLAN"
                    } else {
                        planName.uppercase()
                    },
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )


            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Add exercises to each workout day.",
                color = ForceColors.TextSecondary
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            OutlinedButton(
                onClick = onBack
            ) {
                Text("BACK")
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {

                items(
                    items = workoutDays,
                    key = { it.day }
                ) { workoutDay ->

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = ForceColors.Surface,
                        shape = MaterialTheme.shapes.large
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text =
                                    workoutDay.name.uppercase(),
                                color = ForceColors.TextPrimary,
                                style =
                                    MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            Text(
                                text =
                                    if (
                                        workoutDay.exercises.isEmpty()
                                    ) {
                                        "No exercises yet"
                                    } else {
                                        "${workoutDay.exercises.size} exercises"
                                    },
                                color = ForceColors.TextSecondary
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            OutlinedButton(
                                onClick = {
                                    onAddExercises(
                                        workoutDay
                                    )
                                },
                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {
                                Text("+ ADD EXERCISES")
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = onSavePlan,

                enabled =
                    !isSaving &&
                            workoutDays.isNotEmpty(),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            ForceColors.Primary,
                        contentColor =
                            ForceColors.Background
                    )
            ) {

                Text(
                    text = "SAVE PLAN",
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}