package com.ngedo.force.feature.workout.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.ngedo.force.designsystem.ForceColors

@Composable
fun PlanWorkoutDayDetail(
    workoutDay: PlanWorkoutDay,
    onBack: () -> Unit,
    onExerciseDetails: (PlanExercise) -> Unit,
    onEditExercise: (
        PlanExercise,
        Int,
        String,
        Int
    ) -> Unit,
    onRemoveExercise: (PlanExercise) -> Unit,
    onMoveUp: (PlanExercise) -> Unit,
    onMoveDown: (PlanExercise) -> Unit,
    onAddExercises: () -> Unit,
    onStartWorkout: () -> Unit
) {

    var exerciseBeingEdited by remember {
        mutableStateOf<PlanExercise?>(null)
    }

    var exerciseBeingRemoved by remember {
        mutableStateOf<PlanExercise?>(null)
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
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = workoutDay.name.uppercase(),
                        color = ForceColors.TextPrimary,
                        style =
                            MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "${workoutDay.exercises.size} exercises",
                        color = ForceColors.TextSecondary
                    )
                }

                OutlinedButton(
                    onClick = onBack
                ) {
                    Text("BACK")
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            if (workoutDay.exercises.isEmpty()) {

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ForceColors.Surface,
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "NO EXERCISES YET",
                            color = ForceColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Add exercises to program this workout.",
                            color = ForceColors.TextSecondary
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = workoutDay.exercises,
                        key = {
                            it.exerciseId
                        }
                    ) { exercise ->

                        PlanExerciseCard(
                            exercise = exercise,

                            onDetailsClick = {
                                onExerciseDetails(exercise)
                            },

                            onEditClick = {
                                exerciseBeingEdited =
                                    exercise
                            },

                            onRemoveClick = {
                                exerciseBeingRemoved =
                                    exercise
                            },

                            onMoveUp = {
                                onMoveUp(exercise)
                            },

                            onMoveDown = {
                                onMoveDown(exercise)
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedButton(
                onClick = onAddExercises,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = "+ ADD EXERCISES",
                    color = ForceColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (workoutDay.exercises.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Button(
                    onClick = onStartWorkout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                ForceColors.Primary
                        ),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text(
                        text = "START WORKOUT",
                        color = ForceColors.Background,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    exerciseBeingEdited?.let { exercise ->

        PlanExerciseEditDialog(
            exercise = exercise,

            onDismiss = {
                exerciseBeingEdited = null
            },

            onSave = { sets, reps, restSeconds ->

                onEditExercise(
                    exercise,
                    sets,
                    reps,
                    restSeconds
                )

                exerciseBeingEdited = null
            }
        )
    }

    exerciseBeingRemoved?.let { exercise ->

        AlertDialog(
            onDismissRequest = {
                exerciseBeingRemoved = null
            },

            containerColor = ForceColors.Surface,

            title = {
                Text(
                    text = "Remove Exercise?",
                    color = ForceColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    text =
                        "Remove ${exercise.exerciseName} from ${workoutDay.name}?",
                    color = ForceColors.TextSecondary
                )
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        exerciseBeingRemoved = null
                    }
                ) {

                    Text(
                        text = "CANCEL",
                        color = ForceColors.TextSecondary
                    )
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        onRemoveExercise(exercise)

                        exerciseBeingRemoved = null
                    },

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFFB3261E)
                        )
                ) {

                    Text(
                        text = "REMOVE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}


@Composable
private fun PlanExerciseCard(
    exercise: PlanExercise,
    onDetailsClick: () -> Unit,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ForceColors.Surface,
        shape = RoundedCornerShape(20.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 16.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                TextButton(
                    onClick = onMoveUp,
                    modifier = Modifier.height(34.dp)
                ) {

                    Text(
                        text = "▲",
                        color = ForceColors.Primary,
                        style =
                            MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onMoveDown,
                    modifier = Modifier.height(34.dp)
                ) {

                    Text(
                        text = "▼",
                        color = ForceColors.Primary,
                        style =
                            MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = exercise.exerciseName,
                    color = ForceColors.TextPrimary,
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = exercise.target,
                    color = ForceColors.Primary,
                    style =
                        MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    PlanPrescriptionValue(
                        label = "SETS",
                        value = exercise.sets.toString()
                    )

                    PlanPrescriptionValue(
                        label = "REPS",
                        value =
                            exercise.reps
                                .replace(
                                    " reps",
                                    "",
                                    ignoreCase = true
                                )
                                .trim()
                    )

                    PlanPrescriptionValue(
                        label = "REST",
                        value =
                            "${exercise.restSeconds}s"
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.End,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = onDetailsClick
                    ) {

                        Text(
                            text = "DETAILS",
                            color = ForceColors.Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = onEditClick
                    ) {

                        Text(
                            text = "EDIT",
                            color = Color(0xFFFFC857),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = onRemoveClick
                    ) {

                        Text(
                            text = "REMOVE",
                            color = ForceColors.TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun PlanPrescriptionValue(
    label: String,
    value: String
) {

    Column(
        horizontalAlignment =
            Alignment.Start
    ) {

        Text(
            text = label,
            color = ForceColors.TextSecondary,
            style =
                MaterialTheme.typography.labelSmall
        )

        Text(
            text = value,
            color = ForceColors.TextPrimary,
            style =
                MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Composable
private fun PlanExerciseEditDialog(
    exercise: PlanExercise,
    onDismiss: () -> Unit,
    onSave: (
        sets: Int,
        reps: String,
        restSeconds: Int
    ) -> Unit
) {

    var setsText by remember(exercise) {
        mutableStateOf(
            exercise.sets.toString()
        )
    }

    var repsText by remember(exercise) {
        mutableStateOf(
            exercise.reps
                .replace(
                    " reps",
                    "",
                    ignoreCase = true
                )
                .trim()
        )
    }

    var restText by remember(exercise) {
        mutableStateOf(
            exercise.restSeconds.toString()
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ForceColors.Surface,

        title = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = "Edit Exercise",
                    color = ForceColors.TextPrimary,
                    style =
                        MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = exercise.exerciseName,
                    color = ForceColors.Primary
                )
            }
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = setsText,

                    onValueChange = { value ->
                        if (
                            value.isEmpty() ||
                            value.all {
                                it.isDigit()
                            }
                        ) {
                            setsText = value
                        }
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Sets")
                    },

                    singleLine = true,

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),

                    colors =
                        planEditFieldColors()
                )

                OutlinedTextField(
                    value = repsText,

                    onValueChange = { value ->
                        repsText = value
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Target Reps")
                    },

                    singleLine = true,

                    colors =
                        planEditFieldColors()
                )

                OutlinedTextField(
                    value = restText,

                    onValueChange = { value ->
                        if (
                            value.isEmpty() ||
                            value.all {
                                it.isDigit()
                            }
                        ) {
                            restText = value
                        }
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Rest (seconds)")
                    },

                    singleLine = true,

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),

                    colors =
                        planEditFieldColors()
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "CANCEL",
                    color = ForceColors.TextSecondary
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    val sets =
                        setsText.toIntOrNull()

                    val restSeconds =
                        restText.toIntOrNull()

                    if (
                        sets != null &&
                        sets > 0 &&
                        repsText.isNotBlank() &&
                        restSeconds != null &&
                        restSeconds >= 0
                    ) {

                        onSave(
                            sets,
                            repsText.trim(),
                            restSeconds
                        )
                    }
                },

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            ForceColors.Primary
                    )
            ) {

                Text(
                    text = "SAVE",
                    color = ForceColors.Background,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}


@Composable
private fun planEditFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor =
            ForceColors.TextPrimary,

        unfocusedTextColor =
            ForceColors.TextPrimary,

        focusedLabelColor =
            ForceColors.Primary,

        unfocusedLabelColor =
            ForceColors.TextSecondary,

        focusedBorderColor =
            ForceColors.Primary,

        unfocusedBorderColor =
            ForceColors.TextSecondary,

        cursorColor =
            ForceColors.Primary
    )