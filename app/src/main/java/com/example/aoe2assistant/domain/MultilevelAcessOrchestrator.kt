package com.example.aoe2assistant.domain

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import com.example.aoe2assistant.ERROR_NOTES_LOADING_VERSION
import com.example.aoe2assistant.R
import com.example.aoe2assistant.data.CivData
import com.example.aoe2assistant.data.GRAL_CIV
import com.example.aoe2assistant.data.IconsData
import com.example.aoe2assistant.data.NotesData
import com.example.aoe2assistant.data.ResourcesClass
import com.example.aoe2assistant.data.SettingsClass
import com.example.aoe2assistant.data.WhenToSpeak
import com.example.aoe2assistant.presentation.ScreenLinks
import kotlinx.serialization.json.JsonObject

@RequiresApi(Build.VERSION_CODES.O)
data class MultilevelAcessOrchestrator (val json : JsonOrchestrator,
                                        val extNotes: NotesManager,
                                        val settings: SettingsClass,
                                        val resources: ResourcesClass,
                                        val civData: CivData,
                                        val icons: IconsData,
                                        val debug: Boolean = false){


    val mapChoiceActive = false
    val mapNotActive = "NoMap"
    val techTreeActive = false
    private lateinit var _context: Context

    init {
        extNotes.setJsonOrch(json)
        settings._categoriesList.addAll(
            extNotes.loadNotesFromStorage()
        )
        json.setDebugging(debug)
    }

    fun setContext(context: Context){
        _context = context
    }

    // in here, use it to save the categories.
    // maybe a string as input, if there is like ex_1 == external notes
    // if it is just a number, it is the id for resources. Call the respective fn
    // maybe just checking if string contains _ or if it contains ext or sth like that
    fun categoriesList(): MutableList<String> {
        return settings._categoriesList
    }

    fun setCategoriesList(list: MutableList<String>){
        settings.setCategoriesList(list)
    }

    fun getCategoriesIds(): List<Int> {

        return (0 until settings._categoriesList.size).toList()

    }

    fun getIdFromCategoryIndex(index: Int): Int {
        return settings.getBenefitId(settings._categoriesList[index])
    }

    fun getCategoryText(tempId: Int): String{

        return if ( settings.getBenefitId(settings._categoriesList[tempId]) == -1){

            val temp = settings._categoriesList[tempId]

            if (temp.startsWith("ext")){
                val listParts = temp.split("_")
                if (listParts.size != 3){
                    temp
                } else{
                    listParts[2] + " by " + listParts[1]
                }

            } else {
                temp
            }

        }
        else {

            resources.recoverText(settings.getBenefitId(settings._categoriesList[tempId]))

        }
    }

    fun addExtNote(jsonNotes : JsonObject?): Boolean{
        val noteKey = extNotes.addNotes(jsonNotes)

        return if (noteKey == ERROR_NOTES_LOADING_VERSION){
            Toast.makeText(_context, "Note version does not correspond to current app version", Toast.LENGTH_SHORT).show()
            true
        } else if (noteKey.isNotEmpty()){
            settings._categoriesList.add(noteKey)
            true
        } else {
            false
        }
    }

    fun getAvailableExtNotes(){

    }

    fun getBenefitsList() : List<String> {
        return settings.benefitsList
    }

    // key = notes name = notes id
    // ouput:
    // Pair 1st are my civ notes
    // Pair 2nd are my opp notes
    fun getExtNoteText(key: String, myciv: String, oppciv: String, langChosen: String): Pair<String, String>{
        return extNotes.recoverNote(key, myciv, oppciv, langChosen)

    }

    // text to speech settings

    fun isVoiceTriggeredBy(whenToSpeak: WhenToSpeak): Boolean{
        return settings.speak == whenToSpeak
    }

    fun whenToSpeak() : WhenToSpeak {
        return settings.speak
    }

    fun speakOptionToHeader(): String {
        return resources.speakOptionToHeader(settings.speak)
    }

    fun speakOptionToHeader(option: WhenToSpeak): String {
        return resources.speakOptionToHeader(option)
    }

    fun setWhenToSpeak(option: WhenToSpeak){
        settings.speak = option
    }

    fun setVoice(voice: String){
        settings.voice = voice
    }
    fun getVoice(): String{
        return settings.voice
    }

    fun setVoiceSpeed(speed: Float){
        settings.ttsSpeed = speed
    }
    fun getVoiceSpeed(): Float{
        return settings.ttsSpeed
    }


    // multi language support
    fun recoverText(input: String): String{

        return if (input.startsWith("ext_")){
            input.substringAfter('_')
        }
        else{
            resources.recoverText(settings.getBenefitId(input))
        }

    }

    fun recoverText(id: Int): String {
        return resources.recoverText(id)
    }
    fun recoverIcon(input: String): Int ? {
        return icons.getIcon(getCivKey(input))
    }

    fun getResourceByName(name: String): Int {
        return when (name)
        {
            "userSettingsTitle" -> R.string.userSettingsTitle
            "civilizationsTitle" -> R.string.civilizationsTitle
            "technologyTreeTitle" -> R.string.technologyTreeTitle
            "matchUpInfoTitle" -> R.string.matchUpInfoTitle
            "matchUpTitle" -> R.string.matchUpTitle
            else -> R.string.matchUpTitle
        }
    }


    fun getLangChosen(): String {
        return settings.langChosen
    }

    fun getLocalLanguage(): String {
        return resources.getLocalLanguage()
    }

    fun setLangChosen(lang: String) {
        settings.langChosen = lang
    }

    fun getCivKey(civ: String): String {
        return if (civ == resources.recoverText(R.string.generalOption))
        {
            GRAL_CIV
        }
        else
        {
            civData.getKeyFromCiv(civ)
        }
    }

    fun getCivKeys():List<String> {
        return civData.getKeyCivs() + GRAL_CIV
    }

    fun getCivNamesLocalLang():List<String> {
        return civData.getCivs(resources.getLocalLanguage())
    }


    // json part

    suspend fun writeSettings(settingsClass: SettingsClass){
        json.write(settingsClass)
    }

    fun writeNotes(notesData: NotesData){
        json.write(notesData)
    }
    fun getNotesString() : String {
        return json.jsonTextNotes()
    }
}