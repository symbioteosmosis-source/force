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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ngedo.force.data.local.entity.WorkoutSetEntity
import com.ngedo.force.designsystem.ForceColors


@Composable
fun WorkoutHistoryDetailScreen(
    sessionId: Long,
    onBack: () -> Unit,
    viewModel: WorkoutHistoryDetailViewModel =
        hiltViewModel()
) {

    val uiState by
    viewModel.uiState.collectAsState()

    LaunchedEffect(
        sessionId
    ) {
        viewModel.loadSession(
            sessionId
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

        /*
         * -------------------------------------------------
         * HEADER
         * -------------------------------------------------
         */

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = "Workout Details",
                color =
                    ForceColors.TextPrimary,
                style =
                    MaterialTheme.typography.headlineSmall,
                fontWeight =
                    FontWeight.Bold
            )

            Button(
                onClick = onBack,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            ForceColors.Surface
                    ),
                shape =
                    RoundedCornerShape(12.dp)
            ) {

                Text(
                    text = "Back",
                    color =
                        ForceColors.TextPrimary
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        if (uiState.isLoading) {

            Text(
                text = "Loading workout...",
                color =
                    ForceColors.TextSecondary
            )

        } else if (
            uiState.exercises.isEmpty()
        ) {

            Text(
                text =
                    "No exercise data found for this workout.",
                color =
                    ForceColors.TextSecondary
            )

        } else {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(
                        16.dp
                    )
            ) {

                items(
                    items =
                        uiState.exercises,

                    key = {
                        it.exerciseId
                    }
                ) { exercise ->

                    WorkoutHistoryExerciseCard(
                        exercise =
                            exercise
                    )
                }
            }
        }
    }
}


@Composable
private fun WorkoutHistoryExerciseCard(
    exercise: WorkoutHistoryExerciseUi
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    ForceColors.Surface,
                shape =
                    RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

        Text(
            text =
                exercise.exerciseName,
            color =
                ForceColors.TextPrimary,
            style =
                MaterialTheme.typography.titleMedium,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        /*
         * -------------------------------------------------
         * TABLE HEADER
         * -------------------------------------------------
         */

        Row(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            HistoryHeaderCell(
                text = "SET",
                modifier =
                    Modifier.weight(1f)
            )

            HistoryHeaderCell(
                text = "KG",
                modifier =
                    Modifier.weight(1f)
            )

            HistoryHeaderCell(
                text = "REPS",
                modifier =
                    Modifier.weight(1f)
            )
        }

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )


        if (
            exercise.sets.isEmpty()
        ) {

            Text(
                text =
                    "No sets recorded",
                color =
                    ForceColors.TextSecondary
            )

        } else {

            exercise.sets.forEach { set ->

                HistorySetRow(
                    set = set
                )
            }
        }
    }
}


@Composable
private fun HistorySetRow(
    set: WorkoutSetEntity
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        HistoryValueCell(
            text =
                set.setNumber.toString(),
            modifier =
                Modifier.weight(1f)
        )

        HistoryValueCell(
            text =
                formatWeight(
                    set.weight
                ),
            modifier =
                Modifier.weight(1f)
        )

        HistoryValueCell(
            text =
                if (set.reps > 0) {
                    set.reps.toString()
                } else {
                    "-"
                },
            modifier =
                Modifier.weight(1f)
        )
    }
}


@Composable
private fun HistoryHeaderCell(
    text: String,
    modifier: Modifier = Modifier
) {

    Text(
        text = text,
        modifier = modifier,
        color =
            ForceColors.TextSecondary,
        style =
            MaterialTheme.typography.labelSmall,
        fontWeight =
            FontWeight.Bold
    )
}


@Composable
private fun HistoryValueCell(
    text: String,
    modifier: Modifier = Modifier
) {

    Text(
        text = text,
        modifier = modifier,
        color =
            ForceColors.TextPrimary,
        style =
            MaterialTheme.typography.bodyMedium
    )
}


private fun formatWeight(
    weight: Double
): String {

    if (weight <= 0.0) {
        return "-"
    }

    return if (
        weight % 1.0 == 0.0
    ) {
        "${weight.toInt()} kg"
    } else {
        "$weight kg"
    }
}