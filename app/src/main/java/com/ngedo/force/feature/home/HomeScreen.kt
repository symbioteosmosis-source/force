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
import java.time.LocalTime
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ngedo.force.feature.profile.ProfileViewModel
import com.ngedo.force.feature.workout.plan.MonthlyPlanViewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun HomeScreen(
    onNavigate: (ForceDestination) -> Unit,
    onViewTodayWorkout: (Long) -> Unit,
    onStartTodayWorkout: (Long) -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    monthlyPlanViewModel: MonthlyPlanViewModel = hiltViewModel()
) {

    val profileState by
    profileViewModel.uiState.collectAsState()

    val monthlyPlanState by
    monthlyPlanViewModel.uiState.collectAsState()

    val todayWorkout =
        monthlyPlanState.todayWorkout

    val lifecycleOwner =
        LocalLifecycleOwner.current

    DisposableEffect(
        lifecycleOwner,
        monthlyPlanViewModel
    ) {

        val observer =
            LifecycleEventObserver { _, event ->

                if (event == Lifecycle.Event.ON_RESUME) {
                    monthlyPlanViewModel
                        .refreshTodayWorkout()
                }
            }

        lifecycleOwner.lifecycle
            .addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle
                .removeObserver(observer)
        }
    }

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
            greeting = getGreeting(),
            userName =
                profileState.name
                    .trim()
                    .ifBlank {
                        "FORCE Athlete"
                    }
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ForceColors.Surface,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(20.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = "TODAY'S WORKOUT",
                color = ForceColors.TextSecondary
            )

            when {

                todayWorkout == null -> {

                    Text(
                        text = "Rest Day",
                        color = ForceColors.TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "No workout scheduled for today",
                        color = ForceColors.TextSecondary
                    )
                }

                todayWorkout.isCompleted -> {

                    Text(
                        text = todayWorkout.workoutName,
                        color = ForceColors.TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "✓ WORKOUT COMPLETED",
                        color = ForceColors.Success,
                        fontWeight = FontWeight.Bold
                    )
                }

                else -> {

                    Text(
                        text = todayWorkout.workoutName,
                        color = ForceColors.TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text =
                            "${todayWorkout.exerciseCount} exercises",
                        color = ForceColors.Primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            onViewTodayWorkout(
                                todayWorkout.plannedWorkoutId
                            )
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {

                        Text(
                            text = "VIEW WORKOUT",
                            color = ForceColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            onStartTodayWorkout(
                                todayWorkout.plannedWorkoutId
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    ForceColors.Primary
                            )
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
                onClick = {
                    onNavigate(ForceDestination.AiCoach)
                },
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

private fun getGreeting(): String {

    val hour =
        LocalTime.now().hour

    return when (hour) {

        in 5..11 ->
            "Good morning 👋"

        in 12..16 ->
            "Good afternoon 👋"

        else ->
            "Good evening 👋"
    }
}

