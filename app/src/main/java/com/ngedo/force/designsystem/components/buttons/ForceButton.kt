package com.ngedo.force.designsystem.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ngedo.force.designsystem.ForceColors
import com.ngedo.force.designsystem.ForceShapes

@Composable
fun ForceButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = ForceShapes.Medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = ForceColors.Primary
        )
    ) {
        Text(text)
    }
}