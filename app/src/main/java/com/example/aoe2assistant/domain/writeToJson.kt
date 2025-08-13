package com.example.aoe2assistant.domain

import android.content.Context
import android.widget.Toast
import com.example.aoe2assistant.data.NotesData
import com.example.aoe2assistant.data.SettingsClass
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class WriteToJson{

    enum class TypeOfData {CIV, NOTE, MAP}

    private var _filePath = ""
    private var _dataToWrite: TypeOfData? = null

    private lateinit var _context : Context

    private var _JsonText = ""

    constructor()
    constructor(filePath : String){
        _filePath = filePath
    }
    constructor(filePath : String, context: Context, notes: NotesData){
        _filePath = filePath
        _context = context
        _JsonText = Json.encodeToString(notes)
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

    fun write(notes: NotesData): Boolean{
        if (_filePath.isEmpty()){
            Toast.makeText(_context, "File is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        if (notes.isEmpty()){
            Toast.makeText(_context, "Notes are empty", Toast.LENGTH_SHORT).show()
            return false
        }

        _JsonText = Json.encodeToString(notes)

        try {
            _context.openFileOutput("notesData.txt", Context.MODE_PRIVATE).use {
                it.write(_JsonText.toByteArray())
            }
        }
        catch (e: Exception) {
            Toast.makeText(_context, "impossible to write", Toast.LENGTH_SHORT).show()
            return false
        }

        Toast.makeText(_context, "Write was successful", Toast.LENGTH_SHORT).show()
        return true
    }

    fun write(settingsClass: SettingsClass): Boolean{
        if (_filePath.isEmpty()){
            Toast.makeText(_context, "File is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        _JsonText = Json.encodeToString(settingsClass)

        try {
            _context.openFileOutput(_filePath, Context.MODE_PRIVATE).use {
                it.write(_JsonText.toByteArray())
            }
        }
        catch (e: Exception) {
            Toast.makeText(_context, e.message.toString(), Toast.LENGTH_SHORT).show()
            return false
        }

        Toast.makeText(_context, "Write was successful", Toast.LENGTH_SHORT).show()
        return true
    }

    fun jsonText() : String{
        return _JsonText
    }
}


suspend fun writeSettings(settingsClass: SettingsClass, context: Context){
    val writer = WriteToJson("settings.ini", context, NotesData(hashMapOf()))
    writer.write(settingsClass)
}