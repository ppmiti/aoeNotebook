package com.example.aoe2assistant.data

import androidx.compose.ui.unit.dp
import com.example.aoe2assistant.R
import kotlinx.serialization.Serializable

@Serializable
data class SettingsClass(var langChosen : String,
                         var benefitsList : List<String>,
                         var speak: WhenToSpeak,
                         var ttsSpeed: Float,
                         var voice: String){

    var _categoriesList = mutableListOf<String>()

    fun isEmpty(): Boolean {
        return benefitsList.isEmpty() && langChosen.isEmpty()
    }

    init {
        _categoriesList = benefitsList.toMutableList()
    }

    fun setCategoriesList(categoriesList : MutableList<String>){
        benefitsList = categoriesList
        _categoriesList = categoriesList
    }

    fun getBenefitsIds(benefits : List<String>): List<Int>{
        var output = mutableListOf<Int>()

        for (b in benefits){
            output.add(
                when (b){
                    "economy" -> R.string.economy
                    "military" -> R.string.military
                    "uniqueTechs" -> R.string.uniqueTechs
                    "uniqueUnits" -> R.string.uniqueUnits
                    "generalNote" -> R.string.generalNote
                    "note" -> R.string.note
                    else -> R.string.economy
                }
            )
        }

        return output
    }

    fun getBenefitsIds(): List<Int>{
        var output = mutableListOf<Int>()

        for (b in benefitsList){
            output.add(
                when (b){
                    "economy" -> R.string.economy
                    "military" -> R.string.military
                    "uniqueTechs" -> R.string.uniqueTechs
                    "uniqueUnits" -> R.string.uniqueUnits
                    "generalNote" -> R.string.generalNote
                    "note" -> R.string.note
                    else -> R.string.economy
                }
            )
        }

        return output
    }

    fun getBenefitId(input: String): Int{
        return when (input){
            "economy" -> R.string.economy
            "military" -> R.string.military
            "uniqueTechs" -> R.string.uniqueTechs
            "uniqueUnits" -> R.string.uniqueUnits
            "generalNote" -> R.string.generalNote
            "note" -> R.string.note
            else -> -1
        }
    }

    fun fromBenefitsIds(idList : List<Int>){
        var tempBenefitsList = mutableListOf<String>()
        for (id in idList){
            tempBenefitsList.add(
                when (id){
                    R.string.economy -> "economy"
                    R.string.military -> "military"
                    R.string.uniqueTechs -> "uniqueTechs"
                    R.string.uniqueUnits -> "uniqueUnits"
                    R.string.generalNote -> "generalNote"
                    R.string.note -> "note"
                    else -> "note"
                }
            )
        }
        if (tempBenefitsList.isNotEmpty()) {
            benefitsList = tempBenefitsList
        }
    }

}

val BUTTONCOLUMNWIDTH = 192.dp
val BUTTONCOLUMNWIDTHCIVPAGECIV = 160.dp
val BUTTONCOLUMNWIDTHCIVPAGELANG = 80.dp

enum class WhenToSpeak {
    ONCLICK, ONBENEFITCHANGE, ONBOTH, NEVER
}