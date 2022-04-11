package com.template

import android.content.Context
import android.content.SharedPreferences

object PrefManager {
    val prefs = App.applicationContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)

    fun getLoading(): Boolean {
        return prefs.getBoolean("loading", false)
    }
    fun setLoading(loading: Boolean){
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putBoolean("loading", loading)
        editor.apply()
    }

    fun getHost(): String? {
        return prefs.getString("host", null)
    }
    fun setHost(host: String){
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("host", host)
        editor.apply()
    }
}