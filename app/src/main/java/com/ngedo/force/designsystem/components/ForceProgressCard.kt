package com.ngedo.force.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ngedo.force.designsystem.ForceColors
import com.ngedo.force.designsystem.ForceShapes
import com.ngedo.force.designsystem.ForceSpacing

@Composable
fun ForceProgressCard(

    title: String,

    progress: Float,

    percentage: Int

) {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .background(
                ForceColors.Surface,
                ForceShapes.Large
            )
            .padding(ForceSpacing.Large),

        verticalArrangement = Arrangement.spacedBy(ForceSpacing.Medium)

    ) {

        Text(
            text = title,
            color = ForceColors.TextPrimary,
            style = MaterialTheme.typography.titleLarge
        )

        LinearProgressIndicator(

            progress = { progress },

            modifier = Modifier.fillMaxWidth()

        )

        Text(

            text = "$percentage% Complete",

            color = ForceColors.TextSecondary,

            style = MaterialTheme.typography.bodyMedium

        )

    }

}
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun ForceProgressCardPreview() {

    com.ngedo.force.ui.theme.ForceTheme {

        ForceProgressCard(

            title = "Today's Goal",

            progress = 0.78f,

            percentage = 78

        )

    }

}