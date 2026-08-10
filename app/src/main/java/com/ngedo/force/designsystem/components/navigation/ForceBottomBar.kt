package com.ngedo.force.designsystem.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.ngedo.force.designsystem.ForceColors
import com.ngedo.force.ui.theme.ForceTheme
import androidx.compose.foundation.clickable

@Composable
fun ForceBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {

    val items = listOf(
        "Home",
        "Workout",
        "Progress",
        "Nutrition",
        "Profile"
    )

    val icons = listOf(
        Icons.Rounded.Home,
        Icons.Rounded.FitnessCenter,
        Icons.Rounded.BarChart,
        Icons.Rounded.Fastfood,
        Icons.Rounded.Person
    )

    NavigationBar(
        containerColor = ForceColors.Surface,
        tonalElevation = 0.dp,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .padding(
                    horizontal = 8.dp,
                    vertical = 6.dp
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEachIndexed { index, title ->

                val selected = selectedIndex == index

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clip(
                            RoundedCornerShape(18.dp)
                        )
                        .background(
                            if (selected) {
                                ForceColors.Background
                            } else {
                                ForceColors.Surface
                            }
                        )
                        .then(
                            if (selected) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = ForceColors.Primary.copy(
                                        alpha = 0.55f
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .padding(
                            horizontal = 4.dp,
                            vertical = 5.dp
                        )
                        .then(
                            Modifier
                                .clickable {
                                    onItemSelected(index)
                                }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = icons[index],
                        contentDescription = title,
                        modifier = Modifier.size(
                            if (selected) {
                                26.dp
                            } else {
                                24.dp
                            }
                        ),
                        tint = if (selected) {
                            ForceColors.Primary
                        } else {
                            ForceColors.TextSecondary
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = title,
                        fontSize = 11.sp,
                        color = if (selected) {
                            ForceColors.Primary
                        } else {
                            ForceColors.TextSecondary
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForceBottomBarPreview() {

    ForceTheme {

        ForceBottomBar(
            selectedIndex = 0,
            onItemSelected = {}
        )
    }
}