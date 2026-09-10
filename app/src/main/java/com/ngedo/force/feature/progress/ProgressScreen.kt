package com.ngedo.force.feature.progress

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ngedo.force.data.local.model.ExercisePersonalRecord
import com.ngedo.force.designsystem.ForceColors
import androidx.compose.runtime.LaunchedEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(
    onWorkoutHistoryClick: () -> Unit,
    onPersonalRecordClick: (Long) -> Unit = {},
    viewModel: ProgressViewModel = hiltViewModel()
){

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPersonalRecords()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ForceColors.Background
            )
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        /*
         * -------------------------------------------------
         * HEADER
         * -------------------------------------------------
         */

        item {

            Text(
                text = "Progress",
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Track your training performance over time.",
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }


        /*
         * -------------------------------------------------
         * WORKOUT HISTORY
         * -------------------------------------------------
         */

        item {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ForceColors.Surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        onWorkoutHistoryClick()
                    }
                    .padding(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = "Workout History",
                    color = ForceColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "View your completed workouts, duration and training history.",
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "View History →",
                    color = ForceColors.Primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        /*
         * -------------------------------------------------
         * PERSONAL RECORDS HEADER
         * -------------------------------------------------
         */

        item {

            Text(
                text = "Personal Records",
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Your best recorded performances by exercise.",
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }


        /*
         * -------------------------------------------------
         * PERSONAL RECORDS CONTENT
         * -------------------------------------------------
         */

        if (uiState.isLoading) {

            item {

                Text(
                    text = "Loading personal records...",
                    color = ForceColors.TextSecondary
                )
            }

        } else if (
            uiState.personalRecords.isEmpty()
        ) {

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = ForceColors.Surface,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {

                    Text(
                        text = "No personal records yet",
                        color = ForceColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Complete workouts with recorded weight and reps to create personal records.",
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

        } else {

            items(
                items = uiState.personalRecords,
                key = { record ->
                    record.exerciseName
                }
            ) { record ->

                PersonalRecordCard(
                    record = record,

                    onHighestWeightClick = {
                        record.highestWeightSessionId?.let { sessionId ->
                            onPersonalRecordClick(sessionId)
                        }
                    },

                    onHighestRepsClick = {
                        record.highestRepsSessionId?.let { sessionId ->
                            onPersonalRecordClick(sessionId)
                        }
                    }
                )
            }
        }
    }
}


@Composable
private fun PersonalRecordCard(
    record: ExercisePersonalRecord,
    onHighestWeightClick: () -> Unit,
    onHighestRepsClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = ForceColors.Surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        /*
         * EXERCISE NAME
         */

        Text(
            text = record.exerciseName,
            color = ForceColors.TextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )


        /*
         * HEAVIEST SET
         */

        PersonalRecordRow(
            title = "HEAVIEST SET",
            value =
                formatRecord(
                    weight = record.highestWeight,
                    reps = record.repsAtHighestWeight
                ),
            date =
                formatRecordDate(
                    record.highestWeightDate
                ),
            enabled =
                record.highestWeightSessionId != null,
            onClick = onHighestWeightClick
        )


        /*
         * REP RECORD
         */

        PersonalRecordRow(
            title = "REP RECORD",
            value =
                formatRecord(
                    weight = record.weightAtHighestReps,
                    reps = record.highestReps
                ),
            date =
                formatRecordDate(
                    record.highestRepsDate
                ),
            enabled =
                record.highestRepsSessionId != null,
            onClick = onHighestRepsClick
        )
    }
}


@Composable
private fun PersonalRecordRow(
    title: String,
    value: String,
    date: String,
    enabled: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled
            ) {
                onClick()
            }
            .padding(
                vertical = 4.dp
            ),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = title,
            color = ForceColors.TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        Column(
            horizontalAlignment =
                Alignment.End
        ) {

            Text(
                text = value,
                color = ForceColors.Primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (date.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "$date  →",
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


private fun formatRecordDate(
    timestamp: Long?
): String {

    if (
        timestamp == null ||
        timestamp <= 0L
    ) {
        return ""
    }

    return SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    ).format(
        Date(timestamp)
    )
}
private fun formatRecord(
    weight: Double?,
    reps: Int?
): String {

    if (
        weight == null &&
        reps == null
    ) {
        return "-"
    }

    val weightText =
        when {

            weight == null ||
                    weight <= 0.0 ->
                "-"

            weight % 1.0 == 0.0 ->
                "${weight.toInt()} kg"

            else ->
                "$weight kg"
        }

    val repsText =
        if (
            reps != null &&
            reps > 0
        ) {
            "$reps reps"
        } else {
            "-"
        }

    return "$weightText × $repsText"
}