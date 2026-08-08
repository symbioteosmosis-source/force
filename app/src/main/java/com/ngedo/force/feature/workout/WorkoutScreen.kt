package com.ngedo.force.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ngedo.force.designsystem.ForceColors
import com.ngedo.force.designsystem.ForceSpacing
import com.ngedo.force.navigation.ForceDestination
private data class WorkoutExercise(
    val name: String,
    val target: String,
    val sets: String,
    val reps: String,
    val rest: String
)

private val todaysWorkout = listOf(
    WorkoutExercise(
        name = "Barbell Bench Press",
        target = "Chest",
        sets = "4 sets",
        reps = "8–10 reps",
        rest = "90 sec rest"
    ),
    WorkoutExercise(
        name = "Incline Dumbbell Press",
        target = "Upper Chest",
        sets = "3 sets",
        reps = "10–12 reps",
        rest = "75 sec rest"
    ),
    WorkoutExercise(
        name = "Cable Fly",
        target = "Chest",
        sets = "3 sets",
        reps = "12–15 reps",
        rest = "60 sec rest"
    ),
    WorkoutExercise(
        name = "Tricep Pushdown",
        target = "Triceps",
        sets = "3 sets",
        reps = "10–12 reps",
        rest = "60 sec rest"
    )
)

@Composable
fun WorkoutScreen() {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForceColors.Background)
            .padding(ForceSpacing.Large),
        verticalArrangement = Arrangement.spacedBy(
            ForceSpacing.Large
        )
    ) {

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Today's Workout",
                    color = ForceColors.TextPrimary
                )

                Text(
                    text = "Chest & Triceps",
                    color = ForceColors.Primary
                )

                Text(
                    text = "4 exercises • ~45 minutes",
                    color = ForceColors.TextSecondary
                )
            }
        }

        items(todaysWorkout) { exercise ->
            WorkoutExerciseCard(exercise)
        }

        item {
            Button(
                onClick = {
                    // Active workout functionality will be added next.
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForceColors.Primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Start Workout",
                    color = ForceColors.Background
                )
            }
        }
    }
}

@Composable
private fun WorkoutExerciseCard(
    exercise: WorkoutExercise
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = ForceColors.Surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(ForceSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = exercise.name,
            color = ForceColors.TextPrimary
        )

        Text(
            text = exercise.target,
            color = ForceColors.Primary
        )

        Text(
            text = "${exercise.sets} • ${exercise.reps}",
            color = ForceColors.TextSecondary
        )

        Text(
            text = exercise.rest,
            color = ForceColors.TextSecondary
        )
    }
}