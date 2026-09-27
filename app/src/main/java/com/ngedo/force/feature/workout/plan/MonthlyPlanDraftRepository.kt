package com.ngedo.force.feature.workout.plan

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private val Context.monthlyPlanDraftDataStore by preferencesDataStore(
    name = "monthly_plan_draft"
)

@Singleton
class MonthlyPlanDraftRepository @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    companion object {
        private val DRAFT_KEY =
            stringPreferencesKey(
                "monthly_plan_draft_json"
            )
    }

    suspend fun saveDraft(
        draft: MonthlyPlanDraft
    ) {

        val json =
            JSONObject().apply {

                put(
                    "planName",
                    draft.planName
                )

                put(
                    "goal",
                    draft.goal
                )

                put(
                    "startDate",
                    draft.startDate
                        ?: JSONObject.NULL
                )

                put(
                    "endDate",
                    draft.endDate
                        ?: JSONObject.NULL
                )

                put(
                    "isConfiguringWorkouts",
                    draft.isConfiguringWorkouts
                )

                put(
                    "trainingDays",
                    JSONArray().apply {
                        draft.trainingDays
                            .sorted()
                            .forEach { day ->
                                put(day)
                            }
                    }
                )

                put(
                    "workoutDays",
                    JSONArray().apply {

                        draft.workoutDays
                            .forEach { workoutDay ->

                                put(
                                    workoutDayToJson(
                                        workoutDay
                                    )
                                )
                            }
                    }
                )
            }

        context.monthlyPlanDraftDataStore.edit {
                preferences ->

            preferences[DRAFT_KEY] =
                json.toString()
        }
    }

    suspend fun loadDraft():
            MonthlyPlanDraft? {

        val preferences =
            context.monthlyPlanDraftDataStore
                .data
                .first()

        val jsonString =
            preferences[DRAFT_KEY]
                ?: return null

        return try {

            val json =
                JSONObject(
                    jsonString
                )

            MonthlyPlanDraft(
                planName =
                    json.optString(
                        "planName"
                    ),

                goal =
                    json.optString(
                        "goal"
                    ),

                startDate =
                    nullableLong(
                        json,
                        "startDate"
                    ),

                endDate =
                    nullableLong(
                        json,
                        "endDate"
                    ),

                trainingDays =
                    readTrainingDays(
                        json.optJSONArray(
                            "trainingDays"
                        )
                    ),

                workoutDays =
                    readWorkoutDays(
                        json.optJSONArray(
                            "workoutDays"
                        )
                    ),

                isConfiguringWorkouts =
                    json.optBoolean(
                        "isConfiguringWorkouts",
                        false
                    )
            )

        } catch (
            exception: Exception
        ) {
            null
        }
    }

    suspend fun clearDraft() {

        context.monthlyPlanDraftDataStore.edit {
                preferences ->

            preferences.remove(
                DRAFT_KEY
            )
        }
    }

    private fun workoutDayToJson(
        workoutDay: PlanWorkoutDay
    ): JSONObject {

        return JSONObject().apply {

            put(
                "day",
                workoutDay.day
            )

            put(
                "name",
                workoutDay.name
            )

            put(
                "exercises",
                JSONArray().apply {

                    workoutDay.exercises
                        .forEach { exercise ->

                            put(
                                exerciseToJson(
                                    exercise
                                )
                            )
                        }
                }
            )
        }
    }

    private fun exerciseToJson(
        exercise: PlanExercise
    ): JSONObject {

        return JSONObject().apply {

            put(
                "exerciseId",
                exercise.exerciseId
            )

            put(
                "exerciseName",
                exercise.exerciseName
            )

            put(
                "target",
                exercise.target
            )

            put(
                "sets",
                exercise.sets
            )

            put(
                "reps",
                exercise.reps
            )

            put(
                "restSeconds",
                exercise.restSeconds
            )
        }
    }

    private fun readWorkoutDays(
        array: JSONArray?
    ): List<PlanWorkoutDay> {

        if (array == null) {
            return emptyList()
        }

        val workoutDays =
            mutableListOf<PlanWorkoutDay>()

        for (
        index in 0 until array.length()
        ) {

            val json =
                array.optJSONObject(index)
                    ?: continue

            workoutDays +=
                PlanWorkoutDay(
                    day =
                        json.optInt(
                            "day"
                        ),

                    name =
                        json.optString(
                            "name"
                        ),

                    exercises =
                        readExercises(
                            json.optJSONArray(
                                "exercises"
                            )
                        )
                )
        }

        return workoutDays
            .sortedBy {
                it.day
            }
    }

    private fun readExercises(
        array: JSONArray?
    ): List<PlanExercise> {

        if (array == null) {
            return emptyList()
        }

        val exercises =
            mutableListOf<PlanExercise>()

        for (
        index in 0 until array.length()
        ) {

            val json =
                array.optJSONObject(index)
                    ?: continue

            exercises +=
                PlanExercise(
                    exerciseId =
                        json.optString(
                            "exerciseId"
                        ),

                    exerciseName =
                        json.optString(
                            "exerciseName"
                        ),

                    target =
                        json.optString(
                            "target"
                        ),

                    sets =
                        json.optInt(
                            "sets",
                            3
                        ),

                    reps =
                        json.optString(
                            "reps",
                            "8-12"
                        ),

                    restSeconds =
                        json.optInt(
                            "restSeconds",
                            90
                        )
                )
        }

        return exercises
    }

    private fun readTrainingDays(
        array: JSONArray?
    ): Set<Int> {

        if (array == null) {
            return emptySet()
        }

        val days =
            mutableSetOf<Int>()

        for (
        index in 0 until array.length()
        ) {
            days +=
                array.optInt(index)
        }

        return days
    }

    private fun nullableLong(
        json: JSONObject,
        key: String
    ): Long? {

        if (
            !json.has(key) ||
            json.isNull(key)
        ) {
            return null
        }

        return json.optLong(key)
    }
}