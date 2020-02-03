package com.halo.dictionary.mvp;

import androidx.annotation.NonNull;

/**
 * Запись о слове.
 *
 * Created by halo on 29.10.2017.
 */

public class WordEntry {

    private final long id;
    private final String word;
    private final String translation;

    /**
     * Constructor for creation of word entry.
     *
     * @param word word, not null
     * @param translation translation, if {@code null} an empty string will be used
     * @param id entry id; specify -1 for non-persisted entry
     */
    public WordEntry(@NonNull final String word, final String translation, long id) {
        this.word = word;
        this.translation = translation == null ? "" : translation;
        this.id = id;
    }

    /**
     * Возвращает идентификатор записи в БД.
     *
     * @return идентификатор записи в БД или -1, если запись не сохранена в БД
     */
    public long getId() {
        return this.id;
    }

    /**
     * Returns word.
     *
     * @return word, not null
     */
    @NonNull
    public String getWord() {
        return this.word;
    }

    /**
     * Returns translation.
     *
     * @return translation, not null
     */
    @NonNull
    public String getTranslation() {
        return this.translation;
    }
}
