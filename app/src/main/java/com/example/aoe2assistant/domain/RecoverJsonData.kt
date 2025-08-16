package com.example.aoe2assistant.domain

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.aoe2assistant.data.CivData
import com.example.aoe2assistant.data.CivInfo
import com.example.aoe2assistant.data.NotesData
import com.example.aoe2assistant.data.RawCivInfo
import com.example.aoe2assistant.data.SettingsClass
import com.example.aoe2assistant.data.WhenToSpeak
import java.io.File
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.json.Json
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class readFromJson{

    enum class TypeOfData {CIV, NOTE, MAP}

    private var _filePath = ""
    private var _dataToWrite: TypeOfData? = null

    private lateinit var _context : Context

    private var _JsonText = ""

    private var _debugging = true

    constructor()
    constructor(filePath : String){
        _filePath = filePath
    }

    fun setPath(filePath: String){
        _filePath = filePath
    }
    fun getPath() : String{
        return _filePath
    }

    fun setContext(context: Context){
        _context = context
    }

    fun jsonText() : String{
        return _JsonText
    }

    fun readNotes(): NotesData{

        var notes = NotesData(hashMapOf())

        try {
            val file = File(_context.filesDir, _filePath)
            if (file.exists()) {
                _JsonText = FileInputStream(file).use {
                    it.readBytes().toString(Charsets.UTF_8)
                }
                if (_debugging) {Toast.makeText(_context, "file read", Toast.LENGTH_SHORT).show()}
            } else {
                if (_debugging) {Toast.makeText(_context, "File not found 1", Toast.LENGTH_SHORT).show()}
                return notes
            }
        } catch (e: IOException) {
            e.printStackTrace()
            if (_debugging) {Toast.makeText(_context, "Failed to read file", Toast.LENGTH_SHORT).show()}
            return notes
        }

        notes = Json.decodeFromString<NotesData>(_JsonText)

        if (!notes.isEmpty()){
            if (_debugging) {Toast.makeText(_context, "notes loaded successfully", Toast.LENGTH_SHORT).show()}
        }

        return notes
    }


    fun readSettings(): SettingsClass{

        val rawFields = listOf<String>("economy", "military", "uniqueTechs", "uniqueUnits", "generalNote", "note")
        var settings = SettingsClass("en",rawFields, WhenToSpeak.ONBOTH, 1.2f, "")

        try {
            val file = File(_context.filesDir, _filePath)
            if (file.exists()) {
                _JsonText = FileInputStream(file).use {
                    it.readBytes().toString(Charsets.UTF_8)
                }
                if (_debugging) {Toast.makeText(_context, "file read", Toast.LENGTH_SHORT).show()}
            } else {
                    if (_debugging) {Toast.makeText(_context, "File not found 2", Toast.LENGTH_SHORT).show()}
                return settings
            }
        } catch (e: IOException) {
            e.printStackTrace()
                    if (_debugging) {Toast.makeText(_context, "Failed to read file", Toast.LENGTH_SHORT).show()}
            return settings
        }

        settings = Json.decodeFromString<SettingsClass>(_JsonText)

        if (!settings.isEmpty()){
            if (_debugging) {Toast.makeText(_context, "notes loaded successfully", Toast.LENGTH_SHORT).show()}
        }
        else{
            // if the reading had problems, using standard values
            // it is run the 1st time is opened
            settings = SettingsClass("en",rawFields, WhenToSpeak.ONBOTH, 1.2f, "")
        }

        return settings
    }
}

/*
@Composable
fun recoverJsonData(civData: CivData, langList: List<String>) : HashMap<String, Pair<Boolean, String>> {

    var langAvailables = hashMapOf<String, Pair<Boolean, String>>()

    for (il in langList){

        val filePath = "data$il.json"
        val context = LocalContext.current
        val inputStream: InputStream
        var inputString: String =""

        try {
            inputStream = context.assets.open(filePath)
            inputString = inputStream.bufferedReader().use { it.readText() }
        }
        catch (e: Exception) {
            langAvailables[il] = Pair(false,"file could not be opened")
        }

        var rawInfo = listOf<RawCivInfo>()
        if(inputString.isNotEmpty()){
            rawInfo = Gson().fromJson(inputString, object : TypeToken<List<RawCivInfo>>() {}.type)
        }
        else{
            langAvailables[il] = Pair(false,"no information read")
        }

        if (!langAvailables.containsKey(il)){
            fromRawToListCiv(rawInfo, civData, il)
            langAvailables[il] = Pair(true,"success")
        }

    }
    return langAvailables
}
*/

fun fromRawToListCiv(rawInfo: List<RawCivInfo>, civData: CivData, lang: String){
    val rawFields = civData.getRawFields()

    for (civ in rawInfo){
        val iciv = CivInfo(civ.civ_id,civ.name,civ.type,civ.icon)
        for (field in rawFields){
            val templist = splittingCivBonusString(civ.getBonus(field))
            iciv.addBonus(field, templist)
        }
        civData.addCiv(lang,civ.name,iciv)
    }
    civData.loadKeyMap()
}

fun splittingCivBonusString(input: String?) : List<String>{
    if (input.isNullOrEmpty()){
        return listOf<String>()
    }

    val temp: List<String> = input.split("\n")

    return temp
}
