package com.ngedo.force.feature.workout

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ngedo.force.designsystem.ForceColors
import com.ngedo.force.designsystem.ForceSpacing

private data class WorkoutExercise(
    val name: String,
    val target: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int
)

private val todaysWorkout = listOf(
    WorkoutExercise(
        name = "Barbell Bench Press",
        target = "Chest",
        sets = 4,
        reps = "8–10 reps",
        restSeconds = 90
    ),
    WorkoutExercise(
        name = "Incline Dumbbell Press",
        target = "Upper Chest",
        sets = 3,
        reps = "10–12 reps",
        restSeconds = 75
    ),
    WorkoutExercise(
        name = "Cable Fly",
        target = "Chest",
        sets = 3,
        reps = "12–15 reps",
        restSeconds = 60
    ),
    WorkoutExercise(
        name = "Tricep Pushdown",
        target = "Triceps",
        sets = 3,
        reps = "10–12 reps",
        restSeconds = 60
    )
)

@Composable
fun WorkoutScreen(
    workoutViewModel: ActiveWorkoutViewModel = viewModel()
) {
    val uiState by workoutViewModel.uiState.collectAsState()

    when {
        uiState.isWorkoutComplete -> {
            WorkoutCompleteContent(
                uiState = uiState,
                onFinishWorkout = {
                    workoutViewModel.finishWorkout()
                }
            )
        }

        uiState.isWorkoutStarted -> {
            ActiveWorkoutContent(
                uiState = uiState,

                onWeightChange = {
                    workoutViewModel.updateCurrentWeight(it)
                },

                onRepsChange = {
                    workoutViewModel.updateCurrentReps(it)
                },

                onCompleteSet = {
                    val exercise =
                        todaysWorkout[uiState.currentExerciseIndex]

                    workoutViewModel.completeSet(
                        totalSets = exercise.sets,
                        totalExercises = todaysWorkout.size,
                        restSeconds = exercise.restSeconds
                    )
                },
                onSkipRest = {
                    val exercise =
                        todaysWorkout[uiState.currentExerciseIndex]

                    workoutViewModel.skipRest(
                        totalSets = exercise.sets,
                        totalExercises = todaysWorkout.size
                    )
                },
                onNextExercise = {
                    workoutViewModel.nextExercise(
                        todaysWorkout.size
                    )
                }
            )
        }

        else -> {
            WorkoutOverview(
                onStartWorkout = {
                    workoutViewModel.startWorkout(
                        exerciseNames = todaysWorkout.map {
                            it.name
                        }
                    )
                }
            )
        }
    }
}     // closes WorkoutScreen
@Composable
private fun WorkoutOverview(
    onStartWorkout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForceColors.Background)
            .padding(ForceSpacing.Large),
        verticalArrangement = Arrangement.spacedBy(
            ForceSpacing.Large
        )
    ) {

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Today's Workout",
                    color = ForceColors.TextPrimary,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Chest & Triceps",
                    color = ForceColors.Primary,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "4 exercises • ~45 minutes",
                    color = ForceColors.TextSecondary
                )
            }
        }

        items(todaysWorkout) { exercise ->
            WorkoutExerciseCard(exercise)
        }

        item {
            Button(
                onClick = onStartWorkout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForceColors.Primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Start Workout",
                    color = ForceColors.Background,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ActiveWorkoutContent(
    uiState: ActiveWorkoutUiState,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onCompleteSet: () -> Unit,
    onSkipRest: () -> Unit,
    onNextExercise: () -> Unit
) {
    val exercise =
        todaysWorkout[uiState.currentExerciseIndex]

    val workoutProgress =
        calculateWorkoutProgress(
            uiState.completedSets
        )

    val totalSets =
        todaysWorkout.sumOf { it.sets }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForceColors.Background)
            .padding(ForceSpacing.Large)
    ) {

        /*
         * -------------------------------------------------
         * TOP WORKOUT INFORMATION
         * -------------------------------------------------
         */

        Text(
            text = "Active Workout",
            color = ForceColors.TextPrimary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "Exercise " +
                        "${uiState.currentExerciseIndex + 1} " +
                        "of ${todaysWorkout.size}",
            color = ForceColors.TextSecondary
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        /*
         * -------------------------------------------------
         * WORKOUT PROGRESS
         * -------------------------------------------------
         */

        Text(
            text = "Workout Progress",
            color = ForceColors.TextSecondary,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        LinearProgressIndicator(
            progress = { workoutProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = ForceColors.Primary,
            trackColor = ForceColors.Surface
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text =
                "${uiState.completedSets} of " +
                        "$totalSets sets • " +
                        "${(workoutProgress * 100).toInt()}%",
            color = ForceColors.TextSecondary
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        /*
         * -------------------------------------------------
         * CURRENT EXERCISE
         * -------------------------------------------------
         */

        Text(
            text = exercise.name,
            color = ForceColors.Primary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = exercise.target,
            color = ForceColors.TextSecondary
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text =
                "Set ${uiState.currentSet} " +
                        "of ${exercise.sets}",
            color = ForceColors.TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = exercise.reps,
            color = ForceColors.TextSecondary
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Weight (kg)",
            color = ForceColors.TextSecondary,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = uiState.currentWeight,
            onValueChange = onWeightChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Enter weight",
                    color = ForceColors.TextSecondary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForceColors.Primary,
                unfocusedBorderColor = ForceColors.TextSecondary,
                focusedTextColor = ForceColors.TextPrimary,
                unfocusedTextColor = ForceColors.TextPrimary,
                cursorColor = ForceColors.Primary
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Reps",
            color = ForceColors.TextSecondary,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = uiState.currentReps,
            onValueChange = onRepsChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Enter reps",
                    color = ForceColors.TextSecondary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForceColors.Primary,
                unfocusedBorderColor = ForceColors.TextSecondary,
                focusedTextColor = ForceColors.TextPrimary,
                unfocusedTextColor = ForceColors.TextPrimary,
                cursorColor = ForceColors.Primary
            ),
            shape = RoundedCornerShape(14.dp)
        )
        /*
         * -------------------------------------------------
         * FLEXIBLE SPACE
         *
         * Everything below this point stays anchored
         * toward the bottom of the screen.
         * -------------------------------------------------
         */

        Spacer(
            modifier = Modifier.weight(1f)
        )

        /*
         * -------------------------------------------------
         * REST TIMER
         * -------------------------------------------------
         *
         * The timer has a fixed-height container.
         * This prevents the buttons from jumping when
         * the timer appears/disappears.
         */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (uiState.isResting) {

                Text(
                    text = "REST",
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        formatRestTime(
                            uiState.restSecondsRemaining
                        ),
                    color = ForceColors.Primary,
                    style =
                        MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        /*
         * -------------------------------------------------
         * FIXED BOTTOM ACTION AREA
         * -------------------------------------------------
         */

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if (uiState.isResting) {

                Button(
                    onClick = onSkipRest,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForceColors.Surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Skip Rest",
                        color = ForceColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

            } else {

                Button(
                    onClick = onCompleteSet,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForceColors.Primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Complete Set",
                        color = ForceColors.Background,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onNextExercise,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForceColors.Surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Next Exercise",
                        color = ForceColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutExerciseCard(
    exercise: WorkoutExercise
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = ForceColors.Surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(ForceSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = exercise.name,
            color = ForceColors.TextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = exercise.target,
            color = ForceColors.Primary
        )

        Text(
            text =
                "${exercise.sets} sets • " +
                        exercise.reps,
            color = ForceColors.TextSecondary
        )

        Text(
            text =
                "${exercise.restSeconds} sec rest",
            color = ForceColors.TextSecondary
        )
    }
}

private fun calculateWorkoutProgress(
    completedSets: Int
): Float {
    val totalSets =
        todaysWorkout.sumOf { it.sets }

    if (totalSets == 0) {
        return 0f
    }

    return (
            completedSets.toFloat() /
                    totalSets.toFloat()
            ).coerceIn(0f, 1f)
}

private fun formatRestTime(
    seconds: Int
): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return "%02d:%02d".format(
        minutes,
        remainingSeconds
    )
}

@Composable
private fun WorkoutCompleteContent(
    uiState: ActiveWorkoutUiState,
    onFinishWorkout: () -> Unit
) {
    val totalSets = todaysWorkout.sumOf { it.sets }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForceColors.Background)
            .padding(ForceSpacing.Large),
        verticalArrangement = Arrangement.spacedBy(
            ForceSpacing.Large
        )
    ) {

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Workout Complete",
                    color = ForceColors.TextPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Great work! 🎉",
                    color = ForceColors.Primary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Chest & Triceps",
                    color = ForceColors.TextSecondary
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ForceColors.Surface,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(ForceSpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Workout Summary",
                    color = ForceColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SummaryItem(
                        label = "Exercises",
                        value = todaysWorkout.size.toString()
                    )

                    SummaryItem(
                        label = "Sets",
                        value = uiState.completedSets.toString()
                    )

                    SummaryItem(
                        label = "Planned",
                        value = "$totalSets sets"
                    )
                }
            }
        }

        item {
            Text(
                text = "Completed Exercises",
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(todaysWorkout) { exercise ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ForceColors.Surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(ForceSpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "✓ ${exercise.name}",
                    color = ForceColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        "${exercise.sets} sets • ${exercise.reps}",
                    color = ForceColors.TextSecondary
                )
            }
        }

        item {
            Button(
                onClick = onFinishWorkout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForceColors.Primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Finish Workout",
                    color = ForceColors.Background,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
private fun SummaryItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = ForceColors.Primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = ForceColors.TextSecondary
        )
    }
}
