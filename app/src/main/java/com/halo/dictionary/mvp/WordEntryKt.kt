package com.halo.dictionary.mvp

data class WordEntryKt @JvmOverloads constructor(
    val word: String,
    val translation: String,
    val weight: Int = 0,
    val isArchived: Boolean = false,
    val id: Long? = null,
)