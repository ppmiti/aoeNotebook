package com.example.aoe2assistant.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.aoe2assistant.LocalOrchestrator
import com.example.aoe2assistant.R
import com.example.aoe2assistant.presentation.individualItems.MyImageButton


@Composable
fun buttonsLowerBar(
    navController: NavHostController,
    items: List<ScreenLinks>,
    title : String
) {
    Box(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.buttons_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxWidth()
        )

        BottomNavigation (
            backgroundColor = Color.Transparent,
            contentColor = Color(0xFF930800)

        ) {
            val currentRoute = currentRoute(navController)
            items.forEach { screen ->
                if (!LocalOrchestrator.current.techTreeActive && screen.route.toString().contains("tech", ignoreCase = true)){
                    // nothing to be done
                }
                else {
                    BottomNavigationItem(
                        icon = {
                                    Icon(ImageVector.vectorResource(screen.iconResId!!),
                                        "",
                                        modifier = Modifier.fillMaxSize(0.7f),
                                        tint = Color(168,18,18)
                                        )
                               },
                        selected = currentRoute == screen.route,
                        onClick = {
                            // This if check gives us a "singleTop" behavior where we do not create a
                            // second instance of the composable if we are already on that destination
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route)
                            }
                        }
                    )
                }

            }
        }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}