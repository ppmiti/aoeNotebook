package com.example.aoe2assistant

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.navigation.compose.rememberNavController
import com.example.aoe2assistant.data.CivData
import com.example.aoe2assistant.data.IconsData
import com.example.aoe2assistant.data.MapData
import com.example.aoe2assistant.data.MapInfo
import com.example.aoe2assistant.data.ResourcesClass
import com.example.aoe2assistant.data.SettingsClass
import com.example.aoe2assistant.data.WhenToSpeak
import com.example.aoe2assistant.domain.JsonOrchestrator
import com.example.aoe2assistant.domain.MultilevelAcessOrchestrator
import com.example.aoe2assistant.domain.NotesManager
import com.example.aoe2assistant.domain.VoiceClass
import com.example.aoe2assistant.domain.readFromJson
import com.example.aoe2assistant.domain.recoverJsonData
import com.example.aoe2assistant.presentation.OuterFrame
import com.example.aoe2assistant.presentation.debugItems.DebugItems
import com.example.aoe2assistant.ui.theme.AOE2assistantTheme
import java.util.Locale

val LocalVoice = compositionLocalOf { VoiceClass() }
val LocalOrchestrator = compositionLocalOf { MultilevelAcessOrchestrator(JsonOrchestrator(), NotesManager(hashMapOf()), SettingsClass("", listOf(""),WhenToSpeak.ONCLICK,1f,""), ResourcesClass(), CivData(), IconsData()) }
val LocalDebugItems = compositionLocalOf { DebugItems(false) }
val ERROR_NOTES_LOADING_VERSION: String = "ERROR_NOTES_LOADING_VERSION"

class MainActivity : ComponentActivity() {

    private lateinit var textToSpeech: TextToSpeech
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.US
            }
        }

        textToSpeech.setSpeechRate(0.8f)


        setContent {

            val mapData = MapData()
            val civData = CivData()
            val navController = rememberNavController()

            val map1 = MapInfo(0, "Arabia", "Land", "")
            val map2 = MapInfo(1, "Arena", "Closed", "")
            val map3 = MapInfo(2, "Black Forest", "Land", "")
            val map4 = MapInfo(3, "Islands", "Water", "")

            mapData.addMap(map1.name,map1)
            mapData.addMap(map2.name,map2)
            mapData.addMap(map3.name,map3)
            mapData.addMap(map4.name,map4)

            val langList = listOf<String>("en", "es", "fr", "it", "hu")
            var dataStatus:  HashMap<String, Pair<Boolean, String>>

            AOE2assistantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // json orchestrator
                    val jsonOrch = JsonOrchestrator()
                    jsonOrch.setContext(LocalContext.current)

                    val jsonReader = readFromJson("settings.ini")
                    jsonReader.setContext(LocalContext.current)

                    // notes manager
                    val notesData = jsonOrch.readNotes()
                    val notesManager = NotesManager(hashMapOf(Pair("personal",notesData)))
                    notesManager.setContext(LocalContext.current)

                    val resources = ResourcesClass(LocalContext.current.resources)

                    // reading settings
                    val settings = jsonOrch.readSettings(resources.getLocalLanguage())

                    // voice local
                    val textToSpeechWrapper = VoiceClass()
                    textToSpeechWrapper.setTextToSpeechApi(textToSpeech)
                    textToSpeechWrapper.setPreferencesClass(resources)

                    // loading data into CivData class
                    civData.setRawFields(settings.benefitsList)
                    dataStatus = recoverJsonData(civData,langList)

                    val icons = IconsData(LocalContext.current.resources, civData.getCivs("en"))


                    // local debug items orch
                    val debugItems = DebugItems(false)
                    debugItems.setContext(LocalContext.current)

                    // local orchestrator
                    val localOrch = MultilevelAcessOrchestrator(
                        jsonOrch,
                        notesManager,
                        settings,
                        resources,
                        civData,
                        icons,
                        debugItems.debugging
                    )
                    localOrch.setContext(LocalContext.current)

                    CompositionLocalProvider(LocalTextStyle provides TextStyle(color = Color.Black)){

                        CompositionLocalProvider(LocalVoice provides textToSpeechWrapper) {
                            CompositionLocalProvider(LocalOrchestrator provides localOrch) {
                                CompositionLocalProvider(LocalDebugItems provides debugItems) {
                                    OuterFrame(
                                        civData,
                                        mapData,
                                        notesData,
                                        navController,
                                        dataStatus
                                    )
                                }
                            }
                        }


                    }




                }
            }
        }
    }
}