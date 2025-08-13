package com.example.aoe2assistant.domain

import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import com.example.aoe2assistant.R
import com.example.aoe2assistant.data.ResourcesClass
import com.example.aoe2assistant.data.SettingsClass
import java.util.prefs.Preferences

class VoiceClass {

    private var _voiceReady = true;
    private lateinit var _resources: ResourcesClass
    private lateinit var _textToSpeech: TextToSpeech
    private lateinit var _listBenefitesOpponent: List<String>
    private lateinit var _listBenefitesMe: List<String>
    private lateinit var _benefit: String
    private var _voiceOptions = mutableMapOf<String, List<Voice>>()

    fun recoverVoiceOptions(dataStatus: HashMap<String, Pair<Boolean, String>> ){

        if (_voiceOptions.isNotEmpty()){
            return
        }

        val setVoices = _textToSpeech.voices;

        if (setVoices.isEmpty()){
            return
        }

        val listVoices = setVoices.mapNotNull { it }


        for (ikey in dataStatus.keys){
            val listVoicesOutput = mutableListOf<Voice>()
            val tempListVoices = mutableListOf<String>()
            var i = 0
            for ( j in listVoices.indices){

               if ((listVoices[j].locale.language == ikey || listVoices[j].name.startsWith(ikey))
                   && !listVoices[j].isNetworkConnectionRequired){
                   if (tempListVoices.contains(listVoices[j].locale.displayName)){
                       continue;
                   }
                   tempListVoices.add(listVoices[j].locale.displayName)
                   listVoicesOutput.add(listVoices[j])
                   i += 1
               }

               if (i >= 4 ){
                   break
               }
            }
            _voiceOptions[ikey] = listVoicesOutput
            tempListVoices.clear()
        }
    }

    // voices
    fun getCurrentVoice(): String {
        return _textToSpeech.voice.name
    }

    fun getAvailableVoices(lang: String): List<String> {

        if (!_voiceOptions.containsKey(lang)){
            return listOf()
        }

        val tempList = _voiceOptions[lang]?.mapNotNull { it.locale.displayName }

        // change names, if they are the same

        return tempList?: listOf()
    }

    fun readTestSpeech(pos: Int): Boolean {
        _textToSpeech.stop()

        when(_textToSpeech.isLanguageAvailable(_voiceOptions[_resources.getLocalLanguage()]?.get(pos)?.locale)){
            TextToSpeech.LANG_MISSING_DATA -> {_voiceReady = false; return false;}
            TextToSpeech.LANG_NOT_SUPPORTED-> {_voiceReady = false; return false;}
            else -> _textToSpeech.setLanguage(_voiceOptions[_resources.getLocalLanguage()]?.get(pos)?.locale);
        }
        _voiceReady = true
        _textToSpeech.speak(_resources.recoverText(R.string.testSpeech), TextToSpeech.QUEUE_FLUSH, null, null)
        return true
    }

    fun speedTestSpeech(speed: Float) {
        if (!_voiceReady) return;
        _textToSpeech.stop()
        _textToSpeech.setSpeechRate(speed)
        _textToSpeech.speak(_resources.recoverText(R.string.testSpeech), TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun voiceChoiceName(voicePos: Int): String{
        return _voiceOptions[_resources.getLocalLanguage()]?.get(voicePos)?.name?:""
    }

    fun getUiNameFromId(id: String): String {
        val listVoicesForCurrentLang = _voiceOptions[_resources.getLocalLanguage()]?: listOf()

        if (listVoicesForCurrentLang.isEmpty()){
            return ""
        }

        var posVoice = -1;
        for (iVoice in listVoicesForCurrentLang) {
            if (iVoice.name == id ) {
                posVoice = listVoicesForCurrentLang.indexOf(iVoice)
                break
            }
        }

        return if (posVoice < 0)
        {
            _voiceOptions[_resources.getLocalLanguage()]?.get(0)?.locale?.displayName?:""
        }
        else
        {
            _voiceOptions[_resources.getLocalLanguage()]?.get(posVoice)?.locale?.displayName?:""
        }
    }

    fun setBenefit(input: String){
        _benefit = input
    }

    fun setMyBenefits(input: List<String>){
        _listBenefitesMe = input
    }

    fun setOpponentBenefits(input: List<String>){
        _listBenefitesOpponent = input
    }

    fun setTextToSpeechApi(input: TextToSpeech ){
        _textToSpeech = input
    }

    fun setPreferencesClass(input: ResourcesClass){
        _resources = input
    }

    suspend fun stopPlayback(){
        if (!_voiceReady) return;

        _textToSpeech.stop()
        _textToSpeech.speak("", TextToSpeech.QUEUE_FLUSH, null, null)
    }


    suspend fun speakSavedBenefits(me:Boolean){

        if (_benefit.isEmpty()){
            return
        }

        if (!_voiceReady) return;

        var text = ""
        var listBenefits: List<String>

        if (me){
            text += "Your"
            listBenefits = _listBenefitesMe
        } else {
            text += "The opponents'"
            listBenefits = _listBenefitesOpponent
        }

        text += when(_benefit){
            "economy" -> " economic benefits"
            "military" -> " military benefits"
            "uniqueTechs" -> " unique technologies"
            "uniqueUnits" -> " unique units"
            "generalNote" -> " general notes"
            "note" -> " notes"
            else -> " characteristics"
        }

        _textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null)

        if (listBenefits.isEmpty()){
            _textToSpeech.speak("are empty", TextToSpeech.QUEUE_ADD, null, null)
            return
        }

        for (ib in listBenefits){
            val itext = (listBenefits.indexOf(ib)+1).toString() + ". " + ib
            _textToSpeech.speak(itext, TextToSpeech.QUEUE_ADD, null, null)
        }

    }


    suspend fun speakBenefit(textList: List<String>, benefit: String, me:Boolean){
        if (!_voiceReady) return;

        if (textList.isEmpty()) return

        var text = ""

        text += if (me){
            "Your"
        } else {
            "The opponents'"
        }

        text += when(benefit) {
            "economy" -> " economic benefits"
            "military" -> " military benefits"
            "uniqueTechs" -> " unique technologies"
            "uniqueUnits" -> " unique units"
            "generalNote" -> " general notes"
            "note" -> " notes"
            else -> " this notes"
        }

        _textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null)

        for (ib in textList){
            val itext = (textList.indexOf(ib)+1).toString() + ". " + ib
            _textToSpeech.speak(itext, TextToSpeech.QUEUE_ADD, null, null)
        }

    }

    fun speakText(text: String) {
        if (!_voiceReady) return;
        _textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }


}