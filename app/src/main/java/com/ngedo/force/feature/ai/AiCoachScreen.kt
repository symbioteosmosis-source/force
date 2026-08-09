package com.ngedo.force.feature.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ngedo.force.designsystem.ForceColors
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.layout.imePadding
@Composable
fun AiCoachScreen(
    onBack: () -> Unit,
    viewModel: AiCoachViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForceColors.Background)
    ) {

        // =========================================================
        // SCROLLABLE CONTENT
        // =========================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 20.dp)
        ) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // -----------------------------------------------------
            // HEADER
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = ForceColors.TextPrimary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "AI Coach",
                        color = ForceColors.TextPrimary,
                        fontSize = 24.sp
                    )

                    Text(
                        text = "Your personal training intelligence",
                        color = ForceColors.TextSecondary,
                        fontSize = 13.sp
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = ForceColors.Primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // -----------------------------------------------------
            // GREETING
            // -----------------------------------------------------

            AiGreetingCard()

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // -----------------------------------------------------
            // TOPIC TITLE
            // -----------------------------------------------------

            Text(
                text = "What can I help with?",
                color = ForceColors.TextPrimary,
                fontSize = 18.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // -----------------------------------------------------
            // TOPIC ROW 1
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                AiTopicCard(
                    modifier = Modifier.weight(1f),
                    title = "Workout",
                    icon = Icons.Rounded.FitnessCenter,
                    onClick = {
                        viewModel.selectTopic(
                            AiCoachTopic.WORKOUT
                        )
                    }
                )

                AiTopicCard(
                    modifier = Modifier.weight(1f),
                    title = "Progress",
                    icon = Icons.Rounded.ShowChart,
                    onClick = {
                        viewModel.selectTopic(
                            AiCoachTopic.PROGRESS
                        )
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // -----------------------------------------------------
            // TOPIC ROW 2
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                AiTopicCard(
                    modifier = Modifier.weight(1f),
                    title = "Nutrition",
                    icon = Icons.Rounded.Restaurant,
                    onClick = {
                        viewModel.selectTopic(
                            AiCoachTopic.NUTRITION
                        )
                    }
                )

                AiTopicCard(
                    modifier = Modifier.weight(1f),
                    title = "Recovery",
                    icon = Icons.Rounded.MonitorWeight,
                    onClick = {
                        viewModel.selectTopic(
                            AiCoachTopic.RECOVERY
                        )
                    }
                )
            }

            // -----------------------------------------------------
            // AI RESPONSE
            // -----------------------------------------------------

            if (uiState.response.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(20.dp)
                        )
                        .background(ForceColors.Surface)
                        .padding(18.dp)
                ) {

                    Text(
                        text = "FORCE AI",
                        color = ForceColors.Primary,
                        fontSize = 12.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = uiState.response,
                        color = ForceColors.TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }

            // Extra bottom space for scrolling
            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        // =========================================================
        // MESSAGE INPUT
        //
        // IMPORTANT:
        // This is OUTSIDE the scrollable Column.
        // It therefore stays fixed at the bottom.
        // =========================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ForceColors.Background)
                .imePadding()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 6.dp,
                    bottom = 2.dp
                )
        ) {

            OutlinedTextField(
                value = uiState.userMessage,

                onValueChange = {
                    viewModel.updateMessage(it)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 52.dp,
                        max = 120.dp
                    ),

                placeholder = {
                    Text(
                        text = "Ask your AI Coach...",
                        color = ForceColors.TextSecondary
                    )
                },

                trailingIcon = {

                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.sendMessage()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "Send",
                            tint = ForceColors.Primary
                        )
                    }
                },

                singleLine = false,

                maxLines = 4,

                shape = RoundedCornerShape(18.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ForceColors.Primary,
                    unfocusedBorderColor = ForceColors.TextSecondary,
                    focusedTextColor = ForceColors.TextPrimary,
                    unfocusedTextColor = ForceColors.TextPrimary,
                    cursorColor = ForceColors.Primary
                )
            )
        }
    }
}


// ================================================================
// AI GREETING CARD
// ================================================================

@Composable
private fun AiGreetingCard() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(24.dp)
            )
            .background(ForceColors.Surface)
            .padding(20.dp)
    ) {

        Column {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = ForceColors.Primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Text(
                    text = "AI Coach",
                    color = ForceColors.TextPrimary,
                    fontSize = 17.sp
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Good to see you.",
                color = ForceColors.TextPrimary,
                fontSize = 22.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "I'm ready to help you train smarter, track progress, and improve your workouts.",
                color = ForceColors.TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}


// ================================================================
// AI TOPIC CARD
// ================================================================

@Composable
private fun AiTopicCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .height(100.dp)
            .clip(
                RoundedCornerShape(18.dp)
            )
            .background(ForceColors.Surface)
            .clickable(
                onClick = onClick
            )
            .padding(14.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = ForceColors.Primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = title,
                color = ForceColors.TextPrimary,
                fontSize = 15.sp
            )
        }
    }
}