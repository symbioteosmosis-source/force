package com.ngedo.force.feature.workout.plan

import androidx.lifecycle.ViewModel
import com.ngedo.force.data.repository.WorkoutPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.ngedo.force.data.local.entity.PlannedExerciseEntity
import com.ngedo.force.data.local.entity.PlannedWorkoutEntity
import com.ngedo.force.data.local.entity.WorkoutPlanEntity
import kotlinx.coroutines.launch
import java.util.Calendar


@HiltViewModel
class MonthlyPlanViewModel @Inject constructor(
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val monthlyPlanDraftRepository: MonthlyPlanDraftRepository
) : ViewModel() {

    private suspend fun loadTodayWorkout(
        activePlanId: Long
    ) {

        val now =
            System.currentTimeMillis()

        val calendar =
            java.util.Calendar.getInstance()

        calendar.timeInMillis = now

        calendar.set(
            java.util.Calendar.HOUR_OF_DAY,
            0
        )

        calendar.set(
            java.util.Calendar.MINUTE,
            0
        )

        calendar.set(
            java.util.Calendar.SECOND,
            0
        )

        calendar.set(
            java.util.Calendar.MILLISECOND,
            0
        )

        val dayStart =
            calendar.timeInMillis

        calendar.add(
            java.util.Calendar.DAY_OF_YEAR,
            1
        )

        val dayEnd =
            calendar.timeInMillis

        val plannedWorkout =
            workoutPlanRepository
                .getWorkoutForToday(
                    activePlanId,
                    dayStart,
                    dayEnd
                )
                ?: return

        val exercises =
            workoutPlanRepository
                .getPlannedExercises(
                    plannedWorkout.id
                )

        _uiState.value =
            _uiState.value.copy(
                todayWorkout =
                    TodayPlannedWorkout(
                        plannedWorkoutId =
                            plannedWorkout.id,

                        workoutName =
                            plannedWorkout.name,

                        exerciseCount =
                            exercises.size,

                        isCompleted =
                            plannedWorkout.isCompleted
                    )
            )
    }

    fun refreshTodayWorkout() {

        val activePlanId =
            _uiState.value.activePlanId
                ?: return

        viewModelScope.launch {
            loadTodayWorkout(
                activePlanId
            )
        }
    }

    private fun saveDraft() {

        val state =
            _uiState.value

        val hasDraftContent =
            state.planName.isNotBlank() ||
                    state.goal.isNotBlank() ||
                    state.startDate != null ||
                    state.endDate != null ||
                    state.trainingDays.isNotEmpty() ||
                    state.workoutDays.isNotEmpty()

        if (!hasDraftContent) {
            return
        }

        val draft =
            MonthlyPlanDraft(
                planName = state.planName,
                goal = state.goal,
                startDate = state.startDate,
                endDate = state.endDate,
                trainingDays = state.trainingDays,
                workoutDays = state.workoutDays,
                isConfiguringWorkouts =
                    state.isConfiguringWorkouts
            )

        viewModelScope.launch {

            monthlyPlanDraftRepository
                .saveDraft(draft)
        }
    }


    private val _uiState =
        MutableStateFlow(
            MonthlyPlanUiState()
        )

    val uiState: StateFlow<MonthlyPlanUiState> =
        _uiState.asStateFlow()

    init {
        loadInitialPlanState()
    }

    private fun loadInitialPlanState() {

        viewModelScope.launch {

            val draft =
                monthlyPlanDraftRepository
                    .loadDraft()

            if (draft != null) {

                _uiState.value =
                    _uiState.value.copy(
                        planName = draft.planName,
                        goal = draft.goal,
                        startDate = draft.startDate,
                        endDate = draft.endDate,
                        trainingDays = draft.trainingDays,
                        workoutDays = draft.workoutDays,

                        // Wait for user to choose CONTINUE.
                        isConfiguringWorkouts = false,

                        hasDraft = true,
                        isDraftRecoveryPending = true,
                        isLoadingPlan = false
                    )

                return@launch
            }

            loadActivePlan()
        }
    }

   private fun loadActivePlan() {

        viewModelScope.launch {

            try {

                val plan =
                    workoutPlanRepository
                        .getActiveWorkoutPlan()

                if (plan == null) {

                    _uiState.value =
                        _uiState.value.copy(
                            isLoadingPlan = false,
                            hasActivePlan = false
                        )

                    return@launch
                }

                val plannedWorkouts =
                    workoutPlanRepository
                        .getPlannedWorkouts(
                            plan.id
                        )

                val workoutDays =
                    plannedWorkouts
                        .map { plannedWorkout ->

                            val calendar =
                                Calendar.getInstance().apply {
                                    timeInMillis =
                                        plannedWorkout.scheduledDate
                                }

                            val day =
                                when (
                                    calendar.get(
                                        Calendar.DAY_OF_WEEK
                                    )
                                ) {
                                    Calendar.MONDAY -> 1
                                    Calendar.TUESDAY -> 2
                                    Calendar.WEDNESDAY -> 3
                                    Calendar.THURSDAY -> 4
                                    Calendar.FRIDAY -> 5
                                    Calendar.SATURDAY -> 6
                                    Calendar.SUNDAY -> 7
                                    else -> 0
                                }

                            if (day == 0) {
                                return@map null
                            }

                            val exercises =
                                workoutPlanRepository
                                    .getPlannedExercises(
                                        plannedWorkout.id
                                    )
                                    .sortedBy {
                                        it.exerciseOrder
                                    }
                                    .map { exercise ->

                                        PlanExercise(
                                            exerciseId =
                                                exercise.exerciseId,

                                            exerciseName =
                                                exercise.exerciseName,

                                            target =
                                                exercise.target,

                                            sets =
                                                exercise.sets,

                                            reps =
                                                exercise.reps,

                                            restSeconds =
                                                exercise.restSeconds
                                        )
                                    }

                            PlanWorkoutDay(
                                day = day,
                                name = plannedWorkout.name,
                                exercises = exercises,

                                plannedWorkoutId = plannedWorkout.id,
                                weekNumber = plannedWorkout.weekNumber,
                                scheduledDate = plannedWorkout.scheduledDate,
                                isCompleted = plannedWorkout.isCompleted
                            )
                        }
                        .filterNotNull()
                        .sortedWith(
                            compareBy<PlanWorkoutDay> {
                                it.weekNumber
                            }.thenBy {
                                it.scheduledDate
                                    ?: Long.MAX_VALUE
                            }
                        )

                val totalWeeks =
                    workoutDays
                        .maxOfOrNull {
                            it.weekNumber
                        }
                        ?.coerceAtLeast(1)
                        ?: 1

                val now =
                    System.currentTimeMillis()

                val todayCalendar =
                    Calendar.getInstance().apply {
                        timeInMillis = now

                        set(
                            Calendar.HOUR_OF_DAY,
                            0
                        )

                        set(
                            Calendar.MINUTE,
                            0
                        )

                        set(
                            Calendar.SECOND,
                            0
                        )

                        set(
                            Calendar.MILLISECOND,
                            0
                        )
                    }

                val today =
                    todayCalendar.timeInMillis

                val currentWeek =
                    workoutDays
                        .filter {
                            it.scheduledDate != null
                        }
                        .groupBy {
                            it.weekNumber
                        }
                        .entries
                        .firstOrNull { (_, days) ->

                            val firstDate =
                                days
                                    .mapNotNull {
                                        it.scheduledDate
                                    }
                                    .minOrNull()
                                    ?: return@firstOrNull false

                            val calendar =
                                Calendar.getInstance().apply {
                                    timeInMillis = firstDate
                                }

                            val dayOfWeek =
                                calendar.get(
                                    Calendar.DAY_OF_WEEK
                                )

                            val daysFromMonday =
                                when (dayOfWeek) {

                                    Calendar.SUNDAY -> 6

                                    else ->
                                        dayOfWeek -
                                                Calendar.MONDAY
                                }

                            calendar.add(
                                Calendar.DAY_OF_MONTH,
                                -daysFromMonday
                            )

                            val weekStart =
                                calendar.timeInMillis

                            calendar.add(
                                Calendar.DAY_OF_MONTH,
                                7
                            )

                            val nextWeekStart =
                                calendar.timeInMillis

                            today >= weekStart &&
                                    today < nextWeekStart
                        }
                        ?.key
                        ?: 1

                _uiState.value =
                    _uiState.value.copy(
                        activePlanId = plan.id,
                        hasActivePlan = true,


                        planName = plan.name,
                        goal = plan.goal,
                        startDate = plan.startDate,
                        endDate = plan.endDate,

                        trainingDays =
                            workoutDays
                                .map { it.day }
                                .toSet(),

                        workoutDays =
                            workoutDays,

                        selectedWeek = currentWeek,
                        totalWeeks = totalWeeks,

                        isLoadingPlan = false
                    )

                loadTodayWorkout(
                    plan.id
                )

            } catch (exception: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoadingPlan = false,
                        errorMessage =
                            exception.message
                                ?: "Unable to load plan."
                    )
            }
        }
    }

    fun previousWeek() {

        val currentWeek =
            _uiState.value.selectedWeek

        if (currentWeek <= 1) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedWeek =
                    currentWeek - 1
            )
    }


    fun nextWeek() {

        val state =
            _uiState.value

        if (
            state.selectedWeek >=
            state.totalWeeks
        ) {
            return
        }

        _uiState.value =
            state.copy(
                selectedWeek =
                    state.selectedWeek + 1
            )
    }

    fun updatePlanName(
        name: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                planName = name
            )

        saveDraft()
    }

    fun updateGoal(
        goal: String
    ) {
        _uiState.value =
            _uiState.value.copy(
                goal = goal
            )
        saveDraft()
    }

    fun updateStartDate(
        date: Long
    ) {
        _uiState.value =
            _uiState.value.copy(
                startDate = date
            )
        saveDraft()
    }

    fun updateEndDate(
        date: Long
    ) {
        _uiState.value =
            _uiState.value.copy(
                endDate = date
            )
        saveDraft()
    }

    fun clearError() {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = null
            )
    }

    fun toggleTrainingDay(
        day: Int
    ) {

        val currentState =
            _uiState.value

        val isRemoving =
            day in currentState.trainingDays

        val updatedTrainingDays =
            if (isRemoving) {
                currentState.trainingDays - day
            } else {
                currentState.trainingDays + day
            }

        val dayAlreadyExists =
            currentState.workoutDays.any {
                it.day == day
            }

        val updatedWorkoutDays =
            if (
                !isRemoving &&
                !dayAlreadyExists
            ) {
                (
                        currentState.workoutDays +
                                PlanWorkoutDay(
                                    day = day,
                                    name = dayName(day)
                                )
                        ).sortedBy {
                        it.day
                    }
            } else {
                currentState.workoutDays
            }

        _uiState.value =
            currentState.copy(
                trainingDays = updatedTrainingDays,
                workoutDays = updatedWorkoutDays
            )
        saveDraft()
    }

    private fun dayName(
        day: Int
    ): String {

        return when (day) {
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            7 -> "Sunday"
            else -> "Workout"
        }
    }

    fun continueToWorkoutSetup() {

        val state =
            _uiState.value

        if (state.trainingDays.isEmpty()) {
            return
        }

        val startDate =
            state.startDate ?: return

        val endDate =
            state.endDate ?: return

        val availableDays =
            mutableSetOf<Int>()

        val calendar =
            Calendar.getInstance().apply {
                timeInMillis = startDate

                set(
                    Calendar.HOUR_OF_DAY,
                    0
                )

                set(
                    Calendar.MINUTE,
                    0
                )

                set(
                    Calendar.SECOND,
                    0
                )

                set(
                    Calendar.MILLISECOND,
                    0
                )
            }

        while (
            calendar.timeInMillis <= endDate
        ) {

            val forceDay =
                when (
                    calendar.get(
                        Calendar.DAY_OF_WEEK
                    )
                ) {
                    Calendar.MONDAY -> 1
                    Calendar.TUESDAY -> 2
                    Calendar.WEDNESDAY -> 3
                    Calendar.THURSDAY -> 4
                    Calendar.FRIDAY -> 5
                    Calendar.SATURDAY -> 6
                    Calendar.SUNDAY -> 7
                    else -> 0
                }

            if (forceDay != 0) {
                availableDays.add(forceDay)
            }

            calendar.add(
                Calendar.DAY_OF_MONTH,
                1
            )
        }

        val unavailableDays =
            state.trainingDays
                .filter {
                    it !in availableDays
                }
                .sorted()

        if (unavailableDays.isNotEmpty()) {

            val dayNames =
                unavailableDays.joinToString(
                    separator = ", "
                ) { day ->
                    dayName(day)
                }

            _uiState.value =
                state.copy(
                    dateRangeWarning =
                        if (unavailableDays.size == 1) {
                            "$dayNames does not occur within your current plan dates. " +
                                    "Extend your plan end date to include this training day."
                        } else {
                            "$dayNames do not occur within your current plan dates. " +
                                    "Extend your plan end date to include these training days."
                        }
                )

            return
        }

        _uiState.value =
            state.copy(
                isConfiguringWorkouts = true,
                dateRangeWarning = null
            )

        saveDraft()
    }

    fun clearDateRangeWarning() {

        _uiState.value =
            _uiState.value.copy(
                dateRangeWarning = null
            )
    }

    fun returnToPlanDetails() {

        _uiState.value =
            _uiState.value.copy(
                isConfiguringWorkouts = false
            )
        saveDraft()
    }

    fun selectWorkoutDay(
        workoutDay: PlanWorkoutDay
    ) {
        _uiState.value =
            _uiState.value.copy(
                selectedWorkoutDay =
                    workoutDay.day,

                selectedPlannedWorkoutId =
                    workoutDay.plannedWorkoutId
            )
    }

    fun clearSelectedWorkoutDay() {

        _uiState.value =
            _uiState.value.copy(
                selectedWorkoutDay = null,
                selectedPlannedWorkoutId = null
            )
    }

    private fun persistPlanChange() {

        val state =
            _uiState.value

        if (
            state.hasActivePlan &&
            state.activePlanId != null &&
            !state.isEditingPlan
        ) {
            updateExistingScheduledDay()
        } else {
            saveDraft()
        }
    }

    private fun updateExistingScheduledDay() {

        val state =
            _uiState.value

        val plannedWorkoutId =
            state.selectedPlannedWorkoutId
                ?: return

        val workoutDay =
            state.workoutDays
                .firstOrNull {
                    it.plannedWorkoutId ==
                            plannedWorkoutId
                }
                ?: return

        viewModelScope.launch {

            try {

                val plannedWorkout =
                    workoutPlanRepository
                        .getPlannedWorkout(
                            plannedWorkoutId
                        )
                        ?: return@launch

                val exercises =
                    workoutDay.exercises
                        .mapIndexed { index, exercise ->

                            PlannedExerciseEntity(
                                plannedWorkoutId =
                                    plannedWorkoutId,

                                exerciseId =
                                    exercise.exerciseId,

                                exerciseName =
                                    exercise.exerciseName,

                                target =
                                    exercise.target,

                                exerciseOrder =
                                    index,

                                sets =
                                    exercise.sets,

                                reps =
                                    exercise.reps,

                                restSeconds =
                                    exercise.restSeconds
                            )
                        }

                workoutPlanRepository
                    .replacePlannedExercises(
                        plannedWorkoutId =
                            plannedWorkoutId,

                        exercises =
                            exercises
                    )

                if (plannedWorkout.isCompleted) {

                    workoutPlanRepository
                        .markWorkoutIncomplete(
                            plannedWorkoutId
                        )
                }

                state.activePlanId?.let { planId ->

                    loadTodayWorkout(
                        planId
                    )
                }

                _uiState.value =
                    _uiState.value.copy(
                        showSavedConfirmation = true
                    )

            } catch (exception: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        errorMessage =
                            exception.message
                                ?: "Unable to update workout."
                    )
            }
        }
    }

    private fun isSelectedWorkoutDay(
        workoutDay: PlanWorkoutDay
    ): Boolean {

        val state =
            _uiState.value

        val selectedPlannedWorkoutId =
            state.selectedPlannedWorkoutId

        return if (selectedPlannedWorkoutId != null) {

            workoutDay.plannedWorkoutId ==
                    selectedPlannedWorkoutId

        } else {

            workoutDay.day ==
                    state.selectedWorkoutDay &&
                    workoutDay.weekNumber ==
                    state.selectedWeek
        }
    }
    fun addExerciseToSelectedDay(
        exerciseId: String,
        exerciseName: String,
        target: String
    ) {

        val selectedDay =
            _uiState.value.selectedWorkoutDay
                ?: return

        val updatedWorkoutDays =
            _uiState.value.workoutDays.map { workoutDay ->

                if (!isSelectedWorkoutDay(workoutDay)) {
                    workoutDay
                } else {

                    val alreadyAdded =
                        workoutDay.exercises.any {
                            it.exerciseId == exerciseId
                        }

                    if (alreadyAdded) {
                        workoutDay
                    } else {
                        workoutDay.copy(
                            exercises =
                                workoutDay.exercises +
                                        PlanExercise(
                                            exerciseId = exerciseId,
                                            exerciseName = exerciseName,
                                            target = target
                                        )
                        )
                    }
                }
            }

        _uiState.value =
            _uiState.value.copy(
                workoutDays = updatedWorkoutDays
            )

        persistPlanChange()
    }

    fun removeExerciseFromSelectedDay(
        exerciseId: String
    ) {

        val selectedDay =
            _uiState.value.selectedWorkoutDay
                ?: return

        val updatedWorkoutDays =
            _uiState.value.workoutDays.map { workoutDay ->

                if (!isSelectedWorkoutDay(workoutDay)) {
                    workoutDay
                } else {
                    workoutDay.copy(
                        exercises =
                            workoutDay.exercises.filterNot {
                                it.exerciseId == exerciseId
                            }
                    )
                }
            }

        _uiState.value =
            _uiState.value.copy(
                workoutDays = updatedWorkoutDays
            )
        persistPlanChange()
    }

    fun updateExerciseInSelectedDay(
        exerciseId: String,
        sets: Int,
        reps: String,
        restSeconds: Int
    ) {

        val selectedDay =
            _uiState.value.selectedWorkoutDay
                ?: return

        val updatedWorkoutDays =
            _uiState.value.workoutDays.map { workoutDay ->

                if (!isSelectedWorkoutDay(workoutDay)) {
                    workoutDay
                } else {
                    workoutDay.copy(
                        exercises =
                            workoutDay.exercises.map { exercise ->

                                if (exercise.exerciseId == exerciseId) {
                                    exercise.copy(
                                        sets = sets,
                                        reps = reps,
                                        restSeconds = restSeconds
                                    )
                                } else {
                                    exercise
                                }
                            }
                    )
                }
            }

        _uiState.value =
            _uiState.value.copy(
                workoutDays = updatedWorkoutDays
            )

        persistPlanChange()
    }

    fun moveSelectedDayExerciseUp(
        exerciseId: String
    ) {
        moveSelectedDayExercise(
            exerciseId = exerciseId,
            direction = -1
        )
    }


    fun moveSelectedDayExerciseDown(
        exerciseId: String
    ) {
        moveSelectedDayExercise(
            exerciseId = exerciseId,
            direction = 1
        )
    }


    private fun moveSelectedDayExercise(
        exerciseId: String,
        direction: Int
    ) {

        val selectedDay =
            _uiState.value.selectedWorkoutDay
                ?: return

        val updatedWorkoutDays =
            _uiState.value.workoutDays.map { workoutDay ->

                if (!isSelectedWorkoutDay(workoutDay)) {
                    workoutDay
                } else {
                    val exercises =
                        workoutDay.exercises.toMutableList()

                    val currentIndex =
                        exercises.indexOfFirst {
                            it.exerciseId == exerciseId
                        }

                    if (currentIndex == -1) {
                        return@map workoutDay
                    }

                    val newIndex =
                        currentIndex + direction

                    if (newIndex !in exercises.indices) {
                        return@map workoutDay
                    }

                    val exercise =
                        exercises.removeAt(currentIndex)

                    exercises.add(
                        newIndex,
                        exercise
                    )

                    workoutDay.copy(
                        exercises = exercises
                    )
                }
            }

        _uiState.value =
            _uiState.value.copy(
                workoutDays = updatedWorkoutDays
            )

        persistPlanChange()
    }

    fun savePlan(
        stayOnCurrentScreen: Boolean = false
    ) {

        val state = _uiState.value

        val selectedWorkoutDays =
            state.workoutDays.filter {
                it.day in state.trainingDays
            }

        val startDate =
            state.startDate ?: return

        val endDate =
            state.endDate ?: return

        if (
            state.planName.isBlank() ||
            state.goal.isBlank() ||
            selectedWorkoutDays.isEmpty()
        ) {
            return
        }


        viewModelScope.launch {

            _uiState.value =
                state.copy(
                    isSaving = true,
                    errorMessage = null
                )

            try {

                val existingPlanId =
                    state.activePlanId

                val planId =
                    if (existingPlanId != null) {

                        workoutPlanRepository
                            .updateWorkoutPlan(
                                WorkoutPlanEntity(
                                    id = existingPlanId,
                                    name = state.planName.trim(),
                                    goal = state.goal.trim(),
                                    startDate = startDate,
                                    endDate = endDate,
                                    isActive = true,
                                    createdAt =
                                        System.currentTimeMillis()
                                )
                            )

                        workoutPlanRepository
                            .deletePlannedWorkoutsForPlan(
                                existingPlanId
                            )

                        existingPlanId

                    } else {

                        workoutPlanRepository
                            .createWorkoutPlan(
                                WorkoutPlanEntity(
                                    name = state.planName.trim(),
                                    goal = state.goal.trim(),
                                    startDate = startDate,
                                    endDate = endDate,
                                    createdAt =
                                        System.currentTimeMillis()
                                )
                            )
                    }

                createScheduledWorkouts(
                    planId = planId,
                    startDate = startDate,
                    endDate = endDate,
                    workoutDays = selectedWorkoutDays
                )

                /*
                 * Refresh today's workout after rebuilding
                 * the active plan.
                 *
                 * This keeps Home and Monthly Plan using
                 * the same completion state.
                 */
                loadTodayWorkout(
                    planId
                )

                monthlyPlanDraftRepository
                    .clearDraft()

                monthlyPlanDraftRepository
                    .clearDraft()

                _uiState.value =
                    _uiState.value.copy(
                        isSaving = false,
                        isSaved = !stayOnCurrentScreen,
                        showSavedConfirmation = stayOnCurrentScreen,

                        isEditingPlan =
                            if (stayOnCurrentScreen) {
                                _uiState.value.isEditingPlan
                            } else {
                                false
                            },

                        isConfiguringWorkouts =
                            if (stayOnCurrentScreen) {
                                _uiState.value.isConfiguringWorkouts
                            } else {
                                false
                            },

                        hasDraft = false,
                        isDraftRecoveryPending = false
                    )

            } catch (exception: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isSaving = false,
                        errorMessage =
                            exception.message
                                ?: "Unable to save plan."
                    )
            }
        }
    }

    fun openLoadedPlan() {

        if (!_uiState.value.hasActivePlan) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                isConfiguringWorkouts = false
            )
    }


    fun editLoadedPlan() {

        if (!_uiState.value.hasActivePlan) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                isEditingPlan = true,
                isConfiguringWorkouts = true
            )
    }

    private suspend fun createScheduledWorkouts(
        planId: Long,
        startDate: Long,
        endDate: Long,
        workoutDays: List<PlanWorkoutDay>
    ) {

        val calendar =
            Calendar.getInstance()

        calendar.timeInMillis =
            startDate

        calendar.set(
            Calendar.HOUR_OF_DAY,
            0
        )

        calendar.set(
            Calendar.MINUTE,
            0
        )

        calendar.set(
            Calendar.SECOND,
            0
        )

        calendar.set(
            Calendar.MILLISECOND,
            0
        )

        var weekNumber = 1

        while (
            calendar.timeInMillis <= endDate
        ) {

            val currentDay =
                when (
                    calendar.get(
                        Calendar.DAY_OF_WEEK
                    )
                ) {

                    Calendar.MONDAY -> 1
                    Calendar.TUESDAY -> 2
                    Calendar.WEDNESDAY -> 3
                    Calendar.THURSDAY -> 4
                    Calendar.FRIDAY -> 5
                    Calendar.SATURDAY -> 6
                    Calendar.SUNDAY -> 7

                    else -> 0
                }

            val workoutTemplate =
                workoutDays.firstOrNull {
                    it.day == currentDay
                }

            if (workoutTemplate != null) {

                val plannedWorkoutId =
                    workoutPlanRepository.addPlannedWorkout(
                        PlannedWorkoutEntity(
                            planId = planId,
                            name = workoutTemplate.name,
                            weekNumber = weekNumber,
                            scheduledDate =
                                calendar.timeInMillis
                        )
                    )

                val plannedExercises =
                    workoutTemplate.exercises.mapIndexed {
                            index,
                            exercise ->

                        PlannedExerciseEntity(
                            plannedWorkoutId =
                                plannedWorkoutId,

                            exerciseId =
                                exercise.exerciseId,

                            exerciseName =
                                exercise.exerciseName,

                            target =
                                exercise.target,

                            exerciseOrder =
                                index,

                            sets =
                                exercise.sets,

                            reps =
                                exercise.reps,

                            restSeconds =
                                exercise.restSeconds
                        )
                    }

                if (plannedExercises.isNotEmpty()) {

                    workoutPlanRepository
                        .addPlannedExercises(
                            plannedExercises
                        )
                }
            }

            calendar.add(
                Calendar.DAY_OF_MONTH,
                1
            )

            if (
                calendar.get(
                    Calendar.DAY_OF_WEEK
                ) == Calendar.MONDAY
            ) {
                weekNumber++
            }
        }
    }

    fun continueDraft() {

        val state =
            _uiState.value

        if (!state.hasDraft) {
            return
        }

        _uiState.value =
            state.copy(
                isDraftRecoveryPending = false,
                isConfiguringWorkouts = true
            )

        saveDraft()
    }

    fun discardDraft() {

        viewModelScope.launch {

            monthlyPlanDraftRepository
                .clearDraft()

            _uiState.value =
                MonthlyPlanUiState(
                    isLoadingPlan = true
                )

            loadActivePlan()
        }
    }

    fun clearSavedConfirmation() {

        _uiState.value =
            _uiState.value.copy(
                showSavedConfirmation = false
            )
    }

    fun startSelectedWorkout(
        onWorkoutReady: (Long) -> Unit
    ) {

        val plannedWorkoutId =
            _uiState.value
                .selectedPlannedWorkoutId
                ?: return

        onWorkoutReady(
            plannedWorkoutId
        )
    }
}