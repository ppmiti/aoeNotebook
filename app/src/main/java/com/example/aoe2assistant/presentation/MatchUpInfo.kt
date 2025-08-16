package com.example.aoe2assistant.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aoe2assistant.DEFAULT_LANGUAGE
import com.example.aoe2assistant.LocalOrchestrator
import com.example.aoe2assistant.LocalVoice
import com.example.aoe2assistant.R
import com.example.aoe2assistant.data.CivInfoMatchUp
import com.example.aoe2assistant.data.MapInfoMatchUp
import com.example.aoe2assistant.data.NotesData
import com.example.aoe2assistant.data.WhenToSpeak
import com.example.aoe2assistant.presentation.individualItems.ButtonCatTile
import com.example.aoe2assistant.presentation.individualItems.ButtonCivTile
import com.example.aoe2assistant.presentation.individualItems.MedievalFont
import com.example.aoe2assistant.presentation.individualItems.WoodenBackground
import com.example.aoe2assistant.presentation.individualItems.medievalFont
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchUpInformationScreen(notesData: NotesData,
                             userCiv: CivInfoMatchUp,
                             opponentCiv: CivInfoMatchUp,
                             mapInfo: MapInfoMatchUp){

    val Orch = LocalOrchestrator.current
    val categoriesIndexes = Orch.getCategoriesIds()
    //val langChosen = Orch.getLangChosen()
    val langChosen = DEFAULT_LANGUAGE;

    val categoriesList = Orch.categoriesList().toList()

    val voiceInstance = LocalVoice.current
    val userCatState = rememberPagerState(pageCount = { categoriesList.size })
    val opponentCatState = rememberPagerState(pageCount = { categoriesList.size })
    val listState = rememberLazyListState()
    val catCoroutineScope = rememberCoroutineScope()
    val listCoroutineScope = rememberCoroutineScope()
    val voiceCoroutineScope = rememberCoroutineScope()

    var whichButtonClicked by remember{ mutableIntStateOf(0) }

    var myBenefits: List<String>
    var opponentBenefits: List<String>

    when (Orch.getIdFromCategoryIndex(userCatState.currentPage)){
        R.string.note -> {
            myBenefits = notesData.getNotes(userCiv.name)?.getNotes(opponentCiv.name, langChosen)?.myNotes?.split("\n")?:emptyList()
            opponentBenefits = notesData.getNotes(userCiv.name)?.getNotes(opponentCiv.name, langChosen)?.oppNotes?.split("\n")?:emptyList()
        }
        R.string.generalNote -> {
            myBenefits = notesData.getNotes(userCiv.name)?.getNotes("General", langChosen)?.myNotes?. split("\n")?:emptyList()
            opponentBenefits = notesData.getNotes(opponentCiv.name)?.getNotes("General", langChosen)?.oppNotes?. split("\n")?:emptyList()
        }
        -1 -> { // external notes case

            val extNotes = Orch.getExtNoteText(categoriesList[userCatState.currentPage],userCiv.name, opponentCiv.name,langChosen)
            myBenefits = extNotes.first.split("\n")
            opponentBenefits = extNotes.second.split("\n")
        }
        else -> {
            myBenefits = userCiv.getBonus(categoriesList[userCatState.currentPage])?: listOf()
            opponentBenefits = opponentCiv.getBonus(categoriesList[userCatState.currentPage])?: listOf()
        }
    }
    voiceInstance.setOpponentBenefits(opponentBenefits)
    voiceInstance.setMyBenefits(myBenefits)
    voiceInstance.setBenefit(categoriesList[userCatState.currentPage])


    LaunchedEffect(key1 = userCatState.currentPageOffsetFraction, key2 = opponentCatState.currentPageOffsetFraction) {
        if (userCatState.currentPageOffsetFraction != opponentCatState.currentPageOffsetFraction) {
            if (userCatState.isScrollInProgress) {
                catCoroutineScope.launch {
                    opponentCatState.scrollToPage(userCatState.currentPage, userCatState.currentPageOffsetFraction)
                }
            } else if (opponentCatState.isScrollInProgress) {
                catCoroutineScope.launch {
                    userCatState.scrollToPage(opponentCatState.currentPage, opponentCatState.currentPageOffsetFraction)
                }
            }
        }
        listCoroutineScope.launch {
            listState.animateScrollToItem(userCatState.currentPage)
        }

        voiceCoroutineScope.launch {
            if (Orch.isVoiceTriggeredBy(WhenToSpeak.ONBOTH) || Orch.isVoiceTriggeredBy(WhenToSpeak.ONBENEFITCHANGE)){
                voiceInstance.stopPlayback()
                voiceInstance.speakBenefit(myBenefits,categoriesList[userCatState.currentPage],true)
                voiceInstance.speakBenefit(opponentBenefits,categoriesList[userCatState.currentPage],false)
            }
        }
    }
    LaunchedEffect(key1 =whichButtonClicked) {
        catCoroutineScope.launch {
            userCatState.scrollToPage(whichButtonClicked)
        }
        voiceCoroutineScope.launch {
            if (Orch.isVoiceTriggeredBy(WhenToSpeak.ONBOTH) || Orch.isVoiceTriggeredBy(WhenToSpeak.ONBENEFITCHANGE)){
                voiceInstance.stopPlayback()

                when (Orch.getIdFromCategoryIndex(userCatState.currentPage)){
                    R.string.note -> {
                        myBenefits = notesData.getNotes(userCiv.name)?.getNotes(opponentCiv.name, langChosen)?.myNotes?.split("\n")?:emptyList()
                        opponentBenefits = notesData.getNotes(userCiv.name)?.getNotes(opponentCiv.name, langChosen)?.oppNotes?.split("\n")?:emptyList()
                    }
                    R.string.generalNote -> {
                        myBenefits = notesData.getNotes(userCiv.name)?.getNotes("General", langChosen)?.myNotes?. split("\n")?:emptyList()
                        opponentBenefits = notesData.getNotes(opponentCiv.name)?.getNotes("General", langChosen)?.oppNotes?. split("\n")?:emptyList()
                    }
                    -1 -> { // external notes case

                        val extNotes = Orch.getExtNoteText(categoriesList[userCatState.currentPage],userCiv.name, opponentCiv.name,langChosen)
                        myBenefits = extNotes.first.split("\n")
                        opponentBenefits = extNotes.second.split("\n")
                    }
                    else -> {
                        myBenefits = userCiv.getBonus(categoriesList[userCatState.currentPage])?: listOf()
                        opponentBenefits = opponentCiv.getBonus(categoriesList[userCatState.currentPage])?: listOf()
                    }
                }
                voiceInstance.setOpponentBenefits(opponentBenefits)
                voiceInstance.setMyBenefits(myBenefits)
                voiceInstance.setBenefit(categoriesList[userCatState.currentPage])

                voiceInstance.speakBenefit(myBenefits,categoriesList[userCatState.currentPage],true)
                voiceInstance.speakBenefit(opponentBenefits,categoriesList[userCatState.currentPage],false)
            }
        }
    }


    Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally){

        WoodenBackground(
            content = {
                LazyRow(state = listState, modifier = Modifier.fillMaxWidth()){
                    items(categoriesIndexes){ cat->

                        ButtonCatTile(
                            onClick = {whichButtonClicked = categoriesIndexes.indexOf(cat)},
                            Orch.getCategoryText(cat),
                            cat == categoriesIndexes[userCatState.currentPage]
                        )

                    }
                }
            },
            50,
            Alignment.Center
        )

        Divider(modifier = Modifier.fillMaxWidth())

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        )
        {
            Image(
                painter = painterResource(id = R.drawable.paper_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // starting description of civs
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(0.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        CivHeader(userCiv.name, userCiv.type, voiceCoroutineScope, true)

                        HorizontalPager(
                            state = userCatState,
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.9f)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    PopulateList(myBenefits)
                                }
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.fillMaxWidth(0.95f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(0.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        CivHeader(opponentCiv.name, opponentCiv.type, voiceCoroutineScope, false)

                        HorizontalPager(
                            state = userCatState,
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.9f)

                                .align(Alignment.CenterHorizontally)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    PopulateList(opponentBenefits)
                                }
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.fillMaxWidth())

                if (mapInfo.name.isNotEmpty() && mapInfo.name != Orch.mapNotActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.3f)
                            .height(0.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            MapHeader(mapInfo.name, mapInfo.type)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.9f),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Column {
                                    PopulateList(listOf(""))
                                }
                            }
                        }
                    }
                }

            }// ending description of civs

        } // ending box for image background

    }


}


fun updateBenefits(){

}



@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CivHeader(civName: String, civType: String, voiceCoroutineScope: CoroutineScope, me: Boolean){
    val voiceInstance = LocalVoice.current
    val localOrch = LocalOrchestrator.current

    Row(verticalAlignment = Alignment.CenterVertically){
        ButtonCivTile({


            if (localOrch.isVoiceTriggeredBy(WhenToSpeak.ONBOTH) || localOrch.isVoiceTriggeredBy(WhenToSpeak.ONCLICK))  {
                voiceCoroutineScope.launch {
                    voiceInstance.stopPlayback()
                    voiceInstance.speakSavedBenefits(me)
                }
            }

            },
            civName,
            -1,
            hButton = 70,
            wButton = 150,
            fontSize = 18)

        Text(civType, modifier = Modifier.padding(4.dp), fontFamily = medievalFont.fontFamily )
    }
}

@Composable
fun MapHeader(mapName: String, mapType: String){
    Row(verticalAlignment = Alignment.CenterVertically){
        Text(mapName, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
        Text("${mapType} Map", modifier = Modifier.padding(8.dp))
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PopulateList(benefits: List<String> = LocalOrchestrator.current.getBenefitsList()){

    for (ib in benefits){
        if (ib.isEmpty()){
            continue
        }
        Text(" -   $ib",
            modifier = Modifier.padding(4.dp),
            color = Color.Black,
            fontFamily = medievalFont.fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MatchUpInformationScreenPreview(){
    val civ1 = CivInfoMatchUp("Britons",
                                "Archery")
    civ1.setBonus("eco",
        listOf("Town Centers cost -50% wood starting in the Castle Age, and many more stuff to fill another line", "Shepherds work 25% faster"))
    civ1.setBonus("mili",
        listOf("longer range", "faster archeries"))
    civ1.setBonus("general",
        listOf(""))
    civ1.setBonus("uniqueTechs",
        listOf("silver", "gold"))
    civ1.setBonus("uniqueUnits",
        listOf("longArcher"))
    civ1.setBonus("notes",
        listOf(""))


    val civ2 = CivInfoMatchUp("Cumans",
        "Cavarly")
    civ2.setBonus("eco",
        listOf("Additional Town Center can be built in the Feudal Age", "Archery Ranges and Stables cost -75 wood"))
    civ2.setBonus("mili",
        listOf("Siege Workshop and Battering Ram available in the Feudal Age","Capped Ram upgrade available in Castle Age","Cavalry 5% faster each age (starting in Feudal Age)"))
    civ2.setBonus("general",
        listOf(""))
    civ2.setBonus("uniqueTechs",
        listOf("silver", "gold"))
    civ2.setBonus("uniqueUnits",
        listOf("Kipchak (cavalry archer)"))
    civ2.setBonus("notes",
        listOf(""))


    val map = MapInfoMatchUp("Arabia",
        "Land")

    MatchUpInformationScreen(  NotesData(HashMap()),
                                civ1,
                                civ2,
                                map)
}