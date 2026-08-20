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

private data class WorkoutExercise(
    val name: String,
    val target: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val mediaUrl: String
)


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
        name = "Tricep Pushdown",
        target = "Triceps",
        sets = 3,
        reps = "10–12 reps",
        restSeconds = 60,
        mediaUrl = ""
    )
)


@Composable
fun WorkoutScreen(
    workoutViewModel: ActiveWorkoutViewModel = hiltViewModel()
) {

    val uiState by workoutViewModel.uiState.collectAsState()

    when {

        /*
         * -------------------------------------------------
         * WORKOUT COMPLETE
         * -------------------------------------------------
         */

        uiState.isWorkoutComplete -> {

            WorkoutCompleteContent(
                uiState = uiState,
                onFinishWorkout = {
                    workoutViewModel.finishWorkout()
                }
            )
        }


        /*
         * -------------------------------------------------
         * ACTIVE WORKOUT
         * -------------------------------------------------
         */

        uiState.isWorkoutStarted &&
                uiState.currentExerciseIndex in todaysWorkout.indices -> {

            ActiveWorkoutContent(
                uiState = uiState,

                onWeightChange = {
                    workoutViewModel.updateCurrentWeight(it)
                },

                onRepsChange = {
                    workoutViewModel.updateCurrentReps(it)
                },

                onCompleteSet = {

                    val exercise =
                        todaysWorkout[uiState.currentExerciseIndex]

                    workoutViewModel.completeSet(
                        totalSets = exercise.sets,
                        totalExercises = todaysWorkout.size,
                        restSeconds = exercise.restSeconds
                    )
                },

                onSkipRest = {

                    val exercise =
                        todaysWorkout[uiState.currentExerciseIndex]

                    workoutViewModel.skipRest(
                        totalSets = exercise.sets,
                        totalExercises = todaysWorkout.size
                    )
                },

                onNextExercise = {

                    workoutViewModel.nextExercise(
                        todaysWorkout.size
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

            WorkoutCompleteContent(
                uiState = uiState,
                onFinishWorkout = {
                    workoutViewModel.finishWorkout()
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
                onStartWorkout = {

                    workoutViewModel.startWorkout(
                        exerciseNames = todaysWorkout.map {
                            it.name
                        }
                    )
                }
            )
        }
    }
}


/*
 * =========================================================
 * WORKOUT OVERVIEW
 * =========================================================
 */

@Composable
private fun WorkoutOverview(
    onStartWorkout: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ForceColors.Background
            )
    ) {

        /*
         * -------------------------------------------------
         * SCROLLABLE CONTENT
         * -------------------------------------------------
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
                        text = "4 exercises • ~45 minutes",
                        color = ForceColors.TextSecondary
                    )
                }
            }


            items(todaysWorkout) { exercise ->

                WorkoutExerciseCard(
                    exercise = exercise
                )
            }


            item {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }
        }


        /*
         * -------------------------------------------------
         * FIXED START WORKOUT BUTTON
         * -------------------------------------------------
         */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    horizontal = ForceSpacing.Large,
                    vertical = 8.dp
                )
        ) {

            Button(
                onClick = onStartWorkout,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        ForceColors.Primary
                ),

                shape =
                    RoundedCornerShape(14.dp)
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
    onNextExercise: () -> Unit
) {

    val exercise =
        todaysWorkout[uiState.currentExerciseIndex]

    val workoutProgress =
        calculateWorkoutProgress(
            uiState.completedSets
        )

    val totalSets =
        todaysWorkout.sumOf {
            it.sets
        }

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
                                "of ${todaysWorkout.size}",
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

                        if (exercise.mediaUrl.isNotBlank()) {

                            AsyncImage(
                                model = exercise.mediaUrl,
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
private fun WorkoutExerciseCard(
    exercise: WorkoutExercise
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = ForceColors.Surface,
                shape =
                    RoundedCornerShape(20.dp)
            )
            .padding(
                ForceSpacing.Medium
            ),

        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = exercise.name,
            color =
                ForceColors.TextPrimary,

            style =
                MaterialTheme.typography.titleMedium,

            fontWeight =
                FontWeight.Bold
        )

        Text(
            text = exercise.target,
            color =
                ForceColors.Primary
        )

        Text(
            text =
                "${exercise.sets} sets • " +
                        exercise.reps,

            color =
                ForceColors.TextSecondary
        )

        Text(
            text =
                "${exercise.restSeconds} sec rest",

            color =
                ForceColors.TextSecondary
        )
    }
}
@Composable
private fun SetTracker(
    exercise: WorkoutExercise,
    uiState: ActiveWorkoutUiState
) {
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
        todaysWorkout.sumOf {
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
                                todaysWorkout
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


            items(todaysWorkout) { exercise ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color =
                                ForceColors.Surface,

                            shape =
                                RoundedCornerShape(
                                    16.dp
                                )
                        )
                        .padding(
                            ForceSpacing.Medium
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text =
                            "✓ ${exercise.name}",

                        color =
                            ForceColors.TextPrimary,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "${exercise.sets} sets • " +
                                    exercise.reps,

                        color =
                            ForceColors.TextSecondary
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
    completedSets: Int
): Float {

    val totalSets =
        todaysWorkout.sumOf {
            it.sets
        }

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