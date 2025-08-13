package com.example.aoe2assistant.presentation

import android.annotation.SuppressLint
import androidx.compose.material.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aoe2assistant.LocalOrchestrator
import com.example.aoe2assistant.LocalVoice
import com.example.aoe2assistant.data.CivData
import com.example.aoe2assistant.data.CivInfo
import com.example.aoe2assistant.data.MapData
import com.example.aoe2assistant.data.MapInfo
import com.example.aoe2assistant.data.NotesData
import com.example.aoe2assistant.data.WhenToSpeak
import com.example.aoe2assistant.domain.PrepareMatchUpInfo
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
@Composable
fun MiddleScreen(civData: CivData,
                 mapData: MapData,
                 notesData: NotesData,
                 navController: NavHostController,
                 dataStatus:  HashMap<String, Pair<Boolean, String>>){

    var changingFromSettingToAnother by remember{ mutableStateOf(false) }
    val settingsCoroutineScope = rememberCoroutineScope()
    val localOrch = LocalOrchestrator.current


    val test = navController.previousBackStackEntry.toString()

    LaunchedEffect(key1 = changingFromSettingToAnother) {
        if (navController.previousBackStackEntry?.destination?.route == ScreenLinks.User.route
                && navController.currentBackStackEntry?.destination?.route != ScreenLinks.User.route){

            settingsCoroutineScope.launch {
                localOrch.writeSettings(localOrch.settings)
                localOrch.extNotes.saveNotesToStorage()
            }

        }
    }

    NavHost(navController = navController, startDestination = ScreenLinks.MatchUp.route){


        composable(route = ScreenLinks.MatchUp.route){

            if (changingFromSettingToAnother){
                changingFromSettingToAnother = false
            }

            MatchUpSelectionScreen(civData.getCivs(localOrch.getLangChosen()), mapData.getMaps(), navigateToDetail = {
                // This part is responsible for passing data from the current screen to the detail screen,
                // It usilizes the savedStateHandle, which is a component of the Compose navigation framework

                // currentBackStackEntry :  stores the sequence of pages(screens) that we have navigated in the app
                // savedStateHandle : it is used to pass data between screens, it is used to store and retrieve data that need to be pass to the next screen
                // set("cat", it)  : it sets a key-value pair in the savedStateHandle


                navController.currentBackStackEntry?.savedStateHandle?.set("my", it.first)
                navController.currentBackStackEntry?.savedStateHandle?.set("opp", it.second)
                navController.currentBackStackEntry?.savedStateHandle?.set("map", it.third)
                navController.navigate(ScreenLinks.MatchUpInfo.route)
            }
            )
        }

        composable(route = ScreenLinks.MatchUpInfo.route){

            if (changingFromSettingToAnother){
                changingFromSettingToAnother = false
            }

            // on top, we saved data in the currentBackStackEntry
            // in here we retrieved that information, if it exists

            /*
            val category = navController.previousBackStackEntry?.savedStateHandle?.get<Category>("cat") ?: Category("","","","")
            CategoryDetailScreen(category = category)
            */

            val myCivName = navController.previousBackStackEntry?.savedStateHandle?.get<String>("my")?:""
            val opCivName = navController.previousBackStackEntry?.savedStateHandle?.get<String>("opp")?:""
            val mapName = navController.previousBackStackEntry?.savedStateHandle?.get<String>("map")?:""

            if (myCivName.isEmpty() || opCivName.isEmpty() || mapName.isEmpty()){
                navController.navigate(ScreenLinks.MatchUp.route)
            }

            val matchUpInfoTriplet = PrepareMatchUpInfo(civData.getCiv(localOrch.getLangChosen(), myCivName)?: CivInfo(-1,myCivName,"",""),
                civData.getCiv(localOrch.getLangChosen(), opCivName)?: CivInfo(-1,opCivName,"",""),
                mapData.getMap(mapName)?: MapInfo(-1,mapName,"", "")
            )
            MatchUpInformationScreen(notesData, matchUpInfoTriplet.first, matchUpInfoTriplet.second, matchUpInfoTriplet.third)

                /*
                (listOfCatsInput = categoryList.toString(),
                notesData = notesData,
                userCiv = matchUpInfoTriplet.first,
                opponentCiv = matchUpInfoTriplet.second,
                mapInfo = matchUpInfoTriplet.third)
                */

        }

        composable(route = ScreenLinks.User.route){

            if (!changingFromSettingToAnother){
                changingFromSettingToAnother = true
            }
            //LocalOrchestrator.current.extNotes.loadNotesFromStorage()
            UserScreen(dataStatus, notesData)
        }

        composable(route = ScreenLinks.CivTree.route){

            if (changingFromSettingToAnother){
                changingFromSettingToAnother = false
            }

            CivListScreen(civData.getCivs(localOrch.getLangChosen()),
                        civData,
                        mapData,
                        notesData,
                        navController,
                        dataStatus)
        }

        composable(route = ScreenLinks.TechTree.route){

            if (changingFromSettingToAnother){
                changingFromSettingToAnother = false
            }

            Text("TechTree")
            Text("TechTree")
        }

        composable(route = ScreenLinks.Donate.route){

            if (changingFromSettingToAnother){
                changingFromSettingToAnother = false
            }

            DonateScreen()
        }
    }

}
