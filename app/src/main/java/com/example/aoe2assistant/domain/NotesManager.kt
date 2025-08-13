package com.example.aoe2assistant.domain

import android.content.Context
import android.net.Uri
import com.example.aoe2assistant.ERROR_NOTES_LOADING_VERSION
import com.example.aoe2assistant.data.NotesData
import com.example.aoe2assistant.data.TextOfNotes
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File

data class NotesManager(
    private val listNotes: HashMap<String, NotesData> = HashMap()
) {

    private lateinit var _jsonOrch : JsonOrchestrator
    private lateinit var _context : Context

    fun setJsonOrch(input : JsonOrchestrator){
        _jsonOrch = input
    }

    fun setContext(input : Context){
        _context = input
    }

    fun loadNotesFromStorage(): MutableList<String> {
        val output = mutableListOf<String>()
        val files = _context.filesDir.listFiles()
        files?.filter { it.name.endsWith(".txt") }?.forEach { file ->
            val parts = file.name.split("_")
            if (parts.size == 4) {
                val noteName = parts[2]
                val noteCreator = parts[3]
                val password = noteName + noteCreator
                val uri = Uri.fromFile(file)
                val jsonData = _jsonOrch.readJsonData(uri, noteCreator, password)

                output.add(addNotes(jsonData))
            }
        }

        return output
    }

    suspend fun saveNotesToStorage() {
        listNotes.forEach { (key, value) ->
            val parts = key.split("_")
            if (parts.size == 3) {
                val noteName = parts[1]
                val noteCreator = parts[2]
                val password = noteName + noteCreator
                val fileName = "external_note_${noteName}_${noteCreator}.txt"
                val file = File(_context.filesDir, fileName)
                val uri = Uri.fromFile(file)
                _jsonOrch.exportJsonData(uri, noteCreator, noteName, password, value)
            }
        }
    }

    private fun getNoteId(noteId: String): String {
        return "ext_$noteId"
    }

    private fun addNotes(noteId: String, newNotes: NotesData): String {
        val key = getNoteId(noteId)
        return if (!listNotes.containsKey(key)) {
            listNotes[key] = newNotes
            key
        } else {
            ""
        }
    }

    fun addNotes(jsonNotes: JsonObject?): String {
        if (jsonNotes == null) {
            return ""
        }
        if (jsonNotes.containsKey("username") && jsonNotes.containsKey("version") && jsonNotes.containsKey("notesName") && jsonNotes.containsKey("data")) {
            val userName = jsonNotes["username"].toString().trim('"')
            val notesName = jsonNotes["notesName"].toString().trim('"')
            val versionLoadedNotes = jsonNotes["version"].toString().trim('"')
            val noteId = "${userName}_${notesName}"
            val rawNotes = jsonNotes["data"].toString()
                .replace("\\", "")
                .trim('"')
            val notes = Json.decodeFromString<NotesData>(rawNotes)
            return if (notes.isEmpty()) {
                ""
            } else if (noteVersionMatching(versionLoadedNotes)) {
                ERROR_NOTES_LOADING_VERSION
            } else {
                addNotes(noteId, notes)
            }
        }
        return ""
    }

    fun removeNote(noteId: String) {
        val key = getNoteId(noteId)
        listNotes.remove(key)
    }

    fun getAvailableNotes(): List<String> {
        return listNotes.keys.toList()
    }

    // key = notes name = notes id
    // ouput:
    // Pair 1st are my civ notes
    // Pair 2nd are my opp notes
    fun recoverNote(key: String, myciv: String, oppciv: String, langChosen: String): Pair<String, String> {
        var noteId = key;
        if (!key.startsWith("ext")){
            noteId = getNoteId(noteId)
        }

        if (!listNotes.containsKey(noteId)) {
            return Pair("", "")
        }

        // now, I must have the noteId key
        // due to previous if
        val notes = listNotes[noteId]!!
        val textNotes = notes.getNotes(myciv)?.getNotes(oppciv,langChosen)?: TextOfNotes("","")
        return Pair(textNotes.myNotes, textNotes.oppNotes)
    }

    // note version matching

    private fun noteVersionMatching(noteVersion: String): Boolean
    {
        return isVersionOlder(noteVersion, "0.0.0")
    }

    private fun isVersionOlder(current: String, other: String): Boolean {
        val parts1 = current.split(".")
        val parts2 = other.split(".")

        val maxLength = maxOf(parts1.size, parts2.size)

        for (i in 0 until maxLength) {
            val num1 = parts1.getOrNull(i)?.toIntOrNull() ?: 0
            val num2 = parts2.getOrNull(i)?.toIntOrNull() ?: 0

            if (num1 < num2) return true
            if (num1 > num2) return false
        }

        return false // They're equal
    }
}