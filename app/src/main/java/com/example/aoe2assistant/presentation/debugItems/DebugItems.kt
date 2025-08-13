package com.example.aoe2assistant.presentation.debugItems

import android.content.Context
import android.widget.Toast

class DebugItems(val debugging: Boolean) {

    private lateinit var _context: Context

    fun setContext(context: Context){
        _context = context
    }

    fun toast(msg: String, length: Int){
        if (!debugging) return
        Toast.makeText(_context, msg, length).show()
    }

}