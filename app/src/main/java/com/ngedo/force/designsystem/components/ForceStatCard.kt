package com.ngedo.force.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ngedo.force.designsystem.ForceColors

@Composable
fun ForceStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color = ForceColors.Primary
) {

    Column(
        modifier = modifier
            .background(
                color = ForceColors.Surface,
                shape = RoundedCornerShape(18.dp)
            )
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = title,
            color = ForceColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            color = accentColor,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}