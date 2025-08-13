package com.example.aoe2assistant.presentation

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.core.os.persistableBundleOf
import com.example.aoe2assistant.LocalOrchestrator
import com.example.aoe2assistant.LocalVoice
import com.example.aoe2assistant.R
import com.example.aoe2assistant.data.BUTTONCOLUMNWIDTH
import com.example.aoe2assistant.data.NotesData
import com.example.aoe2assistant.data.SettingsClass
import com.example.aoe2assistant.data.WhenToSpeak
import com.example.aoe2assistant.domain.CreateNotesFile
import com.example.aoe2assistant.domain.SelectNotesFile
import com.example.aoe2assistant.presentation.individualItems.ButtonCatTile
import com.example.aoe2assistant.presentation.individualItems.MedievalFont
import com.example.aoe2assistant.presentation.individualItems.PaperBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun UserScreen(dataStatus: HashMap<String, Pair<Boolean, String>>, notesData: NotesData)
{

    PaperBackground(
        {
            SettingsLayout(dataStatus,
                notesData)
        },
        alignment = Alignment.Center
    )


}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SettingsLayout(dataStatus: HashMap<String, Pair<Boolean, String>>, notesData: NotesData)
{
    val voiceTestCoroutineScope = rememberCoroutineScope()
    val localOrch = LocalOrchestrator.current
    var numberNotes by remember { mutableIntStateOf(0) }

    val settingsList = listOf("lang",
        "divider",
        "whenToSpeak",
        "voice",
        "sliderVoice",
        "divider",
        "benefitsList",
        "divider",
        "saveNotes",
        "loadNotes",
        "extNotesList")

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        contentPadding = PaddingValues(8.dp)
    ){
        items(settingsList){
                item ->
            when(item){
                "lang"->LanguageSelection(dataStatus)
                "divider"->Divider(modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(8.dp))
                "whenToSpeak"->WhenToSpeakSelection()
                "voice"->VoiceSelection(dataStatus, voiceTestCoroutineScope)
                "sliderVoice"->NumberSlider()
                "benefitsList"->SortableListScreen()
                "saveNotes"->ExportJsonDataButton(notesData)
                "loadNotes"->ImportJsonDataButton(onStateChange = {newNumber -> numberNotes = newNumber})
                "extNotesList"->NotesScreen(numberNotes)

            }
        }
    }




}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LanguageSelection(dataStatus: HashMap<String, Pair<Boolean, String>>)
{
    val localOrch = LocalOrchestrator.current
    var langChosen by remember{ mutableStateOf(localOrch.getLocalLanguage()) }
    var iExpanded by remember{ mutableStateOf(false) }

    val medievalFont = MedievalFont()

    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = localOrch.recoverText(R.string.languageSettings), color = Color.Black, fontFamily = medievalFont.fontFamily, fontWeight = FontWeight.Medium)

        Box {  // box to create drop down menu
            Button(onClick = { iExpanded = true },
                modifier = Modifier
                    .padding(8.dp)
                    .width(BUTTONCOLUMNWIDTH),
                enabled = false,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            )
            {  // button to open menu
                Text(text = langChosen, color = Color.Black)
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Arrow down")
            }
            DropdownMenu(
                expanded = iExpanded,
                onDismissRequest = { iExpanded = false }) {
                for (il in dataStatus.keys) {
                    DropdownMenuItem(text = { Text(text = il, color = Color.Black) },
                        enabled = dataStatus[il]?.first ?: false,
                        onClick = { langChosen = il; iExpanded = false; changeLocalLang(il)})
                }
            }

        }

    }
    localOrch.setLangChosen(langChosen)
}


fun changeLocalLang(lang: String){
    AppCompatDelegate.setApplicationLocales(
        LocaleListCompat.forLanguageTags(
            lang //ISO for language
        )
    )
}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun WhenToSpeakSelection() {
    val localOrch = LocalOrchestrator.current
    val textStartingOption = localOrch.speakOptionToHeader()
    var speakingChoice by remember{ mutableStateOf(textStartingOption) }
    var iExpanded by remember{ mutableStateOf(false) }

    val medievalFont = MedievalFont()

    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = localOrch.recoverText(R.string.speakSettings), color = Color.Black, fontFamily = medievalFont.fontFamily, fontWeight = FontWeight.Medium)


        Box {  // box to create drop down menu
            OutlinedButton(onClick = { iExpanded = true },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(BUTTONCOLUMNWIDTH),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )) {  // button to open menu
                Text(text = speakingChoice , color = Color.Black, fontFamily = medievalFont.fontFamily, fontWeight = FontWeight.Medium)
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Arrow down")
            }

            DropdownMenu(
                expanded = iExpanded,
                onDismissRequest = { iExpanded = false }) {
                for (il in WhenToSpeak.entries) {
                    DropdownMenuItem(text = { Text(text = localOrch.speakOptionToHeader(il) , color = Color.Black, fontFamily = medievalFont.fontFamily, fontWeight = FontWeight.Medium) },
                        onClick = { speakingChoice = localOrch.speakOptionToHeader(il); iExpanded = false; localOrch.setWhenToSpeak(il)})
                }
            }

        }

    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun VoiceSelection(dataStatus: HashMap<String, Pair<Boolean, String>>, scope: CoroutineScope) {
    val tts = LocalVoice.current
    val localOrch = LocalOrchestrator.current
    tts.recoverVoiceOptions(dataStatus)
    var currentVoice by remember{ mutableStateOf(tts.getUiNameFromId(localOrch.getVoice())) }
    var iExpanded by remember{ mutableStateOf(false) }
    val context = LocalContext.current

    val medievalFont = MedievalFont()

    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = localOrch.recoverText(R.string.voice) , color = Color.Black, fontFamily = medievalFont.fontFamily, fontWeight = FontWeight.Medium)


        Box {  // box to create drop down menu
            OutlinedButton(onClick = { iExpanded = true },
                modifier = Modifier
                    .padding(8.dp)
                    .width(BUTTONCOLUMNWIDTH),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) {  // button to open menu
                Text(text = currentVoice , color = Color.Black, fontFamily = medievalFont.fontFamily, fontWeight = FontWeight.Medium)
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Arrow down")
            }
            DropdownMenu(
                expanded = iExpanded,
                onDismissRequest = { iExpanded = false }) {
                val list = tts.getAvailableVoices(localOrch.getLangChosen())
                for (il in list) {
                    DropdownMenuItem(text = { Text(text = il, fontFamily = medievalFont.fontFamily, fontWeight = FontWeight.Medium) },
                        onClick = { currentVoice = il;
                                    iExpanded = false;
                                    localOrch.setVoice(tts.voiceChoiceName(list.indexOf(il)))
                                    scope.launch {
                                        if (!tts.readTestSpeech(list.indexOf(il))){
                                            Toast.makeText(context, "Voice NOT available", Toast.LENGTH_LONG).show()
                                        }
                                    }
                        }
                    )
                }
            }

        }

    }
}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun NumberSlider() {
    val voiceClass = LocalVoice.current
    val localOrch = LocalOrchestrator.current

    var sliderPosition by remember { mutableFloatStateOf(localOrch.getVoiceSpeed()) }

    val medievalFont = MedievalFont()

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = LocalOrchestrator.current.recoverText(R.string.TtsSpeed), color = Color.Black, fontFamily = medievalFont.fontFamily, fontWeight = FontWeight.Medium)
        Box(){
            Text(text = sliderPosition.toString() , color = Color.Black, fontFamily = medievalFont.fontFamily, fontWeight = FontWeight.Medium)
            Slider(
                modifier = Modifier
                    .padding(4.dp)
                    .width(BUTTONCOLUMNWIDTH),
                value = sliderPosition,
                onValueChange = { sliderPosition = it; localOrch.setVoiceSpeed(it); voiceClass.speedTestSpeech(it) },
                valueRange = 0.5f..2f,
                steps = 5,
                colors = SliderDefaults.colors(
                    thumbColor = Color.Black
                )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun SortableListScreen() {
    val localOrch = LocalOrchestrator.current
    //var categoriesMut by remember { mutableStateOf(settingList.toMutableList()) }
    var categoriesMut = LocalOrchestrator.current.categoriesList()
    var index by remember { mutableIntStateOf(0) }
    var moveElementOutput : Pair<MutableList<String>, Int>
    val horizontalAliLeft = Alignment.CenterHorizontally
    val horizontalAliRight = Alignment.Start

    val medievalFont = MedievalFont()

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(240.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.4f),
            horizontalAlignment = horizontalAliLeft,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = LocalOrchestrator.current.recoverText(R.string.topElementText),
                modifier = Modifier.padding(4.dp),
                color = Color.Black,
                fontFamily = medievalFont.fontFamily,
                fontWeight = FontWeight.Medium)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = horizontalAliLeft
                ) {
                    OutlinedButton(onClick = { moveElementOutput = moveElement(categoriesMut,index,true);
                        categoriesMut=moveElementOutput.first;
                        index =moveElementOutput.second;
                        localOrch.setCategoriesList(categoriesMut)
                    },
                        shape = CircleShape,
                        modifier = Modifier.width(BUTTONCOLUMNWIDTH).weight(0.5f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "move element up"
                        )
                    }
                    OutlinedButton(onClick = { moveElementOutput = moveElement(categoriesMut,index,false);
                        categoriesMut=moveElementOutput.first  ;
                        index =moveElementOutput.second;
                        localOrch.setCategoriesList(categoriesMut)
                                             },
                        shape = CircleShape,
                        modifier = Modifier.width(BUTTONCOLUMNWIDTH).weight(0.5f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "move element down"
                        )
                    }
                }
            }

            Text(text = LocalOrchestrator.current.recoverText(R.string.bottomElementText),
                modifier = Modifier.padding(4.dp),
                color = Color.Black,
                fontFamily = medievalFont.fontFamily,
                fontWeight = FontWeight.Medium)
        }



        LazyColumn(modifier = Modifier
            .fillMaxHeight()
            .fillMaxHeight(0.6f),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = horizontalAliRight
        )
        {
            items(categoriesMut){
                    ie ->
            //for (ie in categoriesMut) {
                val buttonText = localOrch.recoverText(ie)
                ButtonCatTile({ index = categoriesMut.indexOf(ie) },
                    buttonText,
                    categoriesMut[index] == ie,
                    60,
                    300,
                    startingFontSize = 18,
                    nextMatchPlaque = true)
            }
        }
    }
}


fun moveElement(categoriesMut : MutableList<String>, index: Int, upwards : Boolean) : Pair<MutableList<String>, Int>{

    if (upwards && index ==0){
        return Pair(categoriesMut, index)
    }
    else if (!upwards && index == categoriesMut.size-1){
        return Pair(categoriesMut, index)
    }

    val item = categoriesMut.removeAt(index);
    val newIndex = index + if (upwards) {-1} else {1}
    categoriesMut.add(newIndex,item)

    return Pair(categoriesMut, newIndex)
}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ExportJsonDataButton(notesData: NotesData) {
    val openDialog = remember { mutableStateOf(false) }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val notesname = remember { mutableStateOf("") }
    val context = LocalContext.current
    val jsonOrch = LocalOrchestrator.current.json
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    fun emptyFields(){
        username.value = ""
        password.value = ""
        notesname.value = ""
    }

    val medievalFont = MedievalFont()

    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = LocalOrchestrator.current.recoverText(R.string.exportNotesLabel),
            color = Color.Black,
            fontFamily = medievalFont.fontFamily,
            fontWeight = FontWeight.Medium)

        OutlinedButton(onClick = { openDialog.value = true; emptyFields() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            modifier = Modifier.width(150.dp)
        ) {
            Text( LocalOrchestrator.current.recoverText(R.string.exportNotesTitle),
                color = Color.Black,
                fontFamily = medievalFont.fontFamily,
                fontWeight = FontWeight.Medium)
        }

    }


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text(LocalOrchestrator.current.recoverText(R.string.exportNotesTitle)) },
            text = {
                Column {

                    CreateNotesFile { uri ->
                        selectedUri = uri
                        // Handle the selected Uri (e.g., read or write data)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = username.value,
                        onValueChange = { username.value = it },
                        label = { Text(LocalOrchestrator.current.recoverText(R.string.username)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = notesname.value,
                        onValueChange = { notesname.value = it },
                        label = { Text(LocalOrchestrator.current.recoverText(R.string.notesname)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = { Text(LocalOrchestrator.current.recoverText(R.string.password)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (selectedUri != null){
                        if (username.value.isNotBlank() && password.value.isNotBlank()) {
                            jsonOrch.exportJsonData(selectedUri!!, username.value, notesname.value, password.value, notesData)

                            val toast = Toast(context)
                            toast.setText("Export successful")
                            toast.show()
                        }
                        else {
                            val toast = Toast(context)
                            toast.setText("no export done")
                            toast.show()
                        }
                    }

                    openDialog.value = false

                }) {
                    Text(LocalOrchestrator.current.recoverText(R.string.exportnotes))
                }
            },
            dismissButton = {
                Button(onClick = { openDialog.value = false }) {
                    Text(LocalOrchestrator.current.recoverText(R.string.cancel))
                }
            }
        )

    }
}



@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ImportJsonDataButton(onStateChange: (Int) -> Unit) {

    val openDialog = remember { mutableStateOf(false) }
    var username = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    val context = LocalContext.current
    val jsonOrch = LocalOrchestrator.current.json
    val notesManager = LocalOrchestrator.current.extNotes
    val localOrch = LocalOrchestrator.current
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    fun emptyFields(){
        username.value = ""
        password.value = ""
    }

    val medievalFont = MedievalFont()

    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = LocalOrchestrator.current.recoverText(R.string.importNotesLabel),
            color = Color.Black,
            fontFamily = medievalFont.fontFamily,
            fontWeight = FontWeight.Medium)

        OutlinedButton(onClick = { openDialog.value = true; emptyFields() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            modifier = Modifier.width(150.dp)
        ) {
            Text(LocalOrchestrator.current.recoverText(R.string.importNotesTitle),
                color = Color.Black,
                fontFamily = medievalFont.fontFamily,
                fontWeight = FontWeight.Medium)
        }

    }


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text(LocalOrchestrator.current.recoverText(R.string.importNotesTitle)) },
            text = {
                Column {

                    SelectNotesFile { uri ->
                        selectedUri = uri
                        // Handle the selected Uri (e.g., read or write data)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = username.value,
                        onValueChange = { username.value = it },
                        label = { Text(LocalOrchestrator.current.recoverText(R.string.username)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = { Text(LocalOrchestrator.current.recoverText(R.string.password)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {

                    var success = true
                    if (selectedUri != null){
                        if (username.value.isNotBlank() && password.value.isNotBlank()) {

                            val temp = localOrch.addExtNote(
                                jsonOrch.readJsonData(selectedUri!!, username.value, password.value)
                            )

                            success = temp

                        }
                        else {
                            success = false
                        }
                    }

                    openDialog.value = false

                    val toast = Toast(context)
                    if (success){
                        toast.setText("Import successful")
                        onStateChange(notesManager.getAvailableNotes().size)

                    }
                    else{
                        toast.setText("No import done")
                    }

                    toast.show()

                }) {
                    Text(LocalOrchestrator.current.recoverText(R.string.importnotes))
                }
            },
            dismissButton = {
                Button(onClick = { openDialog.value = false }) {
                    Text(LocalOrchestrator.current.recoverText(R.string.cancel))
                }
            }
        )

    }
}



@Composable
fun NotesScreen(numberNotes: Int) {
    val notesManager = LocalOrchestrator.current.extNotes
    val personalIndex = notesManager.getAvailableNotes().indexOf("personal")
    val notes = notesManager.getAvailableNotes().toMutableList()
    notes.removeAt(personalIndex)
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var notesNumberState by remember { mutableIntStateOf(numberNotes) }
    var selectedNoteId by remember { mutableStateOf("") }

    if (notesNumberState != numberNotes){
        notesNumberState = numberNotes
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .border(BorderStroke(1.dp, Color.Black)),
        contentAlignment = Alignment.Center
    ){
        if (notes.isEmpty()){
            Text(text = LocalOrchestrator.current.recoverText(R.string.noNotesLoaded))
        }
        else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (noteId in notes){

                    val parts = noteId.split("_")
                    if (parts.size != 3) {
                        continue
                    }

                    val noteName = parts[1]
                    val noteCreator = parts[2]

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "- $noteName by $noteCreator")
                        Button(onClick = {
                            selectedNoteId = noteId
                            showConfirmationDialog = true
                        }) {
                            Text(text = LocalOrchestrator.current.recoverText(R.string.deletenote))
                        }
                    }
                }
            }
        }
    }



    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(text = "Confirm deletion") },
            text = { Text(text = "Are you sure you want to delete this note?") },
            confirmButton = {
                Button(onClick = {
                    notesManager.removeNote(selectedNoteId)
                    showConfirmationDialog = false
                }) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmationDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}






