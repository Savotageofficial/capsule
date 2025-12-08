package com.example.capsule.data.repository

import android.content.Context

class Prefs(context: Context) {

    private val prefs = context.getSharedPreferences("chat_history", Context.MODE_PRIVATE)

    fun saveString(name: String , Item: String) {
        prefs.edit().putString(name, Item).apply()
    }

    fun getString(name: String): String {
        return prefs.getString(name, "") ?: ""
    }
}
