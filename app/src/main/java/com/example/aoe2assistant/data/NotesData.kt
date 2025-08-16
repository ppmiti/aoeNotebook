package com.example.aoe2assistant.data

import com.example.aoe2assistant.DEFAULT_LANGUAGE
import kotlinx.serialization.Serializable

@Serializable
class NotesData(var data: HashMap<String, CivNoteInfo>) {
    // keys are civ names,  the contents are the notes for that civ

    fun getNotes(myCiv: String) : CivNoteInfo?{
        return data[myCiv]
    }

    fun addNotes(myCiv: String, value:CivNoteInfo){
        data[myCiv] = value
    }

    fun isEmpty() : Boolean{
        return data.isEmpty()
    }

    fun changeNote(myCiv: String, lang: String, opp:String, myNote:String, oppNote:String, civlist: List<String> = listOf()){

        if (myCiv.isEmpty() && opp.isEmpty()){
            return
        }

        if (myNote.isEmpty() && oppNote.isEmpty()){
            return
        }

        if (myCiv==ALLCIVS)
        {
            val originalCivNote = data[myCiv]?.getNotes(opp,lang)?.oppNotes?:""

            for (imyciv in civlist){

                if (data.contains(imyciv)){
                    val civn = data[imyciv]!!
                    val notes = civn.getNotes(opp,lang)
                    if (notes != null) {
                        if (notes.oppNotes.contains(originalCivNote)) {  // I added new info into the existing one
                            notes.oppNotes.replace(originalCivNote, oppNote)
                        }
                        else if(notes.oppNotes.trim().isEmpty()){
                            notes.oppNotes = oppNote;
                        }
                    }
                }
                else {
                    changeAddNote(imyciv, lang, opp, myNote, oppNote)
                }
            }
        }

        changeAddNote(myCiv, lang, opp, myNote, oppNote)

    }

    private fun changeAddNote(myCiv: String, lang: String, opp:String, myNote:String, oppNote:String){
        if (!data.contains(myCiv)){

            val civNoteToAdd = CivNoteInfo(myCiv, hashMapOf())
            val notes = TextOfNotes(myNote, oppNote)
            civNoteToAdd.writeNote(opp, lang, notes)

            data[myCiv] = civNoteToAdd
        }
        else{
            val civn = data[myCiv]!!  // error triggered, sth wrong with the code
            // if we arrive here, the civ must be there

            val notes = TextOfNotes(myNote, oppNote)
            civn.writeNote(opp, lang, notes)

        }
    }

    fun getCivs(lang: String):List<String>{
        return data.keys.toList().sorted() ?: listOf()
    }

    fun getCivsOld(lang:String):List<String>{
        return data.keys.filter { it.contains(lang) }.toList().sorted()
    }
}


data class RawNoteInfo(val civ: String,
                      val note: String){

}



@Serializable
data class CivNoteInfo(
    private val civ: String,  // this is the civ I will be playing with
    private var notes: MutableMap<String, MutableMap<String, TextOfNotes>> // opponent, language, notes
) {
    fun getCivs(): List<String>{
        return notes.keys.toList()
    }

    fun getNotes(opp: String, lang: String) : TextOfNotes?{
        return notes[opp]?.get(DEFAULT_LANGUAGE)
    }

    fun writeNote(opp: String, lang: String, value: TextOfNotes){
        if (notes.containsKey(opp)){
            val tempMap = notes[opp]!!
            tempMap[DEFAULT_LANGUAGE] = value
            notes[opp] = tempMap
        }
        else {
            val tempMap = mutableMapOf<String, TextOfNotes>(Pair(DEFAULT_LANGUAGE,value))
            notes[opp] = tempMap
        }
    }

}

@Serializable
data class TextOfNotes(
    var myNotes: String,
    var oppNotes: String
)

data class oppNotesModifs(
    val original: String,
    var changed: String
)


val ALLCIVS="ALL"