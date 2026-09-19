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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.ui.graphics.SolidColor

@Composable
fun ProgressScreen(
    onWorkoutHistoryClick: () -> Unit,
    onPersonalRecordClick: (Long) -> Unit = {},
    viewModel: ProgressViewModel = hiltViewModel()
){

    val uiState by viewModel.uiState.collectAsState()

    val totalVolume =
        uiState.workoutVolumeHistory
            .sumOf { it.totalVolume }

    val workoutCount =
        uiState.workoutVolumeHistory.size

    val completedWorkoutCount =
        uiState.completedWorkoutCount

    val weeklyTrainingFrequency =
        completedWorkoutCount / (30.0 / 7.0)

    val trainingFrequencyInsight =
        when {
            completedWorkoutCount == 0 ->
                "No completed workouts recorded in the last 30 days."

            weeklyTrainingFrequency < 1.0 ->
                "Training has been light this month."

            weeklyTrainingFrequency < 2.0 ->
                "You are building a regular training rhythm."

            weeklyTrainingFrequency < 4.0 ->
                "Good consistency across the last 30 days."

            else ->
                "Very consistent training frequency."
        }

    val exerciseSearchQuery =
        uiState.exerciseSearchQuery

    val sortedStrengthExercises =
        uiState.personalRecords
            .sortedWith(
                compareByDescending<ExercisePersonalRecord> {
                    uiState.favoriteExercises
                        .contains(it.exerciseName)
                }.thenBy {
                    it.exerciseName.lowercase()
                }
            )

    val filteredStrengthExercises =
        if (exerciseSearchQuery.isBlank()) {

            sortedStrengthExercises

        } else {

            sortedStrengthExercises.filter { record ->

                record.exerciseName.contains(
                    exerciseSearchQuery.trim(),
                    ignoreCase = true
                )
            }
        }

    val volumeTrendPercent =
        if (uiState.workoutVolumeHistory.size >= 2) {

            val firstVolume =
                uiState.workoutVolumeHistory
                    .first()
                    .totalVolume

            val lastVolume =
                uiState.workoutVolumeHistory
                    .last()
                    .totalVolume

            if (firstVolume > 0.0) {
                ((lastVolume - firstVolume) /
                        firstVolume) * 100.0
            } else {
                null
            }

        } else {
            null
        }

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

        item {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ForceColors.Surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "30-Day Training Volume",
                    color = ForceColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Total load lifted across completed workouts.",
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = formatVolume(totalVolume),
                            color = ForceColors.Primary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Total volume",
                            color = ForceColors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {

                        Text(
                            text = workoutCount.toString(),
                            color = ForceColors.TextPrimary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (workoutCount == 1) {
                                "Workout"
                            } else {
                                "Workouts"
                            },
                            color = ForceColors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }

                if (volumeTrendPercent != null) {

                    val trendText =
                        when {

                            volumeTrendPercent > 1.0 ->
                                "↑ ${String.format(
                                    Locale.getDefault(),
                                    "%.1f",
                                    volumeTrendPercent
                                )}% from first workout"

                            volumeTrendPercent < -1.0 ->
                                "↓ ${String.format(
                                    Locale.getDefault(),
                                    "%.1f",
                                    kotlin.math.abs(volumeTrendPercent)
                                )}% from first workout"

                            else ->
                                "→ Volume is stable"
                        }

                    Text(
                        text = trendText,
                        color =
                            when {
                                volumeTrendPercent > 1.0 ->
                                    androidx.compose.ui.graphics.Color(
                                        0xFF7CFF6B
                                    )

                                volumeTrendPercent < -1.0 ->
                                    MaterialTheme.colorScheme.error

                                else ->
                                    ForceColors.TextSecondary
                            },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                /*
                 * GRAPH
                 */

                if (uiState.workoutVolumeHistory.isNotEmpty()) {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    TrainingVolumeGraph(
                        volumeHistory =
                            uiState.workoutVolumeHistory
                    )
                }
            }
        }

        item {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ForceColors.Surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Training Frequency",
                    color = ForceColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Your completed workout consistency over the last 30 days.",
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.Top
                ) {

                    Column {

                        Text(
                            text = completedWorkoutCount.toString(),
                            color = ForceColors.Primary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text =
                                if (completedWorkoutCount == 1) {
                                    "Completed workout"
                                } else {
                                    "Completed workouts"
                                },
                            color = ForceColors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {

                        Text(
                            text =
                                String.format(
                                    Locale.getDefault(),
                                    "%.1f",
                                    weeklyTrainingFrequency
                                ),
                            color =
                                androidx.compose.ui.graphics.Color(
                                    0xFF7CFF6B
                                ),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Per week",
                            color = ForceColors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Text(
                    text = trainingFrequencyInsight,
                    color =
                        if (weeklyTrainingFrequency >= 2.0) {
                            androidx.compose.ui.graphics.Color(
                                0xFF7CFF6B
                            )
                        } else {
                            ForceColors.TextSecondary
                        },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }


        item {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ForceColors.Surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Strength Progression",
                    color = ForceColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Select an exercise to view your 90-day strength history.",
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (!uiState.isExerciseSelectorOpen) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = ForceColors.Background,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                viewModel.openExerciseSelector()
                            }
                            .padding(
                                horizontal = 14.dp,
                                vertical = 14.dp
                            ),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Column {

                            Text(
                                text = "Exercise",
                                color = ForceColors.TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text =
                                    uiState.selectedStrengthExercise
                                        ?: "Select exercise",
                                color = ForceColors.TextPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "▼",
                            color = ForceColors.TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (uiState.isExerciseSelectorOpen) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        BasicTextField(
                            value = exerciseSearchQuery,
                            onValueChange = {
                                viewModel.updateExerciseSearchQuery(it)
                            },
                            singleLine = true,

                            cursorBrush = SolidColor(
                                ForceColors.Primary
                            ),

                            textStyle =
                                MaterialTheme.typography.bodyMedium.copy(
                                    color = ForceColors.TextPrimary
                                ),
                            modifier =
                                Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = ForceColors.Background,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(
                                            horizontal = 12.dp,
                                            vertical = 12.dp
                                        ),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Box(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        if (exerciseSearchQuery.isBlank()) {

                                            Text(
                                                text = "Search exercise...",
                                                color = ForceColors.TextSecondary,
                                                style =
                                                    MaterialTheme.typography.bodyMedium
                                            )
                                        }

                                        innerTextField()
                                    }
                                }
                            }
                        )

                        if (
                            exerciseSearchQuery.isBlank() &&
                            uiState.favoriteExercises.isNotEmpty()
                        ) {

                            Text(
                                text = "Favorites",
                                color = ForceColors.TextSecondary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        filteredStrengthExercises.forEach { record ->

                            val isFavorite =
                                uiState.favoriteExercises
                                    .contains(record.exerciseName)

                            val isSelected =
                                uiState.selectedStrengthExercise ==
                                        record.exerciseName

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color =
                                            if (isSelected) {
                                                ForceColors.Primary
                                                    .copy(alpha = 0.15f)
                                            } else {
                                                ForceColors.Background
                                            },
                                        shape =
                                            RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        viewModel.loadStrengthHistory(
                                            record.exerciseName
                                        )
                                    }
                                    .padding(
                                        start = 12.dp,
                                        top = 6.dp,
                                        bottom = 6.dp
                                    ),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Text(
                                    text = record.exerciseName,
                                    modifier = Modifier.weight(1f),
                                    color =
                                        if (isSelected) {
                                            ForceColors.Primary
                                        } else {
                                            ForceColors.TextPrimary
                                        },
                                    style =
                                        MaterialTheme.typography.bodyMedium,
                                    fontWeight =
                                        if (isSelected) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Medium
                                        }
                                )

                                IconButton(
                                    onClick = {
                                        viewModel.toggleFavoriteExercise(
                                            record.exerciseName
                                        )
                                    }
                                ) {

                                    Icon(
                                        imageVector =
                                            if (isFavorite) {
                                                Icons.Filled.Star
                                            } else {
                                                Icons.Outlined.StarBorder
                                            },
                                        contentDescription =
                                            if (isFavorite) {
                                                "Remove favorite"
                                            } else {
                                                "Add favorite"
                                            },
                                        tint =
                                            if (isFavorite) {
                                                androidx.compose.ui.graphics.Color(
                                                    0xFFFFC857
                                                )
                                            } else {
                                                ForceColors.TextSecondary
                                            }
                                    )
                                }
                            }
                        }

                        if (filteredStrengthExercises.isEmpty()) {

                            Text(
                                text = "No matching exercises found.",
                                color = ForceColors.TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                if (uiState.strengthHistory.isNotEmpty())
                {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    StrengthProgressionGraph(
                        history = uiState.strengthHistory
                    )
                }

                    Text(
                        text = "Close",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.closeExerciseSelector()
                            }
                            .padding(vertical = 10.dp),
                        color = ForceColors.Primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                if (
                    uiState.selectedStrengthExercise != null &&
                    uiState.strengthHistory.isEmpty()
                ) {

                    Text(
                        text = "No strength history found for this exercise.",
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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

private fun formatVolume(
    volume: Double
): String {

    return when {

        volume >= 1_000_000 ->
            String.format(
                Locale.getDefault(),
                "%.1fM kg",
                volume / 1_000_000
            )

        volume >= 1_000 ->
            String.format(
                Locale.getDefault(),
                "%.1fk kg",
                volume / 1_000
            )

        else ->
            "${volume.toInt()} kg"
    }
}

@Composable
private fun TrainingVolumeGraph(
    volumeHistory: List<com.ngedo.force.data.local.model.DailyWorkoutVolume>
) {

    if (volumeHistory.isEmpty()) {
        return
    }

    val volumes =
        volumeHistory.map {
            it.totalVolume
        }

    val maxVolume =
        volumes
            .maxOrNull()
            ?.coerceAtLeast(1.0)
            ?: 1.0

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            Column(
                modifier = Modifier
                    .height(150.dp)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = formatVolume(maxVolume),
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = formatVolume(maxVolume / 2.0),
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "0 kg",
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            ) {

                val graphColor =
                    androidx.compose.ui.graphics.Color(
                        0xFF7CFF6B
                    )

                val graphWidth =
                    size.width

                val graphHeight =
                    size.height

                val horizontalPadding =
                    8.dp.toPx()

                val verticalPadding =
                    12.dp.toPx()

                val usableWidth =
                    graphWidth -
                            (horizontalPadding * 2)

                val usableHeight =
                    graphHeight -
                            (verticalPadding * 2)

                val pointSpacing =
                    if (volumes.size > 1) {
                        usableWidth /
                                (volumes.size - 1)
                    } else {
                        0f
                    }

                val points =
                    volumes.mapIndexed { index, volume ->

                        val x =
                            if (volumes.size == 1) {
                                graphWidth / 2f
                            } else {
                                horizontalPadding +
                                        (pointSpacing * index)
                            }

                        val normalizedVolume =
                            (volume / maxVolume)
                                .toFloat()

                        val y =
                            verticalPadding +
                                    usableHeight *
                                    (1f - normalizedVolume)

                        Offset(
                            x = x,
                            y = y
                        )
                    }

                // Bottom grid line

                drawLine(
                    color =
                        ForceColors.TextSecondary
                            .copy(alpha = 0.18f),
                    start =
                        Offset(
                            horizontalPadding,
                            graphHeight - verticalPadding
                        ),
                    end =
                        Offset(
                            graphWidth - horizontalPadding,
                            graphHeight - verticalPadding
                        ),
                    strokeWidth =
                        1.dp.toPx()
                )

                // Middle grid line

                drawLine(
                    color =
                        ForceColors.TextSecondary
                            .copy(alpha = 0.10f),
                    start =
                        Offset(
                            horizontalPadding,
                            graphHeight / 2f
                        ),
                    end =
                        Offset(
                            graphWidth - horizontalPadding,
                            graphHeight / 2f
                        ),
                    strokeWidth =
                        1.dp.toPx()
                )

                // Trend line

                if (points.size > 1) {

                    val path =
                        Path().apply {

                            moveTo(
                                points.first().x,
                                points.first().y
                            )

                            points
                                .drop(1)
                                .forEach { point ->

                                    lineTo(
                                        point.x,
                                        point.y
                                    )
                                }
                        }

                    drawPath(
                        path = path,
                        color = graphColor,
                        style =
                            androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 3.dp.toPx()
                            )
                    )
                }

                // Workout points

                points.forEach { point ->

                    drawCircle(
                        color = graphColor,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        if (volumeHistory.isNotEmpty()) {

            val firstDate =
                formatGraphDate(
                    volumeHistory.first().workoutDate
                )

            val lastDate =
                formatGraphDate(
                    volumeHistory.last().workoutDate
                )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = firstDate,
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = lastDate,
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text(
            text = "Training volume trend",
            color = ForceColors.TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun StrengthProgressionGraph(
    history: List<com.ngedo.force.data.local.model.ExerciseStrengthProgress>
) {

    if (history.isEmpty()) {
        return
    }

    val weights =
        history.map {
            it.highestWeight
        }

    val maxWeight =
        weights
            .maxOrNull()
            ?.coerceAtLeast(1.0)
            ?: 1.0

    val minWeight =
        weights
            .minOrNull()
            ?: 0.0

    val graphRange =
        (maxWeight - minWeight)
            .coerceAtLeast(1.0)

    val firstWeight =
        history.first().highestWeight

    val latestWeight =
        history.last().highestWeight

    val weightChange =
        latestWeight - firstWeight

    val strengthChangePercent =
        if (firstWeight > 0.0) {
            (weightChange / firstWeight) * 100.0
        } else {
            0.0
        }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text =
                        when {
                            weightChange > 0.0 ->
                                "+${formatStrengthWeight(weightChange)}"

                            weightChange < 0.0 ->
                                "-${formatStrengthWeight(
                                    kotlin.math.abs(weightChange)
                                )}"

                            else ->
                                "No change"
                        },
                    color =
                        when {
                            weightChange > 0.0 ->
                                androidx.compose.ui.graphics.Color(
                                    0xFF7CFF6B
                                )

                            weightChange < 0.0 ->
                                MaterialTheme.colorScheme.error

                            else ->
                                ForceColors.TextSecondary
                        },
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "90-day change",
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (weightChange != 0.0) {

                Text(
                    text =
                        String.format(
                            Locale.getDefault(),
                            "%+.1f%%",
                            strengthChangePercent
                        ),
                    color =
                        if (weightChange > 0.0) {
                            androidx.compose.ui.graphics.Color(
                                0xFF7CFF6B
                            )
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            Column(
                modifier = Modifier
                    .height(160.dp)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = formatStrengthWeight(maxWeight),
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text =
                        formatStrengthWeight(
                            minWeight + (graphRange / 2.0)
                        ),
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = formatStrengthWeight(minWeight),
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
            ) {

                val graphColor =
                    androidx.compose.ui.graphics.Color(
                        0xFFFFC857
                    )

                val graphWidth =
                    size.width

                val graphHeight =
                    size.height

                val horizontalPadding =
                    8.dp.toPx()

                val verticalPadding =
                    12.dp.toPx()

                val usableWidth =
                    graphWidth -
                            (horizontalPadding * 2)

                val usableHeight =
                    graphHeight -
                            (verticalPadding * 2)

                val pointSpacing =
                    if (weights.size > 1) {
                        usableWidth /
                                (weights.size - 1)
                    } else {
                        0f
                    }

                val points =
                    weights.mapIndexed { index, weight ->

                        val x =
                            if (weights.size == 1) {
                                graphWidth / 2f
                            } else {
                                horizontalPadding +
                                        (pointSpacing * index)
                            }

                        val normalizedWeight =
                            ((weight - minWeight) /
                                    graphRange)
                                .toFloat()

                        val y =
                            verticalPadding +
                                    usableHeight *
                                    (1f - normalizedWeight)

                        Offset(
                            x = x,
                            y = y
                        )
                    }

                // Bottom line
                drawLine(
                    color =
                        ForceColors.TextSecondary
                            .copy(alpha = 0.18f),
                    start =
                        Offset(
                            horizontalPadding,
                            graphHeight - verticalPadding
                        ),
                    end =
                        Offset(
                            graphWidth - horizontalPadding,
                            graphHeight - verticalPadding
                        ),
                    strokeWidth = 1.dp.toPx()
                )

                // Middle guide line
                drawLine(
                    color =
                        ForceColors.TextSecondary
                            .copy(alpha = 0.10f),
                    start =
                        Offset(
                            horizontalPadding,
                            graphHeight / 2f
                        ),
                    end =
                        Offset(
                            graphWidth - horizontalPadding,
                            graphHeight / 2f
                        ),
                    strokeWidth = 1.dp.toPx()
                )

                if (points.size > 1) {

                    val path =
                        Path().apply {

                            moveTo(
                                points.first().x,
                                points.first().y
                            )

                            points
                                .drop(1)
                                .forEach { point ->

                                    lineTo(
                                        point.x,
                                        point.y
                                    )
                                }
                        }

                    drawPath(
                        path = path,
                        color = graphColor,
                        style =
                            androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 3.dp.toPx()
                            )
                    )
                }

                points.forEach { point ->

                    drawCircle(
                        color = graphColor,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        if (history.isNotEmpty()) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        formatGraphDate(
                            history.first().workoutDate
                        ),
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text =
                        formatGraphDate(
                            history.last().workoutDate
                        ),
                    color = ForceColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text(
            text =
                "Strength trend • ${formatStrengthWeight(minWeight)} – " +
                        formatStrengthWeight(maxWeight),
            color = ForceColors.TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun formatStrengthWeight(
    weight: Double
): String {

    return if (weight % 1.0 == 0.0) {
        "${weight.toInt()} kg"
    } else {
        "$weight kg"
    }
}
private fun formatGraphDate(
    timestamp: Long
): String {

    return SimpleDateFormat(
        "dd MMM",
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