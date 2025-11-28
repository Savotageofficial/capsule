package com.example.capsule.data.repository

import android.content.Context

class Prefs(context: Context) {

    private val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    fun saveString(name: String , Item: String) {
        prefs.edit().putString(name, Item).apply()
    }

    fun getString(name: String): String {
        return prefs.getString(name, "") ?: ""
    }
}
