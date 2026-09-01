package com.ngedo.force.feature.profile

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ngedo.force.designsystem.ForceColors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Color

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {

    val uiState by
    viewModel.uiState.collectAsState()

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
            Arrangement.spacedBy(16.dp)
    ) {

        /*
         * -------------------------------------------------
         * HEADER
         * -------------------------------------------------
         */

        item {

            Text(
                text = "Profile",
                color = ForceColors.TextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "Manage your body information and training goals.",
                color = ForceColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (uiState.isEditing) {

            /*
             * -------------------------------------------------
             * EDIT MODE
             * -------------------------------------------------
             */

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = ForceColors.Surface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(18.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {

                    Text(
                        text = "Personal Information",
                        color = ForceColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Update your basic body information.",
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = {
                            viewModel.updateName(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Name")
                        },
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        OutlinedTextField(
                            value = uiState.ageInput,
                            onValueChange = { value ->

                                if (
                                    value.isEmpty() ||
                                    value.all { it.isDigit() }
                                ) {
                                    viewModel.updateAge(value)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            label = {
                                Text("Age")
                            },
                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType =
                                        KeyboardType.Number
                                ),
                            singleLine = true
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = "Sex",
                                color = ForceColors.TextSecondary,
                                style =
                                    MaterialTheme.typography.labelMedium
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.spacedBy(6.dp)
                            ) {

                                UnitOption(
                                    text = "Male",
                                    selected =
                                        uiState.sex == "Male",
                                    modifier =
                                        Modifier.weight(1f),
                                    onClick = {
                                        viewModel.updateSex("Male")
                                    }
                                )

                                UnitOption(
                                    text = "Female",
                                    selected =
                                        uiState.sex == "Female",
                                    modifier =
                                        Modifier.weight(1f),
                                    onClick = {
                                        viewModel.updateSex("Female")
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        OutlinedTextField(
                            value = uiState.weightInput,
                            onValueChange = { value ->

                                if (
                                    value.isEmpty() ||
                                    value.matches(
                                        Regex("^\\d*\\.?\\d*$")
                                    )
                                ) {
                                    viewModel.updateWeight(value)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            label = {
                                Text(
                                    if (
                                        uiState.preferredUnits ==
                                        "Metric"
                                    ) {
                                        "Weight (kg)"
                                    } else {
                                        "Weight (lb)"
                                    }
                                )
                            },
                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType =
                                        KeyboardType.Decimal
                                ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uiState.heightInput,
                            onValueChange = { value ->

                                if (
                                    value.isEmpty() ||
                                    value.matches(
                                        Regex("^\\d*\\.?\\d*$")
                                    )
                                ) {
                                    viewModel.updateHeight(value)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            label = {
                                Text(
                                    if (
                                        uiState.preferredUnits ==
                                        "Metric"
                                    ) {
                                        "Height (cm)"
                                    } else {
                                        "Height (in)"
                                    }
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
                }
            }


            /*
             * TRAINING GOAL
             */

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = ForceColors.Surface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(18.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Training Goal",
                        color = ForceColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Choose the main goal FORCE should optimize for.",
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        GoalOption(
                            text = "Build Muscle",
                            selected =
                                uiState.trainingGoal == "Build Muscle",
                            modifier = Modifier.weight(1f),
                            selectedColor = Color(0xFF4CAF6A),
                            onClick = {
                                viewModel.updateTrainingGoal(
                                    "Build Muscle"
                                )
                            }
                        )

                        GoalOption(
                            text = "Lose Fat",
                            selected =
                                uiState.trainingGoal == "Lose Fat",
                            modifier = Modifier.weight(1f),
                            selectedColor = Color(0xFF4CAF6A),
                            onClick = {
                                viewModel.updateTrainingGoal(
                                    "Lose Fat"
                                )
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        GoalOption(
                            text = "Maintain",
                            selected =
                                uiState.trainingGoal == "Maintain",
                            modifier = Modifier.weight(1f),
                            selectedColor = Color(0xFF4CAF6A),
                            onClick = {
                                viewModel.updateTrainingGoal(
                                    "Maintain"
                                )
                            }
                        )

                        GoalOption(
                            text = "Strength",
                            selected =
                                uiState.trainingGoal == "Strength",
                            modifier = Modifier.weight(1f),
                            selectedColor = Color(0xFF4CAF6A),
                            onClick = {
                                viewModel.updateTrainingGoal(
                                    "Strength"
                                )
                            }
                        )
                    }
                }
            }

            /*
 * ACTIVITY LEVEL
 */

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = ForceColors.Surface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(18.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Activity Level",
                        color = ForceColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Choose the level that best matches your usual daily activity.",
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        GoalOption(
                            text = "Sedentary",
                            selected =
                                uiState.activityLevel == "Sedentary",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.updateActivityLevel(
                                    "Sedentary"
                                )
                            }
                        )

                        GoalOption(
                            text = "Lightly Active",
                            selected =
                                uiState.activityLevel == "Lightly Active",
                            modifier = Modifier.weight(1f),
                            selectedColor = Color(0xFF4D8DFF),
                            onClick = {
                                viewModel.updateActivityLevel(
                                    "Lightly Active"
                                )
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        GoalOption(
                            text = "Moderately Active",
                            selected =
                                uiState.activityLevel == "Moderately Active",
                            modifier = Modifier.weight(1f),
                            selectedColor = Color(0xFF4D8DFF),
                            onClick = {
                                viewModel.updateActivityLevel(
                                    "Moderately Active"
                                )
                            }
                        )

                        GoalOption(
                            text = "Very Active",
                            selected =
                                uiState.activityLevel == "Very Active",
                            modifier = Modifier.weight(1f),
                            selectedColor = Color(0xFF4D8DFF),
                            onClick = {
                                viewModel.updateActivityLevel(
                                    "Very Active"
                                )
                            }
                        )
                    }
                }
            }


            /*
             * PREFERRED UNITS
             */

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = ForceColors.Surface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(18.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Preferred Units",
                        color = ForceColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Choose how FORCE should display your measurements.",
                        color = ForceColors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        UnitOption(
                            text = "Metric",
                            selected =
                                uiState.preferredUnits == "Metric",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.updatePreferredUnits(
                                    "Metric"
                                )
                            }
                        )

                        UnitOption(
                            text = "Imperial",
                            selected =
                                uiState.preferredUnits == "Imperial",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.updatePreferredUnits(
                                    "Imperial"
                                )
                            }
                        )
                    }
                }
            }


            /*
             * SAVE
             */

            item {

                Button(
                    onClick = {
                        viewModel.saveProfile()
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                ForceColors.Primary
                        ),

                    shape =
                        RoundedCornerShape(16.dp)
                ) {

                    Text(
                        text = "Save Profile",
                        color = ForceColors.Background,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        } else {

            /*
             * -------------------------------------------------
             * VIEW MODE
             * -------------------------------------------------
             */

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = ForceColors.Surface,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(20.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        /*
                         * PROFILE AVATAR
                         */

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    color = ForceColors.Primary.copy(
                                        alpha = 0.15f
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text =
                                    uiState.name
                                        .trim()
                                        .firstOrNull()
                                        ?.uppercase()
                                        ?: "F",

                                color = ForceColors.Primary,
                                style =
                                    MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(16.dp)
                        )

                        /*
                         * PROFILE NAME + SUMMARY
                         */

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    if (uiState.name.isBlank()) {
                                        "FORCE Athlete"
                                    } else {
                                        uiState.name
                                    },

                                color = ForceColors.TextPrimary,
                                style =
                                    MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            if (uiState.name.isNotBlank()) {

                                Row(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {

                                    /*
                                     * TRAINING GOAL
                                     */

                                    Text(
                                        text = uiState.trainingGoal,

                                        color = Color(0xFF4CAF6A),

                                        style =
                                            MaterialTheme.typography.labelMedium,

                                        fontWeight = FontWeight.Medium,

                                        modifier = Modifier
                                            .background(
                                                color = Color(0xFF123D22),
                                                shape = RoundedCornerShape(50)
                                            )
                                            .padding(
                                                horizontal = 10.dp,
                                                vertical = 5.dp
                                            )
                                    )

                                    /*
                                     * ACTIVITY LEVEL
                                     */

                                    Text(
                                        text = uiState.activityLevel,

                                        color = Color(0xFF4D8DFF),

                                        style =
                                            MaterialTheme.typography.labelMedium,

                                        fontWeight = FontWeight.Medium,

                                        modifier = Modifier
                                            .background(
                                                color = Color(0xFF172D40),
                                                shape = RoundedCornerShape(50)
                                            )
                                            .padding(
                                                horizontal = 10.dp,
                                                vertical = 5.dp
                                            )
                                    )
                                }
                            } else {

                                Text(
                                    text = "Create your fitness profile",

                                    color = ForceColors.TextSecondary,
                                    style =
                                        MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            item {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        ProfileStatCard(
                            title = "Weight",
                            value =
                                "${uiState.weightInput} " +
                                        if (
                                            uiState.preferredUnits ==
                                            "Metric"
                                        ) {
                                            "kg"
                                        } else {
                                            "lb"
                                        },
                            modifier = Modifier.weight(1f)
                        )

                        ProfileStatCard(
                            title = "Height",
                            value =
                                "${uiState.heightInput} " +
                                        if (
                                            uiState.preferredUnits ==
                                            "Metric"
                                        ) {
                                            "cm"
                                        } else {
                                            "in"
                                        },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        ProfileStatCard(
                            title = "Age",
                            value = "${uiState.ageInput} Years",
                            modifier = Modifier.weight(1f)
                        )

                        ProfileStatCard(
                            title = "Sex",
                            value = uiState.sex,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (uiState.nutritionRecommendation != null) {

                item {

                    val recommendation =
                        uiState.nutritionRecommendation!!

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = ForceColors.Surface,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(18.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(14.dp)
                    ) {

                        Text(
                            text = "FORCE Recommended Nutrition",
                            color = ForceColors.TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text =
                                "Based on your body information, activity level and training goal.",
                            color = ForceColors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        /*
                         * DAILY CALORIES
                         */

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text =
                                    recommendation.calories.toString(),
                                color = ForceColors.Primary,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "kcal / day",
                                color = ForceColors.TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        /*
                         * MACROS
                         */

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            NutritionMacroCard(
                                title = "Protein",
                                value =
                                    "${recommendation.proteinGrams} g",
                                modifier = Modifier.weight(1f)
                            )

                            NutritionMacroCard(
                                title = "Carbs",
                                value =
                                    "${recommendation.carbsGrams} g",
                                modifier = Modifier.weight(1f)
                            )

                            NutritionMacroCard(
                                title = "Fat",
                                value =
                                    "${recommendation.fatGrams} g",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Button(
                            onClick = {
                                if (!uiState.nutritionGoalsApplied) {
                                    viewModel.applyRecommendedNutritionGoals()
                                }
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
                                text =
                                    if (uiState.nutritionGoalsApplied) {
                                        "✓ Recommended Goals Applied"
                                    } else {
                                        "Apply Recommended Goals"
                                    },

                                color = ForceColors.Background,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }


            item {

                Button(
                    onClick = {
                        viewModel.startEditing()
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                ForceColors.Surface
                        ),

                    shape =
                        RoundedCornerShape(16.dp)
                ) {

                    Text(
                        text =
                            if (uiState.name.isBlank()) {
                                "Create Profile"
                            } else {
                                "Edit Profile"
                            },

                        color = ForceColors.Primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun NutritionMacroCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .background(
                color = ForceColors.Background,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 12.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = title,
            color = ForceColors.TextSecondary,
            style = MaterialTheme.typography.labelSmall
        )

        Text(
            text = value,
            color = ForceColors.TextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .background(
                color = ForceColors.Surface,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(6.dp)
    ) {

        Text(
            text = title,
            color = ForceColors.TextSecondary,
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = value,
            color = ForceColors.TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GoalOption(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    selectedColor: Color = ForceColors.Primary,
    onClick: () -> Unit
) {

    TextButton(
        onClick = onClick,

        modifier = modifier
            .background(
                color =
                    if (selected) {
                        selectedColor.copy(
                            alpha = 0.15f
                        )
                    } else {
                        ForceColors.Background
                    },

                shape =
                    RoundedCornerShape(12.dp)
            )
    ) {

        Text(
            text = text,

            color =
                if (selected) {
                    selectedColor
                } else {
                    ForceColors.TextSecondary
                },

            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
        )
    }
}


@Composable
private fun UnitOption(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    TextButton(
        onClick = onClick,

        modifier = modifier
            .background(
                color =
                    if (selected) {
                        ForceColors.Primary.copy(
                            alpha = 0.15f
                        )
                    } else {
                        ForceColors.Background
                    },

                shape =
                    RoundedCornerShape(12.dp)
            )
    ) {

        Text(
            text = text,

            color =
                if (selected) {
                    ForceColors.Primary
                } else {
                    ForceColors.TextSecondary
                },

            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
        )
    }
}