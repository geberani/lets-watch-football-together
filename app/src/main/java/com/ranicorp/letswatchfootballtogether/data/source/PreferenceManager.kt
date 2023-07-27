package com.ranicorp.letswatchfootballtogether.data.source

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {

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

    fun clearPreferences() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}