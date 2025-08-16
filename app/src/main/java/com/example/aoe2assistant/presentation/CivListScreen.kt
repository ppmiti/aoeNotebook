package com.example.aoe2assistant.presentation

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aoe2assistant.DEFAULT_LANGUAGE
import com.example.aoe2assistant.LocalOrchestrator
import com.example.aoe2assistant.LocalVoice
import com.example.aoe2assistant.R
import com.example.aoe2assistant.data.ALLCIVS
import com.example.aoe2assistant.data.BUTTONCOLUMNWIDTHCIVPAGECIV
import com.example.aoe2assistant.data.BUTTONCOLUMNWIDTHCIVPAGELANG
import com.example.aoe2assistant.data.CivData
import com.example.aoe2assistant.data.CivInfo
import com.example.aoe2assistant.data.MapData
import com.example.aoe2assistant.data.NotesData
import com.example.aoe2assistant.data.oppNotesModifs
import com.example.aoe2assistant.presentation.individualItems.ButtonCatTile
import com.example.aoe2assistant.presentation.individualItems.ButtonCivTile
import com.example.aoe2assistant.presentation.individualItems.MedievalFont
import com.example.aoe2assistant.presentation.individualItems.MyImageButton
import com.example.aoe2assistant.presentation.individualItems.PaperBackground
import com.example.aoe2assistant.presentation.individualItems.medievalFont

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CivListScreen(civsList: List<String>,
                    civData: CivData,
                    mapData: MapData,
                    notesData: NotesData,
                    navController: NavHostController,
                    dataStatus:  HashMap<String, Pair<Boolean, String>>){

    val civsListLocalLang = LocalOrchestrator.current.getCivNamesLocalLang()

    PaperBackground(
            {
                CivsListContent(civsListLocalLang,
                civData,
                mapData,
                notesData,
                navController,
                dataStatus
            )
        },
        alignment = Alignment.Center
        )



}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CivsListContent(civsList: List<String>,
                    civData: CivData,
                    mapData: MapData,
                    notesData: NotesData,
                    navController: NavHostController,
                    dataStatus:  HashMap<String, Pair<Boolean, String>>
){

    val localOrch = LocalOrchestrator.current
    val generalNote = localOrch.resources.recoverText(R.string.generalOption)
    val allCivlNote = localOrch.resources.recoverText(R.string.allCivsCiv)

    var selectMyCivDialog by remember{ mutableStateOf(false) };
    var myCivChosen by remember{mutableStateOf("")}

    var selectOppCivDialog by remember{ mutableStateOf(false) }
    var oppCivChosen by remember{mutableStateOf(generalNote)}

    val oppCivList = civsList.toMutableList()
    oppCivList.add(0,generalNote)
    val myCivList = civsList.toMutableList()
    myCivList.add(0,allCivlNote)

    var myNotes by remember{mutableStateOf("")}
    var myTempNotes by remember{mutableStateOf("")}
    var oppNotes by remember{mutableStateOf("")}
    var oppTempNotes by remember{mutableStateOf("")}

    var notesEditable by remember{mutableStateOf(false)}
    val civInfo = civData.getCiv(localOrch.getLangChosen(),myCivChosen)

    var iExpanded by remember{ mutableStateOf(false) }
    var langChosen by remember{ mutableStateOf(localOrch.resources.getLocalLanguage()) }
    langChosen = DEFAULT_LANGUAGE

    localOrch.writeNotes(notesData)

    fun emptyNotes(){
        myNotes = ""
        myTempNotes = ""
        oppNotes = ""
        oppTempNotes = ""
    }

    Column(modifier = Modifier.fillMaxSize()){

        ButtonCivTile({
                selectMyCivDialog = true;
                val mycivinput = if (myCivChosen == allCivlNote) { ALLCIVS } else { localOrch.getCivKey(myCivChosen) }
                notesData.changeNote(mycivinput
                    ,langChosen,
                    localOrch.getCivKey(oppCivChosen),
                    myNotes,
                    oppNotes,
                    localOrch.getCivKeys()
                )
                emptyNotes()
            },
            myCivChosen.ifEmpty {
                localOrch.recoverText(R.string.myCivChoice)
            },
            localOrch.recoverIcon(myCivChosen)?:-1,
            fontSize = 24
        )

        if (myCivChosen.isNotEmpty()){
            val mycivinput = if (myCivChosen == allCivlNote) { ALLCIVS } else { localOrch.getCivKey(myCivChosen) }
            myNotes = notesData.getNotes(mycivinput)?.getNotes(localOrch.getCivKey(oppCivChosen), langChosen)?.myNotes?:""
        }

        if (oppCivChosen.isNotEmpty()){
            val mycivinput = if (myCivChosen == allCivlNote) { ALLCIVS } else { localOrch.getCivKey(myCivChosen) }
            oppNotes = notesData.getNotes(mycivinput)?.getNotes(localOrch.getCivKey(oppCivChosen), langChosen)?.oppNotes?:""
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            items(civData.getRawFields()){
                    item ->   // replacing it by item

                if (item == "note"){

                    Column(modifier = Modifier
                        .padding(8.dp)
                        .padding(top = 16.dp)
                        .fillMaxWidth())
                    {
                        Row (modifier = Modifier
                            .padding(8.dp)
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly){


                            Text(text = item.uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,
                                fontWeight = FontWeight.W700,
                                fontFamily = medievalFont.fontFamily)

                            // language choice
                            /*
                            Box {  // box to create drop down menu


                                OutlinedButton(
                                    enabled = myCivChosen.isNotEmpty(),
                                    onClick = { iExpanded = true },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .width(BUTTONCOLUMNWIDTHCIVPAGELANG),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color.Black,
                                        disabledContainerColor = Color.Transparent,
                                        disabledContentColor = Color.Gray
                                    )
                                )
                                {  // button to open menu
                                    Text(text = langChosen,
                                        color = Color.Black,
                                        fontWeight = FontWeight.W700,
                                        fontFamily = medievalFont.fontFamily)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Arrow down")
                                }

                                DropdownMenu(
                                    expanded = iExpanded,
                                    onDismissRequest = { iExpanded = false })
                                {
                                    val list = dataStatus.keys
                                    for (il in list) {
                                        DropdownMenuItem(text = { Text(text = il) },
                                            onClick = {
                                                if (myNotes.isNotEmpty() || oppNotes.isNotEmpty()){
                                                    val mycivinput = if (myCivChosen == allCivlNote) { ALLCIVS } else { localOrch.getCivKey(myCivChosen) }
                                                    notesData.changeNote(mycivinput, langChosen, localOrch.getCivKey(oppCivChosen), myNotes, oppNotes, localOrch.getCivKeys())
                                                }
                                                iExpanded = false;
                                                langChosen = il
                                                emptyNotes()
                                            }
                                        )
                                    }
                                }

                            }
                            */

                            // opponent's civ choice

                            ButtonCivTile(
                                {
                                    if (myCivChosen.isNotEmpty()){
                                        selectOppCivDialog = true
                                        val mycivinput = if (myCivChosen == allCivlNote) { ALLCIVS } else { localOrch.getCivKey(myCivChosen) }
                                        notesData.changeNote(mycivinput, langChosen, localOrch.getCivKey(oppCivChosen), myNotes, oppNotes, localOrch.getCivKeys())
                                        emptyNotes()
                                    }
                                },
                                oppCivChosen.ifEmpty {
                                    localOrch.recoverText(R.string.myCivChoice)
                                },
                                localOrch.recoverIcon(localOrch.getCivKey(oppCivChosen))?:-1,
                                wButton = BUTTONCOLUMNWIDTHCIVPAGECIV.value.toInt()
                            )

                        }

                        // containing text fields for writing notes
                        Box (modifier = Modifier.fillMaxWidth()) {

                            Column (modifier = Modifier.fillMaxWidth()) {
                                // my civ notes
                                if (myCivChosen != allCivlNote) {
                                    OutlinedTextField(value = myNotes, onValueChange = { myNotes = it },
                                        readOnly = !notesEditable,
                                        maxLines = 5, minLines = 5,
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        label = {
                                            Text(
                                                if (myCivChosen.isEmpty()) {
                                                    ""
                                                } else {
                                                    "$myCivChosen ${
                                                        LocalOrchestrator.current.recoverText(
                                                            R.string.playingwith
                                                        )
                                                    }"
                                                }
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedBorderColor = Color.Black,
                                            unfocusedBorderColor = Color.Black,
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black,
                                            focusedLabelColor = Color.Black,
                                            unfocusedLabelColor = Color.Black,
                                            focusedPrefixColor = Color.Black,
                                            unfocusedPrefixColor = Color.Black,
                                            focusedSuffixColor = Color.Black,
                                            unfocusedSuffixColor = Color.Black)
                                    )
                                }

                                if (oppCivChosen != generalNote) {
                                    OutlinedTextField(value = oppNotes, onValueChange = {oppNotes = it},
                                        readOnly = !notesEditable,
                                        maxLines = 5, minLines = 5,
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        label = {
                                            Text(
                                                if (oppCivChosen.isEmpty()){
                                                    ""
                                                }
                                                else{
                                                    "$oppCivChosen : ${LocalOrchestrator.current.recoverText(R.string.playingagainst)} $myCivChosen"
                                                },
                                                fontWeight = FontWeight.W700,
                                                fontFamily = medievalFont.fontFamily
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedBorderColor = Color.Black,
                                            unfocusedBorderColor = Color.Black,
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black,
                                            focusedLabelColor = Color.Black,
                                            unfocusedLabelColor = Color.Black,
                                            focusedPrefixColor = Color.Black,
                                            unfocusedPrefixColor = Color.Black,
                                            focusedSuffixColor = Color.Black,
                                            unfocusedSuffixColor = Color.Black)
                                    )
                                }

                                // row of buttons: Edit, Save, cancel
                                Row(modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End) {
                                    OutlinedButton(enabled = myCivChosen.isNotEmpty(),
                                        onClick = {
                                            val mycivinput = if (myCivChosen == allCivlNote) { ALLCIVS } else { localOrch.getCivKey(myCivChosen) }
                                            if (notesEditable) { // before clicking it was in edit mode => save
                                                notesData.changeNote(
                                                    mycivinput,
                                                    langChosen,
                                                    localOrch.getCivKey(oppCivChosen),
                                                    myNotes,
                                                    oppNotes,
                                                    localOrch.getCivKeys()
                                                )
                                                localOrch.writeNotes(notesData)
                                            }
                                            else{ // before clicking it was in read mode => start editing
                                                myTempNotes = myNotes
                                                oppTempNotes = oppNotes

                                            }
                                            notesEditable = !notesEditable

                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent,
                                            contentColor = Color.Black,
                                            disabledContainerColor = Color.Transparent,
                                            disabledContentColor = Color.Gray)
                                    ) {
                                        Text(
                                            text = if (notesEditable) {
                                                localOrch.recoverText(R.string.save)
                                            } else {
                                                localOrch.recoverText(R.string.edit)
                                            },
                                            color = Color.Black,
                                            fontWeight = FontWeight.W700,
                                            fontFamily = medievalFont.fontFamily
                                        )
                                    }

                                    if (notesEditable) {
                                        OutlinedButton(enabled = myCivChosen.isNotEmpty(),
                                            onClick = { myNotes = myTempNotes ; oppNotes = oppTempNotes ; notesEditable = false}
                                            ,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Transparent,
                                                contentColor = Color.Black,
                                                disabledContainerColor = Color.Transparent,
                                                disabledContentColor = Color.Gray)
                                        ) {
                                            Text(
                                                localOrch.recoverText(R.string.cancel)
                                                , color = Color.Black
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }

                    if (localOrch.debug){
                        TextField(value = localOrch.getNotesString(), onValueChange = {},
                            readOnly = false,
                            maxLines = 10, minLines = 10,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                else if (item != "generalNote"){
                    CivDescriptionElements2(item, civInfo)
                }
            }

        }

        // add here infinte scrolling down
        // add in it the modification for the notes
        // it should have another button for the other civ + General option
        // it should have a text field to write, maybe limiting the char would be good
        // it should be possible to save, undo changes and enable/disable editing
    }

    if (selectMyCivDialog){
        val (cc,sb) = selectDialog(myCivList)
        myCivChosen = cc;
        selectMyCivDialog = sb

        if (myCivChosen == allCivlNote && oppCivList[0] == generalNote){
            oppCivList.take(1)
        }
        else if (oppCivList[0] != generalNote){
            oppCivList.add(0,generalNote)
        }
    }
    else if (selectOppCivDialog){
        val (cc,sb) = selectDialog(oppCivList)
        if (oppCivChosen != cc){
            oppNotes = ""
        }
        if (cc.isEmpty()){
            oppCivChosen = ""
        }
        oppCivChosen = cc;
        selectOppCivDialog = sb
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CivDescriptionElements2(elementId: String, civInfo: CivInfo?){

    Text(text = LocalOrchestrator.current.recoverText(elementId).uppercase(),
        modifier = Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
            .fillMaxWidth(),
        color = Color.Black,
        fontFamily = medievalFont.fontFamily,
        fontWeight = FontWeight.W700,
        fontSize = MaterialTheme.typography.titleMedium.fontSize)


    civInfo?.let {
        Text(text = " - " + civInfo.getBonus(elementId)!!.joinToString("\n - "),
            //fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            fontFamily = medievalFont.fontFamily,
            color = Color.Black)
    }

}