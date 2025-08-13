package com.example.aoe2assistant.data

class CivData{
    private var data = hashMapOf<String,HashMap<String,CivInfo>>() // 1st level keys are en, it, es, etc... 2nd level keys are the civ names
    private var keymaptemp = hashMapOf<String,Int>()
    private var keymap = hashMapOf<String,String>()
    private lateinit var rawFields: List<String>

    fun getCiv(lang: String, key: String) : CivInfo?{
        return data[lang]?.get(key)
    }

    fun addCiv(lang: String, key: String, value:CivInfo){
        val hm = data[lang]
        hm?.set(key, value)
        data[lang] = hm?:hashMapOf<String,CivInfo>()

        // preparing ids
        keymaptemp[key] = value.civId
    }

    fun loadKeyMap(){
        var keymaptempen = hashMapOf<Int,String>()
        for (iciv in getCivs("en")){
            keymaptempen[keymaptemp[iciv]?:-1] =iciv
        }
        for (langciv in keymaptemp){
            keymap[langciv.key] = keymaptempen[langciv.value]?:""
        }
    }

    fun getRawFields():List<String>{
        return rawFields
    }
    fun setRawFields(newList: List<String>){
        rawFields = newList
    }

    fun getCivs(lang: String): List<String>{
        return data[lang]?.keys?.toList()?.sorted() ?: listOf()
    }

    fun getKeyCivs(): List<String>{
        return getCivs("en")
    }

    fun getKeyFromCiv(civ: String): String{
        return keymap[civ]?:""
    }

    fun getIdFromCiv(civ: String): Int{
        return keymaptemp[civ]?:-1
    }

    fun getCivsOld(lang:String):List<String>{
        return data.keys.filter { it.contains(lang) }.toList().sorted()
    }
}


data class RawCivInfo(val civ_id: Int,
                      val name: String,
                      val type: String,
                      val icon: String,
                      val economy: String,
                      val military: String,
                      val unique_techs: String,
                      val unique_units: String,
                      val team_bonus: String,
                      val notes: String){

    fun getBonus(key: String) : String{
        if (key.contains("economy", ignoreCase = true)){
            return economy
        }
        else if (key.contains("military", ignoreCase = true)){
            return military
        }
        else if (key.contains("tech", ignoreCase = true)){
            return unique_techs
        }
        else if (key.contains("unit", ignoreCase = true)){
            return unique_units
        }
        else if (key.contains("team", ignoreCase = true)){
            return team_bonus
        }
        else if (key.contains("note", ignoreCase = true)){
            return notes
        }
        else {
            return ""
        }
    }

}
data class CivInfo(val civId: Int,
                   val name: String,
                   val type: String,
                   val icon: String){
    private var bonuses = hashMapOf<String,List<String>>()

    fun getCategories(): List<String>{
        return bonuses.keys.toList()
    }

    fun getBonus(key: String) : List<String>?{
        return bonuses[key]
    }

    fun addBonus(key: String, value: List<String>){
        bonuses[key] = value;
    }
}

data class CivInfoMatchUp(val name: String,
                          val type: String){

    private var bonuses = hashMapOf<String,List<String>>();

    fun getBonus(key: String) : List<String>?{
        return bonuses[key]
    }

    fun setBonus(key: String, value: List<String>){
        bonuses[key] = value;
    }

}

data class CivInfoMatchUp2(val name: String,
                          val type: String,
                          val ecoBonus: List<String>,
                          val miliBonus: List<String>,
                          val uniqueTechs: List<String>,
                          val uniqueUnits: List<String>,
                          val notes: List<String>)

