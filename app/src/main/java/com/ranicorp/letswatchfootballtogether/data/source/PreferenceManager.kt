package com.ranicorp.letswatchfootballtogether.data.source

import android.content.Context

class PreferenceManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(
        "com.ranicorp.letswatchfootballtogether.PREFERENCE_KEY",
        Context.MODE_PRIVATE
    )

    fun putString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String, defValue: String): String {
        return sharedPreferences.getString(key, defValue) ?: defValue
    }
}