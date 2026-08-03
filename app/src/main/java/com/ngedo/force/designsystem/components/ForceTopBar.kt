package com.ngedo.force.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ngedo.force.designsystem.ForceColors
import com.ngedo.force.designsystem.ForceSpacing
import com.ngedo.force.ui.theme.ForceTheme

@Composable
fun ForceTopBar(

    greeting: String,

    userName: String

) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ForceSpacing.Large)
    ) {

        Text(
            text = greeting,
            color = ForceColors.TextSecondary,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = userName,
            color = ForceColors.TextPrimary,
            style = MaterialTheme.typography.headlineLarge
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun ForceTopBarPreview() {

    ForceTheme {
        ForceTopBar(
            greeting = "Good Evening 👋",
            userName = "Layton"
        )
    }
}