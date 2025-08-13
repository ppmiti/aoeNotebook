package com.example.aoe2assistant.data

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.text.toLowerCase
import androidx.core.content.res.ResourcesCompat
import com.example.aoe2assistant.R

class IconsData {

    //private var _iconsList: List<NamedDrawable>
    private var _iconsMap: Map<String, Int>

    constructor(){
        _iconsMap = mapOf()
    }

    constructor(resources: Resources, civList: List<String>) {
        _iconsMap = getAllDrawables(resources, civList)
    }

    private fun getAllDrawables(resources: Resources, civList: List<String>): Map<String, Int> {
        val drawables = R.drawable::class.java.fields.mapNotNull {
                field ->
                ResourcesCompat.getDrawable(resources, field.getInt(null), null)
                    ?.let { NamedDrawabl(field.name, field.getInt(null)) }
        }

        var temp = drawables.mapNotNull { it.takeIf { it.name.contains("civ")}?.apply { it.name = getCivName(it.name) } }

        var output = mutableMapOf<String, Int>()

        for (icon in temp){
            output[icon.name] = icon.drawable
        }

        return output
    }

    fun getIcon(civ: String) : Int? {
        return _iconsMap[civ.lowercase()]
    }

    private fun getCivName(iconName: String): String{
        return iconName.replaceAfter('_',"").replace("_","")
    }

}

// drawables list

class NamedDrawable(var name: String, val drawable: Drawable) {
    override fun toString(): String = name
}

class NamedDrawabl(var name: String, val drawable: Int) {
    override fun toString(): String = name
}


