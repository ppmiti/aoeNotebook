package com.example.aoe2assistant.domain

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.aoe2assistant.data.CivData
import com.example.aoe2assistant.data.CivInfo
import com.example.aoe2assistant.data.NotesData
import com.example.aoe2assistant.data.RawCivInfo
import com.example.aoe2assistant.data.SettingsClass
import com.example.aoe2assistant.data.WhenToSpeak
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class JsonOrchestrator {
    private var _myJsonData: JsonObject = JsonObject(emptyMap())
    private val _fileNameNotes = "myPersonalGamesNotes.json"
    private val _fileNameSettings = "myPersonalGamesSettings.ini"

    private var _filePath = ""
    private var _dataToWrite: readFromJson.TypeOfData? = null

    private lateinit var _context : Context

    private var _JsonText = ""
    private var _JsonTextNotes = ""

    private var debugging = false

    constructor()
    constructor(filePath : String){
        _filePath = filePath
    }
    constructor(filePath : String, debugging: Boolean){
        _filePath = filePath
        this.debugging = debugging
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
    fun jsonTextNotes() : String{
        return _JsonTextNotes
    }

    fun setDebugging(debug: Boolean){
        debugging = debug
    }

    // ---------------------------------------------------------------------------------------
    // ---------------------------------- WRITING --------------------------------------------
    // ---------------------------------------------------------------------------------------

    fun exportJsonData(uri: Uri, username: String, notesName: String, password: String, notesData: NotesData) {
        val key = generateKey(username, password)

        // Build the JSON object with username and data
        val jsonWithUsername = buildJsonObject {
            put("username", JsonPrimitive(username.trim('"')))
            put("notesName", JsonPrimitive(notesName))
            put("version", JsonPrimitive(_context.packageName))
            put("data", JsonPrimitive(Json.encodeToString(notesData)))
        }

        // Convert the JSON object to a string
        val jsonString = Json.encodeToString(JsonObject.serializer(), jsonWithUsername)

        // Encrypt the JSON string
        val encryptedJsonString = encrypt(jsonString, key)

        // Write the encrypted string to the file
        _context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            OutputStreamWriter(outputStream).use { writer ->
                writer.write(encryptedJsonString)
            }
        } ?: run {
            // Handle error: unable to open output stream
            throw IOException("Unable to open output stream for URI: $uri")
        }
    }

    fun write(notes: NotesData): Boolean{
        if (_fileNameNotes.isEmpty()){
            if (debugging) {Toast.makeText(_context, "Notes file not defined", Toast.LENGTH_SHORT).show() }
            return false
        }

        if (notes.isEmpty()){
            if (debugging) { Toast.makeText(_context, "Notes are empty", Toast.LENGTH_SHORT).show() }
            return false
        }

        _JsonTextNotes = Json.encodeToString(notes)

        try {
            _context.openFileOutput(_fileNameNotes, Context.MODE_PRIVATE).use {
                it.write(_JsonTextNotes.toByteArray())
            }
        }
        catch (e: Exception) {
            if (debugging) { Toast.makeText(_context, "impossible to write", Toast.LENGTH_SHORT).show() }
            return false
        }

        if (debugging) { Toast.makeText(_context, "Write notes was successful", Toast.LENGTH_SHORT).show() }
        return true
    }

    fun write(settingsClass: SettingsClass): Boolean{
        if (_fileNameSettings.isEmpty()){
            if (debugging) { Toast.makeText(_context, "Settings file not defined", Toast.LENGTH_SHORT).show() }
            return false
        }

        _JsonText = Json.encodeToString(settingsClass)

        try {
            _context.openFileOutput(_fileNameSettings, Context.MODE_PRIVATE).use {
                it.write(_JsonText.toByteArray())
            }
        }
        catch (e: Exception) {
            if (debugging) {Toast.makeText(_context, e.message.toString(), Toast.LENGTH_SHORT).show()}
            return false
        }

        if (debugging) { Toast.makeText(_context, "Write settings was successful", Toast.LENGTH_SHORT).show() }
        return true
    }


    // ---------------------------------------------------------------------------------------
    // ---------------------------------- READING --------------------------------------------
    // ---------------------------------------------------------------------------------------


    fun readJsonData(uri: Uri, username: String, password: String): JsonObject? {
        val key = generateKey(username, password)

        val jsonString: String

        // Read the encrypted data from the file
        return try {
            _context.contentResolver.openInputStream(uri)?.use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val encryptedJsonString = reader.readText()
                    // Decrypt the JSON string
                    jsonString = decrypt(encryptedJsonString, key)
                    // Parse the decrypted JSON string
                    Json.decodeFromString<JsonObject>(jsonString)
                }
            }
        } catch (e: Exception) {
            // Handle errors, such as file not found or read errors
            e.printStackTrace()
            null
        }
    }


    fun readNotes(): NotesData{

        var notes = NotesData(hashMapOf())

        try {
            val file = File(_context.filesDir, _fileNameNotes)
            if (file.exists()) {
                _JsonText = FileInputStream(file).use {
                    it.readBytes().toString(Charsets.UTF_8)
                }
                if (debugging) { Toast.makeText(_context, "file read notes", Toast.LENGTH_SHORT).show() }
            } else {
                if (debugging) { Toast.makeText(_context, "File not found 4", Toast.LENGTH_SHORT).show() }
                return notes
            }
        } catch (e: IOException) {
            e.printStackTrace()
            if (debugging) { Toast.makeText(_context, "Failed to read file", Toast.LENGTH_SHORT).show() }
            return notes
        }

        notes = Json.decodeFromString<NotesData>(_JsonText)

        if (!notes.isEmpty()){
            if (debugging) { Toast.makeText(_context, "notes loaded successfully", Toast.LENGTH_SHORT).show() }
        }

        return notes
    }


    fun readSettings(defaultLang: String = "en"): SettingsClass{

        val rawFields = listOf<String>("economy", "military", "uniqueTechs", "uniqueUnits", "generalNote", "note")
        var settings = SettingsClass(defaultLang, rawFields, WhenToSpeak.ONBOTH, 1f, "")

        try {
            val file = File(_context.filesDir, _fileNameSettings)
            if (file.exists()) {
                _JsonText = FileInputStream(file).use {
                    it.readBytes().toString(Charsets.UTF_8)
                }
                if (debugging) {Toast.makeText(_context, "file read settings", Toast.LENGTH_SHORT).show()}
            } else {
                if (debugging) {Toast.makeText(_context, "File not found 6", Toast.LENGTH_SHORT).show()}
                return settings
            }
        } catch (e: IOException) {
            e.printStackTrace()
            if (debugging) {Toast.makeText(_context, "Failed to read file", Toast.LENGTH_SHORT).show()}
            return settings
        }

        settings = Json.decodeFromString<SettingsClass>(_JsonText)

        if (!settings.isEmpty()){
            if (debugging) {Toast.makeText(_context, "settings loaded successfully", Toast.LENGTH_SHORT).show()}
        }
        else{
            // if the reading had problems, using standard values
            // it is run the 1st time is opened
            settings = SettingsClass(defaultLang, rawFields, WhenToSpeak.ONBOTH, 1f, "")
        }

        return settings
    }



    fun getJsonData(): String {
        return Json.encodeToString(JsonObject.serializer(), _myJsonData)
    }

    // ---------------------------------------------------------------------------------------
    // ---------------------------------- ENCRYPT --------------------------------------------
    // ---------------------------------------------------------------------------------------


    private fun generateKey(username: String, password: String): SecretKeySpec {
        val key = (username + password).toByteArray()
        val sha = MessageDigest.getInstance("SHA-256")
        val keyBytes = sha.digest(key)
        return SecretKeySpec(keyBytes, "AES")
    }

    private fun encrypt(data: String, key: SecretKeySpec): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return encryptedBytes.joinToString("") { "%02x".format(it) }
    }

    private fun decrypt(data: String, key: SecretKeySpec): String {
        val encryptedBytes = data.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return decryptedBytes.toString(Charsets.UTF_8)
    }



    // ---------------------------------------------------------------------------------------
    // ---------------------------------- RECOVER CIV DATA -----------------------------------
    // ---------------------------------------------------------------------------------------


    fun recoverJsonData(civData: CivData, langList: List<String>) : HashMap<String, Pair<Boolean, String>> {

        val langAvailables = hashMapOf<String, Pair<Boolean, String>>()

        for (il in langList){

            val filePath = "data$il.json"
            val inputStream: InputStream
            var inputString: String =""

            try {
                inputStream = _context.assets.open(filePath)
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


    private fun fromRawToListCiv(rawInfo: List<RawCivInfo>, civData: CivData, lang: String){
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

    private fun splittingCivBonusString(input: String?) : List<String>{
        if (input.isNullOrEmpty()){
            return listOf<String>()
        }

        val temp: List<String> = input.split("\n")

        return temp
    }
}