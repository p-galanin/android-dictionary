package com.halo.dictionary.repository.impl

import android.content.Context
import android.content.SharedPreferences

class PreferenceStorageImpl(context: Context) : PreferenceStorage {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);

    override fun saveInt(key: String, value: Int) {
        with (prefs.edit()) {
            putInt(key, value)
            apply()
        }
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    companion object {
        private const val PREF_FILE_NAME = "dictionary_prefs"
    }
}