package com.ngedo.force.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ngedo.force.data.local.entity.WorkoutSessionEntity
import com.ngedo.force.designsystem.ForceColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.clickable

@Composable
fun WorkoutHistoryScreen(
    onSessionClick: (Long) -> Unit,
    viewModel: WorkoutHistoryViewModel = hiltViewModel()
) {

    val sessions by
    viewModel.completedSessions.collectAsState()

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

        if (sessions.isEmpty()) {

            Text(
                text = "No completed workouts yet.",
                color = ForceColors.TextSecondary
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
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = ForceColors.Surface,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
            }
            .padding(16.dp)
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