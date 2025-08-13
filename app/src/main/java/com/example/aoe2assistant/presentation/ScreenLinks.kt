package com.example.aoe2assistant.presentation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.aoe2assistant.R

// creating the links to the different screens!
// having one class with all the links remove the need for remembering them

sealed class ScreenLinks(val route: String, @DrawableRes val iconResId: Int? = null, val title: String) {

        object User : ScreenLinks("user", R.drawable.settings_icon, "userSettingsTitle")
        object CivTree : ScreenLinks("civTree", iconResId = R.drawable.civlist_icon , "civilizationsTitle")
        object TechTree : ScreenLinks("techTree", iconResId = -1 , "technologyTreeTitle")
        object MatchUpInfo : ScreenLinks("matchUpInfo", iconResId = R.drawable.matchup_icon, "matchUpInfoTitle")
        object MatchUp : ScreenLinks("matchUp", iconResId = R.drawable.matchup_icon, "matchUpTitle")
        object Donate : ScreenLinks("donate", iconResId = R.drawable.donate, "donateTitle")

        companion object {
                fun fromRoute(route: String?): String? {
                        return when (route) {
                                User.route -> User.title
                                CivTree.route -> CivTree.title
                                TechTree.route -> TechTree.title
                                MatchUpInfo.route -> MatchUpInfo.title
                                MatchUp.route -> MatchUp.title
                                else -> null
                        }
                }
        }
}

sealed class MatchUpLinks(val route: String, val icon: ImageVector) {

        object MatchUpInfo : MatchUpLinks("matchUpInfo", Icons.Filled.Home)
        object MatchUp : MatchUpLinks("matchUp", Icons.Filled.Home)
        object Donate : MatchUpLinks("donate", Icons.Filled.Home)
}