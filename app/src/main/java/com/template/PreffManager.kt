package com.template

import android.content.Context

object PrefManager {
    private const val URL_KEY= "URL_KEY"
    private val prefs = App.applicationContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)


    fun getUrl(): String? {
        return prefs.getString(URL_KEY, null)
    }
    fun setUrl(url: String){
        prefs.edit().apply {
            putString(URL_KEY, url)
            apply()
        }
    }
}