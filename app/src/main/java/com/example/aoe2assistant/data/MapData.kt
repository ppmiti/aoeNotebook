package com.example.aoe2assistant.data

class MapData{
    private var data = hashMapOf<String,MapInfo>()
    private val rawFields = listOf<String>("highlights", "notes")

    fun getMap(key: String) : MapInfo?{
        return data[key]
    }

    fun addMap(key: String, value:MapInfo){
        data[key] = value;
    }

    fun getRawFields():List<String>{
        return rawFields
    }

    fun getMaps():List<String>{
        return data.keys.toList().sorted()
    }
}

data class MapInfoMatchUp(val name: String,
                   val type: String) {

    private var bonuses = hashMapOf<String, List<String>>();

    fun getBonus(key: String): List<String>? {
        return bonuses[key]
    }

    fun setBonus(key: String, value: List<String>) {
        bonuses[key] = value;
    }
}


data class MapInfo(val mapId: Int,
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