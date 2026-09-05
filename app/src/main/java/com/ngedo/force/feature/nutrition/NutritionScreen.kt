package com.ngedo.force.feature.nutrition

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ngedo.force.designsystem.ForceColors
import java.util.Locale
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import com.ngedo.force.data.local.entity.NutritionEntryEntity
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material3.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.foundation.lazy.items

@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = hiltViewModel()
) {

    val uiState by
    viewModel.uiState.collectAsState()

    if (uiState.isAddFoodVisible) {

        AddFoodDialog(
            uiState = uiState,

            onDismiss = {
                viewModel.hideAddFood()
            },

            onMealTypeChange = {
                viewModel.updateMealType(it)
            },

            onFoodNameChange = {
                viewModel.updateFoodName(it)
            },

            onSavedFoodSelected = { food ->
                viewModel.selectSavedFood(food)
            },

            onCaloriesChange = {
                viewModel.updateCalories(it)
            },

            onProteinChange = {
                viewModel.updateProtein(it)
            },

            onCarbsChange = {
                viewModel.updateCarbs(it)
            },

            onFatChange = {
                viewModel.updateFat(it)
            },

            onSave = {
                viewModel.saveFood()
            }
        )
    }

    /*
     * DELETE CONFIRMATION DIALOG
     */
    uiState.entryPendingDelete?.let { entry ->

        AlertDialog(
            onDismissRequest = {
                viewModel.cancelDeleteFood()
            },

            title = {
                Text(
                    text = "Delete Food?",
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    text =
                        "Are you sure you want to delete " +
                                "\"${entry.foodName}\"?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        viewModel.confirmDeleteFood()
                    }
                ) {

                    Text(
                        text = "Delete",
                        color = ForceColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        viewModel.cancelDeleteFood()
                    }
                ) {

                    Text(
                        text = "Cancel",
                        color = ForceColors.TextSecondary
                    )
                }
            }
        )
    }
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

        contentPadding =
            PaddingValues(
                bottom = 110.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(0.dp)
    ) {

        /*
         * -------------------------------------------------
         * HEADER
         * -------------------------------------------------
         */

        item {

            Text(
                text = "Nutrition",
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Track today's meals and macros.",
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = {
                        viewModel.previousDay()
                    }
                ) {
                    Text(
                        text = "← Previous",
                        color = ForceColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = formatNutritionDate(
                        uiState.selectedDate
                    ),
                    color = ForceColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = {
                        viewModel.nextDay()
                    }
                ) {
                    Text(
                        text = "Next →",
                        color = ForceColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }


        /*
         * -------------------------------------------------
         * DAILY TOTALS
         * -------------------------------------------------
         */

        item {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 8.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text =
                        if (isToday(uiState.selectedDate)) {
                            "Today's Nutrition"
                        } else {
                            "Daily Nutrition"
                        },
                    color = ForceColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                MacroProgressRow(
                    label = "Calories",
                    current = uiState.calories,
                    target = uiState.calorieTarget,
                    unit = "kcal"
                )
                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Button(
                    onClick = {
                        viewModel.showGoalEditor()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                ForceColors.Background
                        ),
                    shape =
                        RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "Edit Goals",
                        color = ForceColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                MacroProgressRow(
                    label = "Protein",
                    current = uiState.protein,
                    target = uiState.proteinTarget,
                    unit = "g"
                )

                MacroProgressRow(
                    label = "Carbs",
                    current = uiState.carbs,
                    target = uiState.carbsTarget,
                    unit = "g"
                )

                MacroProgressRow(
                    label = "Fat",
                    current = uiState.fat,
                    target = uiState.fatTarget,
                    unit = "g"
                )
                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )
            }
        }


        /*
 * -------------------------------------------------
 * BREAKFAST
 * -------------------------------------------------
 */

        nutritionMealItems(
            title = "Breakfast",
            uiState = uiState,
            onEditEntry = {
                viewModel.startEditingFood(it)
            },
            onDeleteEntry = {
                viewModel.requestDeleteFood(it)
            }
        )


        /*
         * -------------------------------------------------
         * LUNCH
         * -------------------------------------------------
         */

        nutritionMealItems(
            title = "Lunch",
            uiState = uiState,
            onEditEntry = {
                viewModel.startEditingFood(it)
            },
            onDeleteEntry = {
                viewModel.requestDeleteFood(it)
            }
        )


        /*
         * -------------------------------------------------
         * DINNER
         * -------------------------------------------------
         */

        nutritionMealItems(
            title = "Dinner",
            uiState = uiState,
            onEditEntry = {
                viewModel.startEditingFood(it)
            },
            onDeleteEntry = {
                viewModel.requestDeleteFood(it)
            }
        )


        /*
         * -------------------------------------------------
         * SNACKS
         * -------------------------------------------------
         */

        nutritionMealItems(
            title = "Snack",
            uiState = uiState,
            onEditEntry = {
                viewModel.startEditingFood(it)
            },
            onDeleteEntry = {
                viewModel.requestDeleteFood(it)
            }
        )


        /*
         * -------------------------------------------------
         * ADD FOOD BUTTON
         * -------------------------------------------------
         */

        item {

            Button(
                onClick = {
                    viewModel.showAddFood()
                },

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
                    text = "Add Food",
                    color = ForceColors.Background,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (uiState.isGoalEditorVisible) {

        NutritionGoalDialog(
            uiState = uiState,

            onDismiss = {
                viewModel.hideGoalEditor()
            },

            onCaloriesChange = {
                viewModel.updateCalorieTargetInput(it)
            },

            onProteinChange = {
                viewModel.updateProteinTargetInput(it)
            },

            onCarbsChange = {
                viewModel.updateCarbsTargetInput(it)
            },

            onFatChange = {
                viewModel.updateFatTargetInput(it)
            },

            onSave = {
                viewModel.saveNutritionGoals()
            }
        )
    }
}


@Composable
private fun NutritionGoalDialog(
    uiState: NutritionUiState,
    onDismiss: () -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onSave: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Daily Nutrition Goals",
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                GoalInputField(
                    label = "Calories (kcal)",
                    value = uiState.calorieTargetInput,
                    onValueChange = onCaloriesChange
                )

                GoalInputField(
                    label = "Protein (g)",
                    value = uiState.proteinTargetInput,
                    onValueChange = onProteinChange
                )

                GoalInputField(
                    label = "Carbs (g)",
                    value = uiState.carbsTargetInput,
                    onValueChange = onCarbsChange
                )

                GoalInputField(
                    label = "Fat (g)",
                    value = uiState.fatTargetInput,
                    onValueChange = onFatChange
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = onSave
            ) {

                Text(
                    text = "Save Goals",
                    color = ForceColors.Primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "Cancel",
                    color = ForceColors.TextSecondary
                )
            }
        }
    )
}

@Composable
private fun GoalInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,

        onValueChange = { newValue ->

            if (
                newValue.isEmpty() ||
                newValue.matches(
                    Regex("^\\d*\\.?\\d*$")
                )
            ) {
                onValueChange(newValue)
            }
        },

        modifier =
            Modifier.fillMaxWidth(),

        label = {
            Text(
                text = label
            )
        },

        keyboardOptions =
            KeyboardOptions(
                keyboardType =
                    KeyboardType.Decimal
            ),

        singleLine = true
    )
}

@Composable
private fun AddFoodDialog(
    uiState: NutritionUiState,
    onDismiss: () -> Unit,
    onMealTypeChange: (String) -> Unit,
    onFoodNameChange: (String) -> Unit,
    onSavedFoodSelected: (com.ngedo.force.data.local.entity.SavedFoodEntity) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onSave: () -> Unit
) {

    val keyboardController =
        LocalSoftwareKeyboardController.current

    val focusManager =
        LocalFocusManager.current

    AlertDialog(
        onDismissRequest = {
            // Do nothing.
            // User must use Cancel or Save.
        },

        title = {
            Text(
                text =
                    if (uiState.entryBeingEdited != null) {
                        "Edit Food"
                    } else {
                        "Add Food"
                    },
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                focusManager.clearFocus()
                            }
                        )
                    }
                    .padding(
                        bottom = 24.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                /*
                 * MEAL TYPE
                 */

                Text(
                    text = "Meal",
                    style =
                        MaterialTheme.typography.labelLarge,
                    fontWeight =
                        FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    listOf(
                        "Breakfast",
                        "Lunch",
                        "Dinner",
                        "Snack"
                    ).forEach { meal ->

                        val isSelected =
                            uiState.selectedMealType == meal

                        TextButton(
                            onClick = {
                                onMealTypeChange(meal)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color =
                                        if (isSelected) {
                                            ForceColors.Primary.copy(
                                                alpha = 0.15f
                                            )
                                        } else {
                                            Color.Transparent
                                        },
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentPadding = PaddingValues(
                                horizontal = 2.dp,
                                vertical = 8.dp
                            )
                        ) {

                            Text(
                                text = meal,
                                maxLines = 1,
                                softWrap = false,
                                color =
                                    if (isSelected) {
                                        ForceColors.Primary
                                    } else {
                                        ForceColors.TextSecondary
                                    },
                                fontWeight =
                                    if (isSelected) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    },
                                style =
                                    MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }


                /*
  * -------------------------------------------------
  * FOOD NAME + REMEMBERED FOOD SUGGESTIONS
  * -------------------------------------------------
  */

                OutlinedTextField(
                    value =
                        uiState.foodName,

                    onValueChange =
                        onFoodNameChange,

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            text = "Food name"
                        )
                    },

                    singleLine = true
                )


                /*
                 * -------------------------------------------------
                 * SAVED FOOD SUGGESTIONS
                 * -------------------------------------------------
                 */

                if (
                    uiState.savedFoodSuggestions.isNotEmpty()
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = ForceColors.Surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(
                                vertical = 4.dp
                            )
                    ) {

                        Text(
                            text =
                                if (uiState.foodName.isBlank()) {
                                    "Recently used"
                                } else {
                                    "Suggestions"
                                },
                            color = ForceColors.TextSecondary,
                            style =
                                MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            )
                        )

                        uiState.savedFoodSuggestions
                            .forEach { food ->

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {

                                            keyboardController?.hide()

                                            onSavedFoodSelected(
                                                food
                                            )
                                        }
                                        .padding(
                                            horizontal = 12.dp,
                                            vertical = 10.dp
                                        )
                                ) {

                                    Text(
                                        text = food.foodName,
                                        color =
                                            ForceColors.TextPrimary,
                                        style =
                                            MaterialTheme.typography.bodyMedium,
                                        fontWeight =
                                            FontWeight.Bold
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(3.dp)
                                    )

                                    Text(
                                        text =
                                            "${formatNumber(food.calories)} kcal" +
                                                    " • P ${formatNumber(food.proteinGrams)}g" +
                                                    " • C ${formatNumber(food.carbsGrams)}g" +
                                                    " • F ${formatNumber(food.fatGrams)}g",
                                        color =
                                            ForceColors.TextSecondary,
                                        style =
                                            MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                    }
                }


                /*
                 * CALORIES
                 */

                OutlinedTextField(
                    value =
                        uiState.caloriesInput,

                    onValueChange = { value ->

                        if (
                            value.isEmpty() ||
                            value.matches(
                                Regex(
                                    "^\\d*\\.?\\d*$"
                                )
                            )
                        ) {
                            onCaloriesChange(
                                value
                            )
                        }
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            text = "Calories"
                        )
                    },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),

                    singleLine = true
                )


                /*
                 * PROTEIN
                 */

                OutlinedTextField(
                    value =
                        uiState.proteinInput,

                    onValueChange = { value ->

                        if (
                            value.isEmpty() ||
                            value.matches(
                                Regex(
                                    "^\\d*\\.?\\d*$"
                                )
                            )
                        ) {
                            onProteinChange(
                                value
                            )
                        }
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            text = "Protein (g)"
                        )
                    },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),

                    singleLine = true
                )


                /*
                 * CARBS
                 */

                OutlinedTextField(
                    value =
                        uiState.carbsInput,

                    onValueChange = { value ->

                        if (
                            value.isEmpty() ||
                            value.matches(
                                Regex(
                                    "^\\d*\\.?\\d*$"
                                )
                            )
                        ) {
                            onCarbsChange(
                                value
                            )
                        }
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            text = "Carbs (g)"
                        )
                    },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),

                    singleLine = true
                )


                /*
                 * FAT
                 */

                OutlinedTextField(
                    value =
                        uiState.fatInput,

                    onValueChange = { value ->

                        if (
                            value.isEmpty() ||
                            value.matches(
                                Regex(
                                    "^\\d*\\.?\\d*$"
                                )
                            )
                        ) {
                            onFatChange(
                                value
                            )
                        }
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            text = "Fat (g)"
                        )
                    },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),

                    singleLine = true
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    onSave()
                }
            ) {

                Text(
                    text =
                        if (uiState.entryBeingEdited != null) {
                            "Save Changes"
                        } else {
                            "Save"
                        },
                    color =
                        ForceColors.Primary,
                    fontWeight =
                        FontWeight.Bold
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    onDismiss()
                }
            ) {

                Text(
                    text = "Cancel",
                    color =
                        ForceColors.TextSecondary
                )
            }
        }
    )
}

@Composable
private fun MacroProgressRow(
    label: String,
    current: Double,
    target: Double,
    unit: String
) {

    val progress =
        if (target > 0.0) {
            (current / target)
                .toFloat()
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    val difference =
        target - current

    val statusText =
        when {

            difference > 0.0 -> {
                "${formatNumber(difference)} $unit remaining"
            }

            difference < 0.0 -> {
                "${formatNumber(-difference)} $unit over"
            }

            else -> {
                "Target reached"
            }
        }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement =
            Arrangement.spacedBy(6.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = label,
                color = ForceColors.TextSecondary,
                style =
                    MaterialTheme.typography.bodyMedium
            )

            Text(
                text =
                    "${formatNumber(current)} / " +
                            "${formatNumber(target)} $unit",
                color = ForceColors.TextPrimary,
                style =
                    MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        LinearProgressIndicator(
            progress = {
                progress
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = ForceColors.Primary,
            trackColor =
                ForceColors.Primary.copy(
                    alpha = 0.18f
                )
        )

        Text(
            text = statusText,
            color =
                if (difference >= 0.0) {
                    ForceColors.TextSecondary
                } else {
                    ForceColors.Primary
                },
            style =
                MaterialTheme.typography.labelMedium
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.nutritionMealItems(
    title: String,
    uiState: NutritionUiState,
    onEditEntry: (NutritionEntryEntity) -> Unit,
    onDeleteEntry: (NutritionEntryEntity) -> Unit
) {

    val mealEntries =
        uiState.entries.filter {
            it.mealType.equals(
                title,
                ignoreCase = true
            )
        }

    /*
     * Meal header
     */
    item(
        key = "meal_header_$title"
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ForceColors.Surface,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    )
                )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text =
                            when (title) {
                                "Breakfast" -> "☀️"
                                "Lunch" -> "🍴"
                                "Dinner" -> "🍽️"
                                "Snack" -> "🍎"
                                else -> "🍽️"
                            },
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = title,
                        color =
                            ForceColors.TextPrimary,
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Text(
                    text =
                        if (mealEntries.size == 1) {
                            "1 item"
                        } else {
                            "${mealEntries.size} items"
                        },
                    color =
                        ForceColors.TextSecondary,
                    style =
                        MaterialTheme.typography.labelMedium
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        ForceColors.TextSecondary.copy(
                            alpha = 0.12f
                        )
                    )
            )
        }
    }


    /*
     * Individual food rows
     *
     * Every food is now its own LazyColumn item.
     */
    items(
        items = mealEntries,
        key = { entry ->
            "nutrition_${title}_${entry.id}"
        }
    ) { entry ->

        NutritionFoodRow(
            entry = entry,
            onEditEntry = onEditEntry,
            onDeleteEntry = onDeleteEntry
        )
    }


    /*
     * Bottom of meal card
     */
    if (mealEntries.isNotEmpty()) {

        item(
            key = "meal_footer_$title"
        ) {

            Column {

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(
                            color = ForceColors.Surface,
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )
            }
        }

    } else {

        item(
            key = "meal_empty_footer_$title"
        ) {

            Column {

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .background(
                            color = ForceColors.Surface,
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )
            }
        }
    }
}

@Composable
private fun NutritionFoodRow(
    entry: NutritionEntryEntity,
    onEditEntry: (NutritionEntryEntity) -> Unit,
    onDeleteEntry: (NutritionEntryEntity) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                ForceColors.Surface
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            /*
             * FOOD INFORMATION
             */
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        end = 8.dp
                    )
            ) {

                Text(
                    text = entry.foodName,
                    color =
                        ForceColors.TextPrimary,
                    style =
                        MaterialTheme.typography.bodyLarge,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        "${formatNumber(entry.calories)} kcal" +
                                "  •  " +
                                "${formatNumber(entry.proteinGrams)} g protein",
                    color =
                        ForceColors.TextSecondary,
                    style =
                        MaterialTheme.typography.bodySmall
                )
            }


            /*
             * ACTIONS
             */
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = {
                        onEditEntry(entry)
                    },
                    contentPadding =
                        PaddingValues(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                ) {

                    Text(
                        text = "Edit",
                        color =
                            ForceColors.Primary,
                        style =
                            MaterialTheme.typography.labelLarge,
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = {
                        onDeleteEntry(entry)
                    },
                    contentPadding =
                        PaddingValues(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                ) {

                    Text(
                        text = "Delete",
                        color =
                            ForceColors.TextSecondary,
                        style =
                            MaterialTheme.typography.labelLarge,
                        fontWeight =
                            FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun formatNutritionDate(
    timestamp: Long
): String {

    if (isToday(timestamp)) {
        return "Today"
    }

    val formatter =
        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        )

    return formatter.format(
        Date(timestamp)
    )
}


private fun isToday(
    timestamp: Long
): Boolean {

    val selected =
        java.util.Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

    val today =
        java.util.Calendar.getInstance()

    return selected.get(
        java.util.Calendar.YEAR
    ) ==
            today.get(
                java.util.Calendar.YEAR
            ) &&
            selected.get(
                java.util.Calendar.DAY_OF_YEAR
            ) ==
            today.get(
                java.util.Calendar.DAY_OF_YEAR
            )
}

private fun formatNumber(
    value: Double
): String {

    return if (
        value % 1.0 == 0.0
    ) {
        value.toInt().toString()
    } else {
        String.format(
            Locale.US,
            "%.1f",
            value
        )
    }


}