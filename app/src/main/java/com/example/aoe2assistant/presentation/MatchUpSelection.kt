package com.example.aoe2assistant.presentation

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import com.example.aoe2assistant.LocalOrchestrator
import com.example.aoe2assistant.R
import com.example.aoe2assistant.presentation.individualItems.ButtonCatTile
import com.example.aoe2assistant.presentation.individualItems.ButtonCivTile
import com.example.aoe2assistant.presentation.individualItems.MedievalFont
import com.example.aoe2assistant.presentation.individualItems.MyImageButton

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MatchUpSelectionScreen(civsList: List<String>, mapList: List<String>, navigateToDetail: (Triple<String, String, String>) -> Unit){

    var myCivChosen by remember{mutableStateOf("")}
    var opponentCivChosen by remember{mutableStateOf("")}
    var mapChosen by remember{mutableStateOf("")}

    var selectMyCivDialog by remember{mutableStateOf(false)};
    var selectOtherCivDialog by remember{mutableStateOf(false)};
    var selectMapDialog by remember{mutableStateOf(false)};

    var allMatchUpSelected = true

    val localOrch = LocalOrchestrator.current
    val civListLocaLang = localOrch.getCivNamesLocalLang()

    Box(modifier = Modifier.fillMaxSize()){

        // Background image
        Image(
            painter = painterResource(id = R.drawable.matchup_selection_background), // Your image resource here
            contentDescription = null, // Accessibility purposes
            contentScale = ContentScale.FillBounds, // Scales the image to fit all the space in the box
            modifier = Modifier.fillMaxSize() // Fills the box
        )

        Column(modifier = Modifier.height(300.dp).align(Alignment.Center).offset(y=45.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally) {

            ButtonCatTile({},
                LocalOrchestrator.current.recoverText(R.string.nextGame),
                false,
                hButton = 55,
                wButton = 350,
                vOffset = 0,
                nextMatchPlaque = true,
                enableOnClick = false)


            // MY civ button
            ButtonCivTile(onClick = { selectMyCivDialog = true },
                myCivChosen.ifEmpty {
                    allMatchUpSelected = false
                    localOrch.recoverText(R.string.myCivChoice)
                },
                localOrch.recoverIcon(myCivChosen)?:-1,
                hButton = 60,
                wButton = 250,
                hoffset = 0,
                fontSize = 20)


            // OPPONENT civ button
            ButtonCivTile(onClick = { selectOtherCivDialog = true },
                opponentCivChosen.ifEmpty {
                    allMatchUpSelected = false
                    localOrch.recoverText(R.string.oppCivChoice)
                },
                localOrch.recoverIcon(opponentCivChosen)?:-1,
                hButton = 60,
                wButton = 250,
                hoffset = 0,
                fontSize = 20)

            // MAP button
            if (localOrch.mapChoiceActive){
                Button(onClick = { selectMapDialog = true },
                    modifier = Modifier
                        .width(250.dp)
                        .offset(0.dp, (0).dp),
                    colors = ButtonDefaults.buttonColors (
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {

                    MyImageButton(
                        mapChosen.ifEmpty {
                            allMatchUpSelected = false
                            localOrch.recoverText(R.string.mapMatchUp)
                        },
                        -1
                    )
                }
            }
            else{
                mapChosen = localOrch.mapNotActive
            }

            ButtonCatTile({
                if (allMatchUpSelected) {
                    navigateToDetail(Triple(localOrch.getCivKey(myCivChosen),localOrch.getCivKey(opponentCivChosen),mapChosen))
                }},
                if (allMatchUpSelected){
                    LocalOrchestrator.current.recoverText(R.string.startMatch)
                }
                else{
                    LocalOrchestrator.current.recoverText(R.string.pickMatch)
                },
                allMatchUpSelected,
                hButton = 60,
                wButton = 300,
                vOffset = 0,
                nextMatchPlaque = true)


        }


    }

    if (selectMyCivDialog){
        val (cc,sb) = selectDialog(civListLocaLang)
        myCivChosen = cc;
        selectMyCivDialog = sb
    }
    else if (selectOtherCivDialog){
        val (cc,sb) = selectDialog(civListLocaLang)
        opponentCivChosen = cc;
        selectOtherCivDialog = sb
    }
    else if (selectMapDialog){
        val (cc,sb) = selectDialog(mapList)
        mapChosen = cc;
        selectMapDialog = sb
    }


}

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UseCompatLoadingForDrawables")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun selectDialog(optList : List<String>) : Pair<String, Boolean> {

    var itemChosen by remember{mutableStateOf("")}
    var keepSelectionDialog by remember{mutableStateOf(true)}

    AlertDialog(modifier= Modifier.fillMaxWidth(), onDismissRequest = {keepSelectionDialog = false}) {
        LazyVerticalGrid(GridCells.Fixed(2), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(2.dp)){
            items(optList){
                item->
                ButtonCivTile(onClick = { itemChosen = item; keepSelectionDialog = false},
                    item,
                    LocalOrchestrator.current.recoverIcon(item)?:-1,
                    hButton = 60,
                    wButton = 250,
                    padding = 4)
            }
        }
    }

    return Pair(itemChosen, keepSelectionDialog)
}


fun convertToBitmap(input: Drawable?): Bitmap? {
    if (input is BitmapDrawable) {
        if (input.bitmap != null) {
            return input.bitmap
        }

    }

    return null

}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun MatchUpSelectionScreenPreview(){

    val civsList = listOf("Britons", "Cumans", "Byzantines")
    val mapList = listOf("Arabia", "Arena")

    MatchUpSelectionScreen(civsList, mapList, {})
}