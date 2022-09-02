package com.halo.dictionary.repository.impl

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesStorageImpl @Inject constructor(
    @ApplicationContext context: Context
) : PreferencesStorage {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);

    override fun saveInt(key: String, value: Int) {
        with(prefs.edit()) {
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