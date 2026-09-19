package com.ngedo.force.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ngedo.force.designsystem.ForceColors
import com.ngedo.force.designsystem.ForceSpacing
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.width


private data class WorkoutExercise(
    val name: String,
    val target: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val mediaUrl: String
)

private fun workoutExerciseForName(
    exerciseName: String
): WorkoutExercise {

    return todaysWorkout.firstOrNull {
        it.name.equals(
            exerciseName,
            ignoreCase = true
        )
    } ?: WorkoutExercise(
        name = exerciseName,
        target = "Exercise Library",
        sets = 3,
        reps = "8-12",
        restSeconds = 90,
        mediaUrl = ""
    )
}

private val todaysWorkout = listOf(

    WorkoutExercise(
        name = "Barbell Bench Press",
        target = "Chest",
        sets = 4,
        reps = "8–10 reps",
        restSeconds = 90,
        mediaUrl = ""
    ),

    WorkoutExercise(
        name = "Incline Dumbbell Press",
        target = "Upper Chest",
        sets = 3,
        reps = "10–12 reps",
        restSeconds = 75,
        mediaUrl = ""
    ),

    WorkoutExercise(
        name = "Cable Fly",
        target = "Chest",
        sets = 3,
        reps = "12–15 reps",
        restSeconds = 60,
        mediaUrl = ""
    ),

    WorkoutExercise(
        name = "Triceps Pushdown",
        target = "Triceps",
        sets = 3,
        reps = "10–12 reps",
        restSeconds = 60,
        mediaUrl = ""
    )
)


@Composable
fun WorkoutScreen(
    workoutViewModel: ActiveWorkoutViewModel = hiltViewModel(),
    onExerciseLibraryClick: () -> Unit = {},
    onExerciseDetailsClick: (String) -> Unit = {},
    exercisesToAdd: List<String> = emptyList(),
    exerciseToRemove: String? = null,
    onExerciseRemoved: () -> Unit = {},
    onExerciseAdded: () -> Unit = {},
    onPlannedExerciseNamesChanged: (List<String>) -> Unit = {}
) {

    val uiState by workoutViewModel.uiState.collectAsState()

    var exerciseBeingEdited by remember {
        mutableStateOf<PlannedWorkoutExercise?>(null)
    }

    var exerciseBeingRemoved by remember {
        mutableStateOf<PlannedWorkoutExercise?>(null)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val snackbarScope = rememberCoroutineScope()



    LaunchedEffect(
        uiState.isWorkoutStarted,
        uiState.isWorkoutComplete,
        uiState.plannedExercises
    ) {
        if (
            !uiState.isWorkoutStarted &&
            !uiState.isWorkoutComplete &&
            uiState.plannedExercises.isEmpty()
        ) {
            workoutViewModel.initializePlannedExercises(
                todaysWorkout.map { exercise ->
                    PlannedWorkoutExercise(
                        name = exercise.name,
                        target = exercise.target,
                        sets = exercise.sets,
                        reps = exercise.reps,
                        restSeconds = exercise.restSeconds
                    )
                }
            )
        }
    }

    LaunchedEffect(
        exercisesToAdd
    ) {

        if (exercisesToAdd.isNotEmpty()) {

            exercisesToAdd.forEach { exerciseName ->

                workoutViewModel.addPlannedExercise(
                    exerciseName
                )
            }

            onExerciseAdded()
        }
    }

    LaunchedEffect(
        exerciseToRemove
    ) {
        exerciseToRemove?.let { exerciseName ->

            workoutViewModel.removePlannedExercise(
                exerciseName
            )

            onExerciseRemoved()
        }
    }

    LaunchedEffect(
        uiState.plannedExercises
    ) {
        onPlannedExerciseNamesChanged(
            uiState.plannedExercises.map {
                it.name
            }
        )
    }

    when {

        uiState.pendingWorkoutCompletion &&
                (
                        uiState.isNewWeightRecord ||
                                uiState.isNewRepRecord
                        ) &&
                uiState.currentExerciseIndex in uiState.exerciseNames.indices -> {

            ActiveWorkoutContent(
                uiState = uiState,

                onWeightChange = {
                    workoutViewModel.updateCurrentWeight(it)
                },

                onRepsChange = {
                    workoutViewModel.updateCurrentReps(it)
                },

                onDismissPersonalRecord = {
                    workoutViewModel.clearPersonalRecordNotification()
                },

                onCompleteSet = {},
                onSkipRest = {},
                onNextExercise = {}
            )
        }

        uiState.isWorkoutComplete -> {

            WorkoutCompleteContent(
                uiState = uiState,
                onFinishWorkout = {
                    workoutViewModel.finishWorkout()
                }
            )
        }

        uiState.isWorkoutStarted &&
                uiState.currentExerciseIndex in uiState.exerciseNames.indices -> {

            ActiveWorkoutContent(
                uiState = uiState,

                onWeightChange = {
                    workoutViewModel.updateCurrentWeight(it)
                },

                onRepsChange = {
                    workoutViewModel.updateCurrentReps(it)
                },

                onDismissPersonalRecord = {
                    workoutViewModel.clearPersonalRecordNotification()
                },

                onCompleteSet = {

                    val currentExerciseName =
                        uiState.exerciseNames.getOrNull(
                            uiState.currentExerciseIndex
                        ) ?: return@ActiveWorkoutContent

                    val exercise =
                        uiState.plannedExercises
                            .getOrNull(
                                uiState.currentExerciseIndex
                            ) ?: return@ActiveWorkoutContent

                    val nextExerciseName =
                        uiState.exerciseNames.getOrNull(
                            uiState.currentExerciseIndex + 1
                        )

                    val nextExercise =
                        uiState.plannedExercises
                            .getOrNull(
                                uiState.currentExerciseIndex + 1
                            )

                    workoutViewModel.completeSet(
                        totalSets = exercise.sets,
                        totalExercises =
                            uiState.exerciseNames.size,
                        restSeconds =
                            exercise.restSeconds,
                        nextExerciseTotalSets =
                            nextExercise?.sets ?: 1
                    )
                },

                onSkipRest = {

                    val currentExerciseName =
                        uiState.exerciseNames.getOrNull(
                            uiState.currentExerciseIndex
                        )

                    if (currentExerciseName != null) {

                        val exercise =
                            uiState.plannedExercises
                                .getOrNull(
                                    uiState.currentExerciseIndex
                                )

                        if (exercise != null) {

                            workoutViewModel.skipRest(
                                totalSets = exercise.sets,
                                totalExercises = uiState.exerciseNames.size
                            )
                        }
                    }
                },

                onNextExercise = {

                    val nextExerciseName =
                        uiState.exerciseNames.getOrNull(
                            uiState.currentExerciseIndex + 1
                        )

                    val nextExercise =
                        uiState.plannedExercises
                            .getOrNull(
                                uiState.currentExerciseIndex + 1
                            )

                    workoutViewModel.nextExercise(
                        totalExercises =
                            uiState.exerciseNames.size,
                        nextExerciseTotalSets =
                            nextExercise?.sets ?: 1
                    )
                }
            )
        }

        /*
     * Safety state:
     * If the workout is started but the exercise index
     * is outside the workout list, don't render a blank
     * screen.
     */
        uiState.isWorkoutStarted -> {

            ActiveWorkoutContent(
                uiState = uiState,

                onWeightChange = {
                    workoutViewModel.updateCurrentWeight(it)
                },

                onRepsChange = {
                    workoutViewModel.updateCurrentReps(it)
                },

                onDismissPersonalRecord = {
                    workoutViewModel.clearPersonalRecordNotification()
                },

                onCompleteSet = {

                    val currentExerciseName =
                        uiState.exerciseNames.getOrNull(
                            uiState.currentExerciseIndex
                        )

                    val exercise =
                        uiState.plannedExercises
                            .getOrNull(
                                uiState.currentExerciseIndex
                            )

                    if (exercise != null) {

                        val nextExercise =
                            uiState.plannedExercises
                                .getOrNull(
                                    uiState.currentExerciseIndex + 1
                                )

                        workoutViewModel.completeSet(
                            totalSets = exercise.sets,
                            totalExercises =
                                uiState.exerciseNames.size,
                            restSeconds =
                                exercise.restSeconds,
                            nextExerciseTotalSets =
                                nextExercise?.sets ?: 1
                        )
                    }
                },

                onSkipRest = {

                    val exercise =
                        uiState.plannedExercises
                            .getOrNull(
                                uiState.currentExerciseIndex
                            )

                    if (exercise != null) {

                        workoutViewModel.skipRest(
                            totalSets = exercise.sets,
                            totalExercises =
                                uiState.exerciseNames.size
                        )
                    }
                },

                onNextExercise = {

                    val nextExerciseName =
                        uiState.exerciseNames.getOrNull(
                            uiState.currentExerciseIndex + 1
                        )

                    val nextExercise =
                        uiState.plannedExercises
                            .getOrNull(
                                uiState.currentExerciseIndex + 1
                            )

                    workoutViewModel.nextExercise(
                        totalExercises =
                            uiState.exerciseNames.size,
                        nextExerciseTotalSets =
                            nextExercise?.sets ?: 1
                    )
                }
            )
        }


        /*
         * -------------------------------------------------
         * WORKOUT OVERVIEW
         * -------------------------------------------------
         */

        else -> {

            WorkoutOverview(
                plannedExercises = uiState.plannedExercises,

                onStartWorkout = {

                    workoutViewModel.startWorkout(
                        plannedExercises = uiState.plannedExercises
                    )
                },

                onExerciseLibraryClick =
                    onExerciseLibraryClick,

                onExerciseDetailsClick = { exercise ->

                    workoutViewModel.openExerciseDetails(
                        exerciseName = exercise.name,
                        onExerciseFound = { exerciseId ->

                            onExerciseDetailsClick(
                                exerciseId
                            )
                        }
                    )
                },

                onEditExercise = { exercise ->
                    exerciseBeingEdited = exercise
                },

                onRemoveExercise = { exercise ->
                    exerciseBeingRemoved = exercise
                },

                onMoveUp = { exercise ->
                    workoutViewModel.movePlannedExerciseUp(
                        exercise.name
                    )
                },

                onMoveDown = { exercise ->
                    workoutViewModel.movePlannedExerciseDown(
                        exercise.name
                    )
                }
            )
        }
    }

    exerciseBeingEdited?.let { exercise ->

        EditPlannedExerciseDialog(
            exercise = exercise,

            onDismiss = {
                exerciseBeingEdited = null
            },

            onSave = { sets, reps, restSeconds ->

                workoutViewModel.updatePlannedExercise(
                    exerciseName = exercise.name,
                    sets = sets,
                    reps = reps,
                    restSeconds = restSeconds
                )

                exerciseBeingEdited = null
            }


        )
    }

    exerciseBeingRemoved?.let { exercise ->

        AlertDialog(
            onDismissRequest = {
                exerciseBeingRemoved = null
            },

            containerColor = ForceColors.Surface,

            title = {
                Text(
                    text = "Remove Exercise?",
                    color = ForceColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    text = "Remove ${exercise.name} from today's workout?",
                    color = ForceColors.TextSecondary
                )
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        exerciseBeingRemoved = null
                    }
                ) {
                    Text(
                        text = "CANCEL",
                        color = ForceColors.TextSecondary
                    )
                }
            },

            confirmButton = {
                Button(
                    onClick = {

                        workoutViewModel.removePlannedExercise(
                            exercise.name
                        )

                        exerciseBeingRemoved = null

                        snackbarScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "${exercise.name} removed"
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB3261E)
                    )
                ) {
                    Text(
                        text = "REMOVE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 120.dp
                )
        )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 120.dp)
        )
    }
}



/*
 * =========================================================
 * WORKOUT OVERVIEW
 * =========================================================
 */
@Composable
private fun WorkoutOverview(
    plannedExercises: List<PlannedWorkoutExercise>,
    onStartWorkout: () -> Unit,
    onExerciseLibraryClick: () -> Unit,
    onExerciseDetailsClick: (PlannedWorkoutExercise) -> Unit,
    onEditExercise: (PlannedWorkoutExercise) -> Unit,
    onRemoveExercise: (PlannedWorkoutExercise) -> Unit,
    onMoveUp: (PlannedWorkoutExercise) -> Unit,
    onMoveDown: (PlannedWorkoutExercise) -> Unit
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForceColors.Background)
    ) {

        /*
         * SCROLLABLE AREA
         */

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(
                    horizontal = ForceSpacing.Large
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    ForceSpacing.Large
                )
        ) {

            item {

                Spacer(
                    modifier = Modifier.height(
                        ForceSpacing.Large
                    )
                )

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Today's Workout",
                        color = ForceColors.TextPrimary,
                        style =
                            MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Chest & Triceps",
                        color = ForceColors.Primary,
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text =
                            "${plannedExercises.size} exercises • Planned workout",
                        color = ForceColors.TextSecondary
                    )
                }
            }

            items(
                items = plannedExercises,
                key = { exercise ->
                    exercise.name
                }
            ) { exercise ->

                val workoutExercise =
                    WorkoutExercise(
                        name = exercise.name,
                        target = exercise.target,
                        sets = exercise.sets,
                        reps = exercise.reps,
                        restSeconds = exercise.restSeconds,
                        mediaUrl = ""
                    )

                WorkoutExerciseCard(
                    exercise = workoutExercise,

                    onDetailsClick = {
                        onExerciseDetailsClick(exercise)
                    },

                    onEditClick = {
                        onEditExercise(exercise)
                    },

                    onRemoveClick = {
                        onRemoveExercise(exercise)
                    },

                    onMoveUp = {
                        onMoveUp(exercise)
                    },

                    onMoveDown = {
                        onMoveDown(exercise)
                    }
                )

            }

            item {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            }

            /*
         * FIXED QUICK ACTIONS
         */

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ForceColors.Background)
                    .navigationBarsPadding()
                    .padding(
                        start = ForceSpacing.Large,
                        end = ForceSpacing.Large,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onExerciseLibraryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = ForceColors.Surface
                        ),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text(
                        text = "Exercise Library",
                        color = ForceColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onStartWorkout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = ForceColors.Primary
                        ),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text(
                        text = "Start Workout",
                        color = ForceColors.Background,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }


    /*
 * =========================================================
 * ACTIVE WORKOUT
 * =========================================================
 */

    @Composable
    private fun ActiveWorkoutContent(
        uiState: ActiveWorkoutUiState,
        onWeightChange: (String) -> Unit,
        onRepsChange: (String) -> Unit,
        onCompleteSet: () -> Unit,
        onSkipRest: () -> Unit,
        onNextExercise: () -> Unit,
        onDismissPersonalRecord: () -> Unit
    ) {

        val exercise =
            uiState.plannedExercises.getOrNull(
                uiState.currentExerciseIndex
            ) ?: return

        val totalSets =
            uiState.plannedExercises.sumOf {
                it.sets
            }
        val workoutProgress =
            calculateWorkoutProgress(
                completedSets = uiState.completedSets,
                totalSets = totalSets
            )

        val scrollState =
            rememberScrollState()

        /*
     * -------------------------------------------------
     * ROOT
     * -------------------------------------------------
     */

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    ForceColors.Background
                )
        ) {

            /*
         * -------------------------------------------------
         * MAIN OUTER COLUMN
         * -------------------------------------------------
         */

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        top = 16.dp,
                        end = 20.dp
                    )
            ) {

                /*
             * -------------------------------------------------
             * SCROLLABLE CONTENT
             * -------------------------------------------------
             */

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(
                            scrollState
                        )
                        .padding(
                            bottom = 160.dp
                        )
                ) {

                    /*
                 * -------------------------------------------------
                 * TOP INFORMATION
                 * -------------------------------------------------
                 */

                    Text(
                        text = "Active Workout",
                        color = ForceColors.TextPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Exercise " +
                                    "${uiState.currentExerciseIndex + 1} " +
                                    "of ${uiState.exerciseNames.size}",
                        color = ForceColors.TextSecondary
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    /*
                 * -------------------------------------------------
                 * WORKOUT PROGRESS
                 * -------------------------------------------------
                 */

                    Text(
                        text = "Workout Progress",
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    LinearProgressIndicator(
                        progress = {
                            workoutProgress
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = ForceColors.Primary,
                        trackColor = ForceColors.Surface
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "${uiState.completedSets} of " +
                                    "$totalSets sets • " +
                                    "${(workoutProgress * 100).toInt()}%",
                        color = ForceColors.TextSecondary
                    )

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    /*
                 * -------------------------------------------------
                 * CURRENT EXERCISE CARD
                 * -------------------------------------------------
                 */

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = ForceColors.Surface,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(20.dp)
                    ) {

                        Text(
                            text = "Current Exercise",
                            color = ForceColors.Primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = exercise.name,
                            color = ForceColors.TextPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        /*
                     * -------------------------------------------------
                     * EXERCISE TAGS
                     * -------------------------------------------------
                     */

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Surface(
                                color = ForceColors.Background,
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Text(
                                    text = exercise.target,
                                    color = ForceColors.Primary,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 7.dp
                                    ),
                                    style =
                                        MaterialTheme.typography.labelMedium
                                )
                            }

                            Surface(
                                color = ForceColors.Background,
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Text(
                                    text = "Compound",
                                    color = ForceColors.TextSecondary,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 7.dp
                                    ),
                                    style =
                                        MaterialTheme.typography.labelMedium
                                )
                            }

                            Surface(
                                color = ForceColors.Background,
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Text(
                                    text = "Strength",
                                    color = ForceColors.TextSecondary,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 7.dp
                                    ),
                                    style =
                                        MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        /*
                     * -------------------------------------------------
                     * EXERCISE MEDIA
                     * -------------------------------------------------
                     */

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(
                                    color = ForceColors.Background,
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {

                            val mediaUrl =
                                workoutExerciseForName(exercise.name).mediaUrl

                            if (mediaUrl.isNotBlank()) {

                                AsyncImage(
                                    model = mediaUrl,
                                    contentDescription = exercise.name,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            /*
                         * TARGET MUSCLE
                         */

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                                    .background(
                                        color = ForceColors.Surface.copy(
                                            alpha = 0.90f
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    )
                            ) {

                                Text(
                                    text = "TARGET MUSCLE",
                                    color = ForceColors.TextSecondary,
                                    style =
                                        MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = exercise.target,
                                    color = ForceColors.Primary,
                                    style =
                                        MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            /*
                         * INFORMATION BUTTON
                         */

                            Button(
                                onClick = {
                                    // Exercise information
                                    // will be added later.
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .height(42.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            ForceColors.Surface.copy(
                                                alpha = 0.90f
                                            )
                                    ),
                                shape = RoundedCornerShape(12.dp)
                            ) {

                                Text(
                                    text = "ⓘ",
                                    color = ForceColors.TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    /*
                 * -------------------------------------------------
                 * SET INFORMATION
                 * -------------------------------------------------
                 */

                    Text(
                        text =
                            "Set ${uiState.currentSet} " +
                                    "of ${exercise.sets}",
                        color = ForceColors.TextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = exercise.reps,
                        color = ForceColors.TextSecondary
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                    /*
 * -------------------------------------------------
 * PREVIOUS BEST
 * -------------------------------------------------
 */

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = ForceColors.Surface,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(
                                horizontal = 14.dp,
                                vertical = 12.dp
                            )
                    ) {

                        Text(
                            text = "Previous Best",
                            color = ForceColors.TextSecondary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        if (
                            uiState.previousBestWeight != null ||
                            uiState.previousBestReps != null
                        ) {

                            Text(
                                text = buildString {

                                    uiState.previousBestWeight?.let { weight ->
                                        append(
                                            if (weight % 1.0 == 0.0) {
                                                "${weight.toInt()} kg"
                                            } else {
                                                "$weight kg"
                                            }
                                        )
                                    }

                                    if (
                                        uiState.previousBestWeight != null &&
                                        uiState.previousBestReps != null
                                    ) {
                                        append(" × ")
                                    }

                                    uiState.previousBestReps?.let { reps ->
                                        append("$reps reps")
                                    }
                                },
                                color = ForceColors.Primary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                        } else {

                            Text(
                                text = "No previous record",
                                color = ForceColors.TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    uiState.trainingRecommendation?.let { recommendation ->

                        TrainingRecommendationCard(
                            recommendation = recommendation,
                            currentSet = uiState.currentSet
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )
                    }

                    /*
                 * -------------------------------------------------
                 * SET TRACKER
                 * -------------------------------------------------
                 */

                    SetTracker(
                        exercise = exercise,
                        uiState = uiState
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                    /*
 * -------------------------------------------------
 * NEW PERSONAL RECORD
 * -------------------------------------------------
 */

                    if (
                        uiState.isNewWeightRecord ||
                        uiState.isNewRepRecord
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
                                Arrangement.spacedBy(10.dp)
                        ) {

                            Text(
                                text = "🏆 New Personal Record",
                                color = ForceColors.Primary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            if (uiState.isNewWeightRecord) {

                                Text(
                                    text = "Heaviest Set",
                                    color = ForceColors.TextPrimary,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "Previous: " +
                                                formatPrSet(
                                                    weight =
                                                        uiState.previousRecordWeight,
                                                    reps =
                                                        uiState.previousRecordWeightReps
                                                ),
                                    color = ForceColors.TextSecondary
                                )

                                Text(
                                    text =
                                        "New: " +
                                                formatPrSet(
                                                    weight = uiState.newRecordWeight,
                                                    reps = uiState.newRecordReps
                                                ),
                                    color = ForceColors.Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (uiState.isNewRepRecord) {

                                if (uiState.isNewWeightRecord) {
                                    Spacer(
                                        modifier = Modifier.height(4.dp)
                                    )
                                }

                                Text(
                                    text = "Rep Record",
                                    color = ForceColors.TextPrimary,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "Previous: " +
                                                formatPrSet(
                                                    weight =
                                                        uiState.previousRecordRepsWeight,
                                                    reps =
                                                        uiState.previousRecordReps
                                                ),
                                    color = ForceColors.TextSecondary
                                )

                                Text(
                                    text =
                                        "New: " +
                                                formatPrSet(
                                                    weight = uiState.newRecordWeight,
                                                    reps = uiState.newRecordReps
                                                ),
                                    color = ForceColors.Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick =
                                    onDismissPersonalRecord,
                                modifier =
                                    Modifier.fillMaxWidth(),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            ForceColors.Primary
                                    ),
                                shape =
                                    RoundedCornerShape(12.dp)
                            ) {

                                Text(
                                    text = "Continue",
                                    color = ForceColors.Background,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )
                    }

                    /*
                 * -------------------------------------------------
                 * WEIGHT
                 * -------------------------------------------------
                 */

                    Text(
                        text = "Weight (kg)",
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    OutlinedTextField(
                        value = uiState.currentWeight,

                        onValueChange = { value ->
                            if (
                                value.isEmpty() ||
                                value.matches(Regex("^\\d*\\.?\\d*$"))
                            ) {
                                onWeightChange(value)
                            }
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true,

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),

                        placeholder = {
                            Text(
                                text = "Enter weight",
                                color = ForceColors.TextSecondary
                            )
                        },

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    ForceColors.Primary,

                                unfocusedBorderColor =
                                    ForceColors.TextSecondary,

                                focusedTextColor =
                                    ForceColors.TextPrimary,

                                unfocusedTextColor =
                                    ForceColors.TextPrimary,

                                cursorColor =
                                    ForceColors.Primary
                            ),

                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    /*
                 * -------------------------------------------------
                 * REPS
                 * -------------------------------------------------
                 */

                    Text(
                        text = "Reps",
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    OutlinedTextField(
                        value = uiState.currentReps,

                        onValueChange = { value ->
                            if (
                                value.isEmpty() ||
                                value.all { it.isDigit() }
                            ) {
                                onRepsChange(value)
                            }
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true,

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),

                        placeholder = {
                            Text(
                                text = "Enter reps",
                                color = ForceColors.TextSecondary
                            )
                        },

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    ForceColors.Primary,

                                unfocusedBorderColor =
                                    ForceColors.TextSecondary,

                                focusedTextColor =
                                    ForceColors.TextPrimary,

                                unfocusedTextColor =
                                    ForceColors.TextPrimary,

                                cursorColor =
                                    ForceColors.Primary
                            ),

                        shape = RoundedCornerShape(14.dp)
                    )

                    /*
                 * -------------------------------------------------
                 * REST TIMER
                 * -------------------------------------------------
                 */

                    if (uiState.isResting) {

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            horizontalAlignment =
                                Alignment.CenterHorizontally,
                            verticalArrangement =
                                Arrangement.Center
                        ) {

                            Text(
                                text = "REST",
                                color = ForceColors.TextSecondary,
                                style =
                                    MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    formatRestTime(
                                        uiState.restSecondsRemaining
                                    ),
                                color = ForceColors.Primary,
                                style =
                                    MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                /*
             * -------------------------------------------------
             * FIXED BOTTOM ACTION AREA
             * -------------------------------------------------
             */

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(
                            bottom = 8.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    if (uiState.isResting) {

                        /*
                     * SKIP REST
                     */

                        Button(
                            onClick = onSkipRest,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        ForceColors.Surface
                                ),
                            shape = RoundedCornerShape(14.dp)
                        ) {

                            Text(
                                text = "Skip Rest",
                                color = ForceColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    } else {

                        /*
                     * COMPLETE SET
                     */

                        Button(
                            onClick = onCompleteSet,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        ForceColors.Primary
                                ),
                            shape = RoundedCornerShape(14.dp)
                        ) {

                            Text(
                                text = "Complete Set",
                                color = ForceColors.Background,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        /*
                     * NEXT EXERCISE
                     */

                        Button(
                            onClick = onNextExercise,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        ForceColors.Surface
                                ),
                            shape = RoundedCornerShape(14.dp)
                        ) {

                            Text(
                                text = "Next Exercise",
                                color = ForceColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    /*
 * =========================================================
 * WORKOUT EXERCISE CARD
 * =========================================================
 */
    @Composable
    private fun EditPlannedExerciseDialog(
        exercise: PlannedWorkoutExercise,
        onDismiss: () -> Unit,
        onSave: (
            sets: Int,
            reps: String,
            restSeconds: Int
        ) -> Unit
    ) {

        var setsText by remember(exercise) {
            mutableStateOf(
                exercise.sets.toString()
            )
        }

        var repsText by remember(exercise) {

            val firstRepNumber =
                Regex("\\d+")
                    .find(exercise.reps)
                    ?.value
                    ?: ""

            mutableStateOf(firstRepNumber)
        }

        var restText by remember(exercise) {
            mutableStateOf(
                exercise.restSeconds.toString()
            )
        }

        AlertDialog(
            onDismissRequest = onDismiss,

            containerColor =
                ForceColors.Surface,

            title = {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(4.dp)
                ) {

                    Text(
                        text = "Edit Exercise",
                        color = ForceColors.TextPrimary,
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = exercise.name,
                        color = ForceColors.Primary,
                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }
            },

            text = {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(16.dp)
                ) {

                    OutlinedTextField(
                        value = setsText,

                        onValueChange = { value ->
                            if (
                                value.isEmpty() ||
                                value.all { it.isDigit() }
                            ) {
                                setsText = value
                            }
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Sets")
                        },

                        singleLine = true,

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            )
                    )

                    OutlinedTextField(
                        value = repsText.filter { it.isDigit() },

                        onValueChange = { value ->
                            if (
                                value.isEmpty() ||
                                value.all { it.isDigit() }
                            ) {
                                repsText = value
                            }
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Target Reps")
                        },

                        singleLine = true,

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            )
                    )

                    OutlinedTextField(
                        value = restText,

                        onValueChange = { value ->
                            if (
                                value.isEmpty() ||
                                value.all { it.isDigit() }
                            ) {
                                restText = value
                            }
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Rest (seconds)")
                        },

                        singleLine = true,

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            )
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = onDismiss
                ) {

                    Text(
                        text = "CANCEL",
                        color =
                            ForceColors.TextSecondary
                    )
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        val sets =
                            setsText.toIntOrNull()

                        val restSeconds =
                            restText.toIntOrNull()

                        if (
                            sets != null &&
                            sets > 0 &&
                            repsText.isNotBlank() &&
                            restSeconds != null &&
                            restSeconds >= 0
                        ) {

                            onSave(
                                sets,
                                repsText.trim(),
                                restSeconds
                            )
                        }
                    },

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                ForceColors.Primary
                        ),

                    shape =
                        RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "SAVE",
                        color =
                            ForceColors.Background,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        )
    }

@Composable
private fun WorkoutExerciseCard(
    exercise: WorkoutExercise,
    onDetailsClick: () -> Unit,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
){

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ForceColors.Surface,
        shape = RoundedCornerShape(20.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT REORDER RAIL
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                TextButton(
                    onClick = onMoveUp,
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "▲",
                        color = ForceColors.Primary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onMoveDown,
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "▼",
                        color = ForceColors.Primary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            // MAIN CARD CONTENT
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = exercise.name,
                    color = ForceColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = exercise.target,
                    color = ForceColors.Primary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // PRESCRIPTION
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    WorkoutPrescriptionValue(
                        label = "SETS",
                        value = exercise.sets.toString()
                    )

                    WorkoutPrescriptionValue(
                        label = "REPS",
                        value = exercise.reps
                    )

                    WorkoutPrescriptionValue(
                        label = "REST",
                        value = "${exercise.restSeconds}s"
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // ACTIONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = onDetailsClick
                    ) {
                        Text(
                            text = "DETAILS",
                            color = ForceColors.Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = onEditClick
                    ) {
                        Text(
                            text = "EDIT",
                            color = Color(0xFFFFC857),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = onRemoveClick
                    ) {
                        Text(
                            text = "REMOVE",
                            color = ForceColors.TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
    @Composable
    private fun WorkoutPrescriptionValue(
        label: String,
        value: String
    ) {

        Column(
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = label,
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = value,
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

@Composable
private fun SetTracker(
    exercise: PlannedWorkoutExercise,
    uiState: ActiveWorkoutUiState
){
        val completedSets =
            uiState.completedSetsForCurrentExercise

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ForceColors.Surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {

            /*
         * -------------------------------------------------
         * SET TRACKER HEADER
         * -------------------------------------------------
         */

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Set Tracker",
                    color = ForceColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${completedSets.size}/${exercise.sets}",
                    color = ForceColors.Primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            /*
         * -------------------------------------------------
         * TABLE HEADER
         * -------------------------------------------------
         */

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ForceColors.Background,
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp
                        )
                    )
                    .padding(
                        vertical = 10.dp,
                        horizontal = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TrackerHeader(
                    text = "SET",
                    modifier = Modifier.weight(1f)
                )

                TrackerHeader(
                    text = "KG",
                    modifier = Modifier.weight(1f)
                )

                TrackerHeader(
                    text = "REPS",
                    modifier = Modifier.weight(1f)
                )

                TrackerHeader(
                    text = "STATUS",
                    modifier = Modifier.weight(1.2f)
                )
            }

            /*
         * -------------------------------------------------
         * SET ROWS
         * -------------------------------------------------
         */

            for (setNumber in 1..exercise.sets) {

                val savedSet =
                    completedSets.firstOrNull {
                        it.setNumber == setNumber
                    }

                val isCompleted =
                    savedSet != null

                val isCurrent =
                    setNumber == uiState.currentSet &&
                            !uiState.isResting

                val rowBackground =
                    when {
                        isCurrent ->
                            ForceColors.Primary.copy(
                                alpha = 0.12f
                            )

                        else ->
                            ForceColors.Background
                    }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            rowBackground
                        )
                        .then(
                            if (isCurrent) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = ForceColors.Primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .padding(
                            vertical = 10.dp,
                            horizontal = 8.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    /*
                 * SET NUMBER
                 */

                    TrackerCell(
                        text = setNumber.toString(),
                        modifier = Modifier.weight(1f),
                        highlighted = isCurrent
                    )

                    /*
                 * WEIGHT
                 */

                    TrackerCell(
                        text =
                            if (savedSet != null) {
                                if (savedSet.weight > 0) {
                                    savedSet.weight
                                        .toString()
                                } else {
                                    "-"
                                }
                            } else {
                                "-"
                            },
                        modifier = Modifier.weight(1f),
                        highlighted = isCurrent
                    )

                    /*
                 * REPS
                 */

                    TrackerCell(
                        text =
                            if (savedSet != null) {
                                if (savedSet.reps > 0) {
                                    savedSet.reps
                                        .toString()
                                } else {
                                    "-"
                                }
                            } else {
                                "-"
                            },
                        modifier = Modifier.weight(1f),
                        highlighted = isCurrent
                    )

                    /*
                 * STATUS
                 */

                    Row(
                        modifier = Modifier.weight(1.2f),
                        horizontalArrangement =
                            Arrangement.Center,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        when {

                            isCompleted -> {

                                Text(
                                    text = "✓",
                                    color =
                                        ForceColors.Primary,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleLarge,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }

                            isCurrent -> {

                                Text(
                                    text = "●",
                                    color =
                                        ForceColors.Primary,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyLarge
                                )
                            }

                            else -> {

                                Text(
                                    text = "○",
                                    color =
                                        ForceColors.TextSecondary,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /*
 * =========================================================
 * WORKOUT COMPLETE
 * =========================================================
 */
    @Composable
    private fun TrackerHeader(
        text: String,
        modifier: Modifier = Modifier
    ) {
        Text(
            text = text,
            modifier = modifier,
            color = ForceColors.TextSecondary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }

    @Composable
    private fun TrackerCell(
        text: String,
        modifier: Modifier = Modifier,
        highlighted: Boolean = false
    ) {
        Text(
            text = text,
            modifier = modifier,
            color =
                if (highlighted) {
                    ForceColors.Primary
                } else {
                    ForceColors.TextPrimary
                },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight =
                if (highlighted) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
        )
    }

    @Composable
    private fun WorkoutCompleteContent(
        uiState: ActiveWorkoutUiState,
        onFinishWorkout: () -> Unit
    ) {

        val totalSets =
            uiState.plannedExercises.sumOf {
                it.sets
            }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    ForceColors.Background
                )
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(
                        ForceSpacing.Large
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(
                        ForceSpacing.Large
                    )
            ) {

                /*
             * -------------------------------------------------
             * HEADER
             * -------------------------------------------------
             */

                item {

                    Column(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "Workout Complete",

                            color =
                                ForceColors.TextPrimary,

                            style =
                                MaterialTheme.typography.headlineLarge,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text = "Great work! 🎉",

                            color =
                                ForceColors.Primary,

                            style =
                                MaterialTheme.typography.titleLarge,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text = "Chest & Triceps",

                            color =
                                ForceColors.TextSecondary
                        )
                    }
                }


                /*
             * -------------------------------------------------
             * SUMMARY
             * -------------------------------------------------
             */

                item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color =
                                    ForceColors.Surface,

                                shape =
                                    RoundedCornerShape(
                                        20.dp
                                    )
                            )
                            .padding(
                                ForceSpacing.Medium
                            ),

                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = "Workout Summary",

                            color =
                                ForceColors.TextPrimary,

                            style =
                                MaterialTheme.typography.titleLarge,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceEvenly
                        ) {

                            SummaryItem(
                                label = "Exercises",
                                value =
                                    uiState.exerciseNames
                                        .size
                                        .toString()
                            )

                            SummaryItem(
                                label = "Sets",
                                value =
                                    uiState
                                        .completedSets
                                        .toString()
                            )

                            SummaryItem(
                                label = "Planned",
                                value =
                                    "$totalSets sets"
                            )
                        }
                    }
                }


                /*
             * -------------------------------------------------
             * COMPLETED EXERCISES
             * -------------------------------------------------
             */

                item {

                    Text(
                        text = "Completed Exercises",

                        color =
                            ForceColors.TextPrimary,

                        style =
                            MaterialTheme.typography.titleLarge,

                        fontWeight =
                            FontWeight.Bold
                    )
                }


                items(
                    items = uiState.exerciseNames,
                    key = { exerciseName ->
                        exerciseName
                    }
                ) { exerciseName ->

                    val exercise =
                        uiState.plannedExercises
                            .firstOrNull {
                                it.name.equals(
                                    exerciseName,
                                    ignoreCase = true
                                )
                            } ?: return@items

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = ForceColors.Surface,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(ForceSpacing.Medium),

                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        val wasCompleted =
                            uiState.completedExerciseNames.any {
                                it.equals(
                                    exercise.name,
                                    ignoreCase = true
                                )
                            }

                        Text(
                            text =
                                if (wasCompleted) {
                                    "✓ ${exercise.name}"
                                } else {
                                    exercise.name
                                },

                            color =
                                if (wasCompleted) {
                                    ForceColors.TextPrimary
                                } else {
                                    ForceColors.TextSecondary
                                },

                            fontWeight = FontWeight.Bold
                        )
                        if (!wasCompleted) {

                            Text(
                                text = "Skipped",
                                color = ForceColors.TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            text =
                                "${exercise.sets} sets • " +
                                        exercise.reps,
                            color = ForceColors.TextSecondary
                        )
                    }
                }
            }


            /*
         * -------------------------------------------------
         * FIXED FINISH BUTTON
         * -------------------------------------------------
         */

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(
                        horizontal =
                            ForceSpacing.Large,

                        vertical = 8.dp
                    )
            ) {

                Button(
                    onClick =
                        onFinishWorkout,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                ForceColors.Primary
                        ),

                    shape =
                        RoundedCornerShape(14.dp)
                ) {

                    Text(
                        text = "Finish Workout",

                        color =
                            ForceColors.Background,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }


    /*
 * =========================================================
 * SUMMARY ITEM
 * =========================================================
 */

    @Composable
    private fun SummaryItem(
        label: String,
        value: String
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = value,

                color =
                    ForceColors.Primary,

                style =
                    MaterialTheme.typography.titleLarge,

                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = label,

                color =
                    ForceColors.TextSecondary
            )
        }
    }


    /*
 * =========================================================
 * WORKOUT PROGRESS
 * =========================================================
 */

    private fun calculateWorkoutProgress(
        completedSets: Int,
        totalSets: Int
    ): Float {

        if (totalSets == 0) {
            return 0f
        }

        return (
                completedSets.toFloat() /
                        totalSets.toFloat()
                ).coerceIn(
                0f,
                1f
            )
    }


    /*
 * =========================================================
 * REST TIMER FORMAT
 * =========================================================
 */

    private fun formatRestTime(
        seconds: Int
    ): String {

        val minutes =
            seconds / 60

        val remainingSeconds =
            seconds % 60

        return "%02d:%02d".format(
            minutes,
            remainingSeconds
        )
    }

    @Composable
    private fun TrainingRecommendationCard(
        recommendation: TrainingRecommendation,
        currentSet: Int
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ForceColors.Surface,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(
                    horizontal = 14.dp,
                    vertical = 14.dp
                )
        ) {

            /*
         * Header
         */

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "FORCE RECOMMENDS",
                        color = ForceColors.Primary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )


                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = when (
                            recommendation.recommendationType
                        ) {
                            TrainingRecommendationType.RAMP_UP ->
                                "Warm-up / Ramp-up"

                            TrainingRecommendationType.RETURNING_AFTER_BREAK ->
                                "Return Session"

                            TrainingRecommendationType.MATCH_PREVIOUS ->
                                "Working Set"

                            TrainingRecommendationType.PROGRESSION ->
                                "Progression"

                            TrainingRecommendationType.REDUCE_LOAD ->
                                "Reduce Load"

                            TrainingRecommendationType.NO_HISTORY ->
                                "Starting Point"
                        },
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )


            /*
         * Recommendation numbers
         */

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            recommendation.suggestedWeight
                                ?.let { weight ->
                                    "${formatRecommendedWeight(weight)} kg"
                                }
                                ?: "—",
                        color = ForceColors.Primary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Suggested Weight",
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            recommendation.suggestedReps
                                ?.let { reps ->

                                    if (currentSet == 1) {
                                        "$reps reps"
                                    } else {
                                        "$reps+ reps"
                                    }
                                }
                                ?: "—",
                        color = ForceColors.Primary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Suggested Reps",
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )


            /*
         * Explanation
         */

            Text(
                text = recommendation.reason,
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

}

private fun formatRecommendedWeight(
    weight: Double
): String {

    return if (weight % 1.0 == 0.0) {
        weight.toInt().toString()
    } else {
        weight.toString()
    }
}

private fun formatPrSet(
    weight: Double?,
    reps: Int?
): String {

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