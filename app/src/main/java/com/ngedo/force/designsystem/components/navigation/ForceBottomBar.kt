package com.ngedo.force.designsystem.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ngedo.force.designsystem.ForceColors
import androidx.compose.ui.tooling.preview.Preview
import com.ngedo.force.ui.theme.ForceTheme

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
        containerColor = ForceColors.Surface
    ) {

        items.forEachIndexed { index, title ->

            NavigationBarItem(

                selected = selectedIndex == index,

                onClick = { onItemSelected(index) },

                icon = {

                    Icon(
                        imageVector = icons[index],
                        contentDescription = title
                    )

                },

                label = {

                    Text(title)

                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ForceColors.Primary,
                    selectedTextColor = ForceColors.Primary
                )

            )

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