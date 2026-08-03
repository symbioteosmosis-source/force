package com.ngedo.force.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ngedo.force.designsystem.ForceColors
import com.ngedo.force.designsystem.ForceSpacing
import com.ngedo.force.designsystem.components.ForceProgressCard
import com.ngedo.force.designsystem.components.ForceStatCard
import com.ngedo.force.designsystem.components.ForceTopBar

@Composable
fun HomeScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForceColors.Background)
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

    }

}