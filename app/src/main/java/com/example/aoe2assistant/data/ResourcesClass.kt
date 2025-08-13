package com.example.aoe2assistant.data

import android.content.res.Resources
import com.example.aoe2assistant.R

class ResourcesClass {

    private lateinit var _resources: Resources

    fun setResources(res: Resources){
        _resources = res
    }

    fun getResources(): Resources{
        return _resources
    }

    fun recoverText(id: Int): String{
        return _resources.getString(id)
    }

    fun getLocalLanguage(): String {
        return _resources.configuration.locales[0].language
    }

    constructor()
    constructor(res: Resources){
        _resources = res
    }

    private fun speakOptionToIdHeader(option: WhenToSpeak): Int {
        return when(option){
            WhenToSpeak.ONCLICK -> R.string.ONCLICK
            WhenToSpeak.ONBENEFITCHANGE -> R.string.ONBENEFITCHANGE
            WhenToSpeak.ONBOTH -> R.string.ONBOTH
            WhenToSpeak.NEVER -> R.string.NEVER
        }
    }

    fun speakOptionToHeader(option: WhenToSpeak): String {
        return recoverText(speakOptionToIdHeader(option))
    }

    private fun speakOptionToIdExplanation(option: WhenToSpeak): Int {
        return when(option){
            WhenToSpeak.ONCLICK -> R.string.ONCLICKexplanation
            WhenToSpeak.ONBENEFITCHANGE -> R.string.ONBENEFITCHANGEexplanation
            WhenToSpeak.ONBOTH -> R.string.ONBOTHexplanation
            WhenToSpeak.NEVER -> R.string.NEVERexplanation
        }
    }

    fun speakOptionToExplanation(option: WhenToSpeak): String {
        return recoverText(speakOptionToIdExplanation(option))
    }

}