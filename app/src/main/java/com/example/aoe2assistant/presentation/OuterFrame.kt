package com.example.aoe2assistant.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aoe2assistant.LocalOrchestrator
import com.example.aoe2assistant.LocalVoice
import com.example.aoe2assistant.R
import com.example.aoe2assistant.data.CivData
import com.example.aoe2assistant.data.MapData
import com.example.aoe2assistant.data.NotesData
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun OuterFrame(civData: CivData,
               mapData: MapData,
               notesData: NotesData,
               navController: NavHostController,
               dataStatus:  HashMap<String, Pair<Boolean, String>>){

    val title = LocalOrchestrator.current.recoverText(LocalOrchestrator.current.getResourceByName(ScreenLinks.fromRoute(currentRoute(navController))?:""))


    val bottomNavigationItems = listOf(
        ScreenLinks.MatchUp,
        ScreenLinks.CivTree,
        ScreenLinks.Donate,
        ScreenLinks.TechTree
    )

    Scaffold(
        bottomBar = {
            if (currentRoute(navController) != ScreenLinks.MatchUpInfo.route )
            {
                buttonsLowerBar(navController, bottomNavigationItems,title)
            }
        },
        topBar = {
            buttonsTopBar(navController,title)
        }
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(it)) {

            MiddleScreen(civData, mapData, notesData, navController, dataStatus)

        }

    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Preview(showBackground = true)
@Composable
fun OuterFramePreview(){
    OuterFrame(civData = CivData(), MapData(), notesData = NotesData(hashMapOf()), rememberNavController(), hashMapOf<String, Pair<Boolean, String>>())
}