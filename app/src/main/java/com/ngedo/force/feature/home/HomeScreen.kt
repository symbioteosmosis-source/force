package com.ngedo.force.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ngedo.force.designsystem.ForceColors
import com.ngedo.force.designsystem.ForceSpacing
import com.ngedo.force.designsystem.components.ForceProgressCard
import com.ngedo.force.designsystem.components.ForceQuickActionCard
import com.ngedo.force.designsystem.components.ForceStatCard
import com.ngedo.force.designsystem.components.ForceTopBar
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.ngedo.force.navigation.ForceDestination

@Composable
fun HomeScreen(
    onNavigate: (ForceDestination) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForceColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(ForceSpacing.Large),

        verticalArrangement = Arrangement.spacedBy(
            ForceSpacing.Large
        )
    ) {

        ForceTopBar(
            greeting = "Good Evening 👋",
            userName = "Layton"
        )

        ForceProgressCard(
            title = "Today's Goal",
            progress = 0.78f,
            percentage = 78
        )

        ForceStatCard(
            title = "🔥 Calories",
            value = "686 kcal"
        )

        ForceStatCard(
            title = "💪 Workout",
            value = "Chest & Triceps"
        )

        ForceStatCard(
            title = "💧 Water",
            value = "1.8 L"
        )

        Text(
            text = "Quick Actions",
            color = ForceColors.TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                ForceSpacing.Medium
            )
        ) {
            ForceQuickActionCard(
                title = "Workout",
                subtitle = "Start training",
                icon = Icons.Default.FitnessCenter,
                iconTint = ForceColors.Primary,
                onClick = {
                    onNavigate(ForceDestination.Workout)
                },
                modifier = Modifier.weight(1f)
            )

            ForceQuickActionCard(
                title = "AI Coach",
                subtitle = "Get guidance",
                icon = Icons.Default.SmartToy,
                iconTint = ForceColors.Primary,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                ForceSpacing.Medium
            )
        ) {
            ForceQuickActionCard(
                title = "Nutrition",
                subtitle = "Track meals",
                icon = Icons.Default.Restaurant,
                iconTint = ForceColors.Primary,
                onClick = {},
                modifier = Modifier.weight(1f)
            )

            ForceQuickActionCard(
                title = "Progress",
                subtitle = "View results",
                icon = Icons.Default.TrendingUp,
                iconTint = ForceColors.Primary,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}