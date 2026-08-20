package com.ngedo.force.feature.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ngedo.force.designsystem.ForceColors

@Composable
fun ProgressScreen(
    onWorkoutHistoryClick: () -> Unit
) {

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

        Spacer(
            modifier = Modifier.height(24.dp)
        )


        /*
         * -------------------------------------------------
         * WORKOUT HISTORY
         * -------------------------------------------------
         */

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


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        /*
         * -------------------------------------------------
         * PERSONAL RECORDS
         * -------------------------------------------------
         *
         * Placeholder for the next Progress feature.
         */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ForceColors.Surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = "Personal Records",
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Your exercise records and strength progress will appear here.",
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Coming next",
                color = ForceColors.Primary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}