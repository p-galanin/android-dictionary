package com.halo.dictionary.repository.impl

interface PreferenceStorage {
    fun saveInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Int
}