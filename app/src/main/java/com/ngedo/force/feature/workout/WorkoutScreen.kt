package com.ngedo.force.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
                onDone = workoutViewModel::dismissCompletion
            )
        }

        uiState.isWorkoutStarted -> {

            ActiveWorkoutContent(
                uiState = uiState,

                onCompleteSet = {
                    val exercise =
                        todaysWorkout[
                            uiState.currentExerciseIndex
                        ]

                    workoutViewModel.completeSet(
                        totalSets = exercise.sets,
                        totalExercises = todaysWorkout.size,
                        restSeconds = exercise.restSeconds
                    )
                },

                onSkipRest = {
                    val exercise =
                        todaysWorkout[
                            uiState.currentExerciseIndex
                        ]

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
                onStartWorkout =
                    workoutViewModel::startWorkout
            )
        }
    }
}


/*
 * ---------------------------------------------------------
 * WORKOUT OVERVIEW
 * ---------------------------------------------------------
 */

@Composable
private fun WorkoutOverview(
    onStartWorkout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ForceColors.Background
            )
            .padding(
                ForceSpacing.Large
            ),

        verticalArrangement =
            Arrangement.spacedBy(
                ForceSpacing.Large
            )
    ) {

        item {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Today's Workout",
                    color =
                        ForceColors.TextPrimary
                )

                Text(
                    text = "Chest & Triceps",
                    color =
                        ForceColors.Primary
                )

                Text(
                    text =
                        "4 exercises • ~45 minutes",
                    color =
                        ForceColors.TextSecondary
                )
            }
        }

        items(todaysWorkout) { exercise ->

            WorkoutExerciseCard(
                exercise = exercise
            )
        }

        item {

            Button(
                onClick = onStartWorkout,

                modifier =
                    Modifier.fillMaxWidth(),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            ForceColors.Primary
                    ),

                shape =
                    RoundedCornerShape(16.dp)

            ) {

                Text(
                    text = "Start Workout",
                    color =
                        ForceColors.Background
                )
            }
        }
    }
}


/*
 * ---------------------------------------------------------
 * ACTIVE WORKOUT
 * ---------------------------------------------------------
 */

@Composable
private fun ActiveWorkoutContent(
    uiState: ActiveWorkoutUiState,
    onCompleteSet: () -> Unit,
    onSkipRest: () -> Unit,
    onNextExercise: () -> Unit
) {
    val exercise =
        todaysWorkout[
            uiState.currentExerciseIndex
        ]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ForceColors.Background
            )
            .padding(
                ForceSpacing.Large
            ),

        verticalArrangement =
            Arrangement.spacedBy(
                ForceSpacing.Large
            )
    ) {

        Text(
            text = "Active Workout",
            color =
                ForceColors.TextPrimary
        )

        Text(
            text =
                "Exercise " +
                        "${uiState.currentExerciseIndex + 1} " +
                        "of ${todaysWorkout.size}",

            color =
                ForceColors.TextSecondary
        )

        Text(
            text = exercise.name,
            color =
                ForceColors.Primary
        )

        Text(
            text = exercise.target,
            color =
                ForceColors.TextSecondary
        )

        Text(
            text =
                "Set ${uiState.currentSet} " +
                        "of ${exercise.sets}",

            color =
                ForceColors.TextPrimary
        )

        Text(
            text = exercise.reps,
            color =
                ForceColors.TextSecondary
        )

        if (uiState.isResting) {

            Text(
                text = "REST",
                color =
                    ForceColors.Primary
            )

            Text(
                text =
                    formatRestTime(
                        uiState.restSecondsRemaining
                    ),

                color =
                    ForceColors.TextPrimary
            )

            Text(
                text =
                    "Next set: " +
                            "${uiState.currentSet + 1} " +
                            "of ${exercise.sets}",

                color =
                    ForceColors.TextSecondary
            )

            Button(
                onClick = onSkipRest,

                modifier =
                    Modifier.fillMaxWidth(),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            ForceColors.Surface
                    ),

                shape =
                    RoundedCornerShape(16.dp)

            ) {

                Text(
                    text = "Skip Rest",
                    color =
                        ForceColors.TextPrimary
                )
            }

        } else {

            Button(
                onClick = onCompleteSet,

                modifier =
                    Modifier.fillMaxWidth(),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            ForceColors.Primary
                    ),

                shape =
                    RoundedCornerShape(16.dp)

            ) {

                Text(
                    text = "Complete Set",
                    color =
                        ForceColors.Background
                )
            }

            Button(
                onClick = onNextExercise,

                modifier =
                    Modifier.fillMaxWidth(),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            ForceColors.Surface
                    ),

                shape =
                    RoundedCornerShape(16.dp)

            ) {

                Text(
                    text = "Next Exercise",
                    color =
                        ForceColors.TextPrimary
                )
            }
        }
    }
}


/*
 * ---------------------------------------------------------
 * WORKOUT COMPLETE
 * ---------------------------------------------------------
 */

@Composable
private fun WorkoutCompleteContent(
    uiState: ActiveWorkoutUiState,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ForceColors.Background
            )
            .padding(
                ForceSpacing.Large
            ),

        verticalArrangement =
            Arrangement.spacedBy(
                ForceSpacing.Large
            )
    ) {

        Text(
            text = "WORKOUT COMPLETE 🎉",
            color =
                ForceColors.Primary
        )

        Text(
            text = "Chest & Triceps",
            color =
                ForceColors.TextPrimary
        )

        Text(
            text =
                "${uiState.completedExercises} exercises completed",

            color =
                ForceColors.TextSecondary
        )

        Text(
            text =
                "${uiState.completedSets} sets completed",

            color =
                ForceColors.TextSecondary
        )

        Text(
            text = "Great work!",
            color =
                ForceColors.TextPrimary
        )

        Button(
            onClick = onDone,

            modifier =
                Modifier.fillMaxWidth(),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        ForceColors.Primary
                ),

            shape =
                RoundedCornerShape(16.dp)

        ) {

            Text(
                text = "Back to Dashboard",
                color =
                    ForceColors.Background
            )
        }
    }
}


/*
 * ---------------------------------------------------------
 * EXERCISE CARD
 * ---------------------------------------------------------
 */

@Composable
private fun WorkoutExerciseCard(
    exercise: WorkoutExercise
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    ForceColors.Surface,

                shape =
                    RoundedCornerShape(20.dp)
            )
            .padding(
                ForceSpacing.Medium
            ),

        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = exercise.name,
            color =
                ForceColors.TextPrimary
        )

        Text(
            text = exercise.target,
            color =
                ForceColors.Primary
        )

        Text(
            text =
                "${exercise.sets} sets • " +
                        exercise.reps,

            color =
                ForceColors.TextSecondary
        )

        Text(
            text =
                "${exercise.restSeconds} sec rest",

            color =
                ForceColors.TextSecondary
        )
    }
}


/*
 * ---------------------------------------------------------
 * REST TIMER FORMAT
 * ---------------------------------------------------------
 */

private fun formatRestTime(
    totalSeconds: Int
): String {
    val minutes =
        totalSeconds / 60

    val seconds =
        totalSeconds % 60

    return "%02d:%02d".format(
        minutes,
        seconds
    )
}