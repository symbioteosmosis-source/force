package com.ngedo.force.feature.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.ngedo.force.designsystem.ForceColors
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.graphics.Color
import com.ngedo.force.data.local.entity.ExerciseEntity

@Composable
fun ExerciseDetailScreen(
    onBack: () -> Unit = {},
    onUseInWorkout: (ExerciseEntity) -> Unit = {},
    addedExerciseNames: Set<String> = emptySet(),
    viewModel: ExerciseDetailViewModel = hiltViewModel()
){

    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator(
                color = ForceColors.Primary
            )
        }

        return
    }

    val exercise = uiState.exercise ?: return

    val isInWorkout =
        addedExerciseNames.any { exerciseName ->
            exerciseName.equals(
                exercise.name,
                ignoreCase = true
            )
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForceColors.Background)
            .statusBarsPadding()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = ForceColors.Surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {

            Text(
                "← Back",
                color = ForceColors.TextPrimary
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = exercise.name,
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text =
                    if (uiState.isFavorite) {
                        "★"
                    } else {
                        "☆"
                    },
                color =
                    if (uiState.isFavorite) {
                        Color(0xFFFFD54F)
                    } else {
                        ForceColors.TextSecondary
                    },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .clickable {
                        viewModel.toggleFavorite()
                    }
                    .padding(start = 12.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = exercise.primaryMuscle,
            color = ForceColors.Primary,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        DetailCard(
            title = "Secondary Muscles",
            value = exercise.secondaryMuscles.ifBlank { "None" }
        )

        DetailCard(
            title = "Equipment",
            value = exercise.equipment
        )

        DetailCard(
            title = "Difficulty",
            value = exercise.difficulty
        )

        DetailCard(
            title = "Movement Type",
            value = exercise.movementType
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "How to Perform",
            color = ForceColors.TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ForceColors.Surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(18.dp)
        ) {

            Text(
                text = exercise.instructions,
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Button(
            onClick = {
                if (!isInWorkout) {
                    onUseInWorkout(exercise)
                }
            },

            enabled = !isInWorkout,

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = ForceColors.Primary,
                disabledContainerColor = ForceColors.Surface,
                disabledContentColor = ForceColors.TextSecondary
            ),

            shape = RoundedCornerShape(16.dp)
        ) {

            Text(
                text =
                    if (isInWorkout) {
                        "ADDED"
                    } else {
                        "ADD TO WORKOUT"
                    },
                color =
                    if (isInWorkout) {
                        ForceColors.TextSecondary
                    } else {
                        ForceColors.Background
                    },
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}

@Composable
private fun DetailCard(
    title: String,
    value: String
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(
                color = ForceColors.Surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = title,
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = value,
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}