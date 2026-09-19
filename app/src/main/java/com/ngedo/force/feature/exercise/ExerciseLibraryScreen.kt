package com.ngedo.force.feature.exercise

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ngedo.force.designsystem.ForceColors
import androidx.compose.foundation.clickable
import com.ngedo.force.data.local.entity.ExerciseEntity
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Box


@Composable
fun ExerciseLibraryScreen(
    onExerciseClick: (ExerciseEntity) -> Unit = {},
    onAddExercise: (ExerciseEntity) -> Unit = {},
    onRemoveExercise: (ExerciseEntity) -> Unit = {},
    addedExerciseNames: Set<String> = emptySet(),
    viewModel: ExerciseLibraryViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    LaunchedEffect(
        uiState.favoriteMessage
    ) {

        val message =
            uiState.favoriteMessage

        if (message != null) {

            snackbarHostState.showSnackbar(
                message = message
            )

            viewModel.clearFavoriteMessage()
        }
    }

    val searchQuery =
        uiState.searchQuery.trim()

    val sortedExercises =
        uiState.exercises.sortedWith(
            compareByDescending<ExerciseEntity> {
                uiState.favoriteExercises.contains(it.name)
            }.thenBy {
                it.name.lowercase()
            }
        )

    val filteredExercises =
        remember(
            uiState.exercises,
            uiState.favoriteExercises,
            searchQuery
        ) {

            val sortedExercises =
                uiState.exercises.sortedWith(
                    compareByDescending<ExerciseEntity> {
                        uiState.favoriteExercises.contains(
                            it.name
                        )
                    }.thenBy {
                        it.name.lowercase()
                    }
                )

            if (searchQuery.isBlank()) {

                sortedExercises

            } else {

                sortedExercises.filter { exercise ->

                    exercise.name.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||
                            exercise.primaryMuscle.contains(
                                searchQuery,
                                ignoreCase = true
                            ) ||
                            exercise.equipment.contains(
                                searchQuery,
                                ignoreCase = true
                            )
                }
            }
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

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
            Arrangement.spacedBy(12.dp)
    ) {

        item {

            Text(
                text = "Exercise Library",
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Browse exercises by name, muscle group or equipment.",
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

        }

        item {

            BasicTextField(
                value = uiState.searchQuery,
                onValueChange = {
                    viewModel.updateSearchQuery(it)
                },
                singleLine = true,
                cursorBrush =
                    SolidColor(
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
                                color = ForceColors.Surface,
                                shape =
                                    RoundedCornerShape(14.dp)
                            )
                            .padding(
                                horizontal = 14.dp,
                                vertical = 14.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            if (
                                uiState.searchQuery.isBlank()
                            ) {

                                Text(
                                    text =
                                        "Search exercises...",
                                    color =
                                        ForceColors.TextSecondary,
                                    style =
                                        MaterialTheme.typography.bodyMedium
                                )
                            }

                            innerTextField()
                        }
                    }
                }
            )
        }

        if (uiState.isLoading) {

            item {

                Text(
                    text = "Loading exercises...",
                    color = ForceColors.TextSecondary
                )
            }

        } else if (
            filteredExercises.isEmpty()
        ) {

            item {

                Text(
                    text = "No exercises found.",
                    color = ForceColors.TextSecondary
                )
            }

        } else {

            items(
                items = filteredExercises,
                key = { exercise ->
                    exercise.id
                }
            ) { exercise ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = ForceColors.Surface,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            onExerciseClick(exercise)
                        }
                        .padding(14.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(4.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = exercise.name,
                            color = ForceColors.TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            val isAdded =
                                addedExerciseNames.any {
                                    it.equals(
                                        exercise.name,
                                        ignoreCase = true
                                    )
                                }

                            Text(
                                text =
                                    if (
                                        uiState.favoriteExercises.contains(
                                            exercise.name
                                        )
                                    ) {
                                        "★"
                                    } else {
                                        "☆"
                                    },
                                color =
                                    if (
                                        uiState.favoriteExercises.contains(
                                            exercise.name
                                        )
                                    ) {
                                        Color(0xFFFFD54F)
                                    } else {
                                        ForceColors.TextSecondary
                                    },
                                style =
                                    MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.toggleFavoriteExercise(
                                            exercise.name
                                        )
                                    }
                                    .padding(
                                        start = 12.dp,
                                        end = 14.dp
                                    )
                            )

                            if (isAdded) {

                                Text(
                                    text = "ADDED",
                                    color = ForceColors.TextSecondary,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable {
                                            onRemoveExercise(exercise)
                                        }
                                        .padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        )
                                )

                            } else {

                                Text(
                                    text = "+",
                                    color = ForceColors.TextSecondary,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .clickable {
                                            onAddExercise(exercise)
                                        }
                                        .padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        )
                                )
                            }
                        }
                    }

                    Text(
                        text =
                            "${exercise.primaryMuscle} • ${exercise.equipment}",
                        color = ForceColors.Primary,
                        style =
                            MaterialTheme.typography.bodySmall
                    )

                    if (
                        exercise.secondaryMuscles
                            .isNotBlank()
                    ) {

                        Text(
                            text =
                                "Also: ${exercise.secondaryMuscles}",
                            color =
                                ForceColors.TextSecondary,
                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

    }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .padding(16.dp)
        )
}

}