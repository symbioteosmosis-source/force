package com.ngedo.force.feature.workout.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ngedo.force.designsystem.ForceColors
import androidx.compose.foundation.clickable
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.LaunchedEffect
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.activity.compose.BackHandler

@Composable
fun MonthlyPlanScreen(
    onBack: () -> Unit = {},
    onAddExercises: (Int) -> Unit = {},
    onExerciseDetails: (String, Int) -> Unit = { _, _ -> },
    onStartWorkout: (Long) -> Unit = {},
    viewModel: MonthlyPlanViewModel = hiltViewModel()
) {

    val uiState by
    viewModel.uiState.collectAsState()

    val context = LocalContext.current


    LaunchedEffect(
        uiState.showSavedConfirmation
    ) {
        if (uiState.showSavedConfirmation) {

            Toast.makeText(
                context,
                "Workout saved",
                Toast.LENGTH_SHORT
            ).show()

            viewModel.clearSavedConfirmation()
        }
    }

    LaunchedEffect(
        uiState.isSaved
    ) {
        if (uiState.isSaved) {

            delay(500)

            Toast.makeText(
                context,
                "Plan saved successfully",
                Toast.LENGTH_SHORT
            ).show()

            delay(300)

            onBack()
        }
    }

    LaunchedEffect(
        uiState.isLoadingPlan,
        uiState.hasActivePlan,
        uiState.hasDraft
    ) {
        if (
            !uiState.isLoadingPlan &&
            uiState.hasActivePlan &&
            !uiState.hasDraft
        ) {
            viewModel.openLoadedPlan()
        }
    }

    if (uiState.isDraftRecoveryPending) {

        MonthlyPlanDraftRecovery(
            planName = uiState.planName,

            selectedDays =
                uiState.trainingDays.size,

            programmedDays =
                uiState.workoutDays.count {
                    it.day in uiState.trainingDays &&
                            it.exercises.isNotEmpty()
                },

            onContinue = {
                viewModel.continueDraft()
            },

            onDiscard = {
                viewModel.discardDraft()
            },

            onBack = onBack
        )

        return
    }


// =========================================================
// VIEW WORKOUT DAY DETAILS
// =========================================================

    val selectedWorkoutDay =
        uiState.selectedPlannedWorkoutId
            ?.let { plannedWorkoutId ->

                uiState.workoutDays
                    .firstOrNull {
                        it.plannedWorkoutId ==
                                plannedWorkoutId
                    }
            }
            ?: uiState.selectedWorkoutDay
                ?.let { selectedDay ->

                    uiState.workoutDays
                        .firstOrNull {
                            it.day == selectedDay &&
                                    it.weekNumber ==
                                    uiState.selectedWeek
                        }
                }

    selectedWorkoutDay?.let { workoutDay ->

        BackHandler {
            viewModel.clearSelectedWorkoutDay()
        }

        PlanWorkoutDayDetail(
            workoutDay = workoutDay,

            onBack = {
                viewModel.clearSelectedWorkoutDay()
            },

            onExerciseDetails = { exercise ->
                onExerciseDetails(
                    exercise.exerciseId,
                    workoutDay.day
                )
            },

            onAddExercises = {

                viewModel.selectWorkoutDay(
                    workoutDay
                )

                onAddExercises(
                    workoutDay.day
                )
            },

            onEditExercise = {
                    exercise,
                    sets,
                    reps,
                    restSeconds ->

                viewModel.updateExerciseInSelectedDay(
                    exerciseId =
                        exercise.exerciseId,
                    sets = sets,
                    reps = reps,
                    restSeconds = restSeconds
                )
            },

            onRemoveExercise = { exercise ->

                viewModel.removeExerciseFromSelectedDay(
                    exercise.exerciseId
                )
            },

            onMoveUp = { exercise ->

                viewModel.moveSelectedDayExerciseUp(
                    exercise.exerciseId
                )
            },

            onMoveDown = { exercise ->

                viewModel.moveSelectedDayExerciseDown(
                    exercise.exerciseId
                )
            },

            onStartWorkout = {

                viewModel.startSelectedWorkout { plannedWorkoutId ->

                    onStartWorkout(
                        plannedWorkoutId
                    )
                }
            }
        )

        return
    }



// =========================================================
// SAVED PLAN OVERVIEW
// =========================================================

    if (
        uiState.hasActivePlan &&
        !uiState.isEditingPlan &&
        !uiState.isConfiguringWorkouts
    ) {

        MonthlyPlanOverview(
            planName = uiState.planName,
            goal = uiState.goal,

            selectedWeek =
                uiState.selectedWeek,

            totalWeeks =
                uiState.totalWeeks,

            onPreviousWeek = {
                viewModel.previousWeek()
            },

            onNextWeek = {
                viewModel.nextWeek()
            },

            workoutDays =
                uiState.workoutDays.filter {
                    it.day in uiState.trainingDays
                },

            todayWorkout =
                uiState.todayWorkout,

            onBack = onBack,

            onEditPlan = {
                viewModel.editLoadedPlan()
            },

            onStartWorkout = { plannedWorkoutId ->

                onStartWorkout(
                    plannedWorkoutId
                )
            },

            onViewTodayWorkout = {

                val todayWorkout =
                    uiState.todayWorkout

                if (todayWorkout != null) {

                    val todayDay =
                        uiState.workoutDays
                            .firstOrNull {
                                it.plannedWorkoutId ==
                                        todayWorkout.plannedWorkoutId
                            }

                    todayDay?.let { workoutDay ->

                        viewModel.selectWorkoutDay(
                            workoutDay
                        )
                    }
                }
            },

            onOpenDay = { workoutDay ->

                viewModel.selectWorkoutDay(
                    workoutDay
                )
            }
        )

        return
    }

    if (uiState.isConfiguringWorkouts) {

        WorkoutDaysSetup(
            planName = uiState.planName,

            workoutDays =
                uiState.workoutDays.filter {
                    it.day in uiState.trainingDays
                },


            isSaving =
                uiState.isSaving,

            onBack = {
                viewModel.returnToPlanDetails()
            },

            onAddExercises = { workoutDay ->

                viewModel.selectWorkoutDay(
                    workoutDay
                )

                onAddExercises(
                    workoutDay.day
                )
            },

            onSavePlan = {
                viewModel.savePlan()
            }
        )

        return
    }

    var showStartDatePicker by
    remember {
        mutableStateOf(false)
    }

    var showEndDatePicker by
    remember {
        mutableStateOf(false)
    }

    val dateFormatter =
        remember {
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ForceColors.Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = "CREATE PLAN",
                    style =
                        MaterialTheme.typography.headlineSmall,
                    color = ForceColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "CANCEL",
                    color = ForceColors.TextSecondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            onBack()
                        }
                        .padding(
                            top = 6.dp,
                            start = 12.dp,
                            bottom = 12.dp
                        )
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Build your monthly training plan.",
                color = ForceColors.TextSecondary,
                style =
                    MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "PLAN NAME",
                color = ForceColors.TextSecondary,
                style =
                    MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = uiState.planName,
                onValueChange =
                    viewModel::updatePlanName,
                placeholder = {
                    Text(
                        text = "Example: September Strength"
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "TRAINING GOAL",
                color = ForceColors.TextSecondary,
                style =
                    MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = uiState.goal,
                onValueChange =
                    viewModel::updateGoal,
                placeholder = {
                    Text(
                        text = "Example: Build Muscle"
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "PLAN DATES",
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedButton(
                onClick = {
                    showStartDatePicker = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        uiState.startDate?.let {
                            "START DATE  •  ${
                                dateFormatter.format(
                                    Date(it)
                                )
                            }"
                        } ?: "SELECT START DATE"
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedButton(
                onClick = {
                    showEndDatePicker = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        uiState.endDate?.let {
                            "END DATE  •  ${
                                dateFormatter.format(
                                    Date(it)
                                )
                            }"
                        } ?: "SELECT END DATE"
                )
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "WORKOUT DAYS",
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Choose the days you normally train.",
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            val trainingDays =
                listOf(
                    1 to "MON",
                    2 to "TUE",
                    3 to "WED",
                    4 to "THU",
                    5 to "FRI",
                    6 to "SAT",
                    7 to "SUN"
                )

            FlowRow(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                trainingDays.forEach { (day, label) ->

                    FilterChip(
                        selected =
                            day in uiState.trainingDays,

                        onClick = {
                            viewModel.toggleTrainingDay(
                                day
                            )
                        },

                        label = {
                            Text(label)
                        }
                    )
                }
            }

            uiState.dateRangeWarning?.let { warning ->

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),

                    color =
                        ForceColors.Surface,

                    shape =
                        MaterialTheme.shapes.medium
                ) {

                    Column(
                        modifier =
                            Modifier.padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = "PLAN DATE NEEDS ADJUSTING",
                            color =
                                ForceColors.Primary,
                            style =
                                MaterialTheme.typography.labelMedium,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text = warning,
                            color =
                                ForceColors.TextPrimary,
                            style =
                                MaterialTheme.typography.bodyMedium
                        )

                        TextButton(
                            onClick = {
                                viewModel.clearDateRangeWarning()
                                showEndDatePicker = true
                            }
                        ) {

                            Text(
                                text = "CHANGE END DATE",
                                color =
                                    ForceColors.Primary,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )
            }

            Button(
                onClick = {
                    viewModel.continueToWorkoutSetup()
                },

                enabled =
                    uiState.planName.isNotBlank() &&
                            uiState.goal.isNotBlank() &&
                            uiState.startDate != null &&
                            uiState.endDate != null &&
                            uiState.endDate!! >= uiState.startDate!! &&
                            uiState.trainingDays.isNotEmpty(),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            ForceColors.Primary,
                        contentColor =
                            ForceColors.Background
                    )
            ) {

                Text(
                    text = "CONTINUE",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (showStartDatePicker) {

            val datePickerState =
                rememberDatePickerState(
                    initialSelectedDateMillis =
                        uiState.startDate
                )

            DatePickerDialog(
                onDismissRequest = {
                    showStartDatePicker = false
                },
                confirmButton = {

                    TextButton(
                        onClick = {

                            datePickerState
                                .selectedDateMillis
                                ?.let {
                                    viewModel.updateStartDate(it)
                                }

                            showStartDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {

                    TextButton(
                        onClick = {
                            showStartDatePicker = false
                        }
                    ) {
                        Text("CANCEL")
                    }
                }
            ) {

                DatePicker(
                    state = datePickerState
                )
            }
        }

        if (showEndDatePicker) {

            val datePickerState =
                rememberDatePickerState(
                    initialSelectedDateMillis =
                        uiState.endDate
                )

            DatePickerDialog(
                onDismissRequest = {
                    showEndDatePicker = false
                },
                confirmButton = {

                    TextButton(
                        onClick = {

                            datePickerState
                                .selectedDateMillis
                                ?.let {
                                    viewModel.updateEndDate(it)
                                }

                            showEndDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {

                    TextButton(
                        onClick = {
                            showEndDatePicker = false
                        }
                    ) {
                        Text("CANCEL")
                    }
                }
            ) {

                DatePicker(
                    state = datePickerState
                )
            }
        }
    }


}

@Composable
private fun MonthlyPlanDraftRecovery(
    planName: String,
    selectedDays: Int,
    programmedDays: Int,
    onContinue: () -> Unit,
    onDiscard: () -> Unit,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ForceColors.Background
            )
            .padding(24.dp),
        verticalArrangement =
            Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "UNFINISHED PLAN",
            color = ForceColors.Primary,
            style =
                MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text =
                planName.ifBlank {
                    "Monthly Workout Plan"
                },
            color = ForceColors.TextPrimary,
            style =
                MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text =
                "$selectedDays workout days • " +
                        "$programmedDays programmed",
            color = ForceColors.TextSecondary,
            style =
                MaterialTheme.typography.bodyLarge
        )

        Text(
            text =
                "You have an unfinished workout plan. " +
                        "Continue where you stopped or discard it.",
            color = ForceColors.TextSecondary
        )

        Button(
            onClick = onContinue,
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text = "CONTINUE DRAFT",
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = onDiscard,
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text = "DISCARD DRAFT"
            )
        }

        TextButton(
            onClick = onBack,
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text = "BACK"
            )
        }
    }
}