package com.halo.dictionary.mvp;

import androidx.annotation.NonNull;

/**
 * Запись о слове
 *
 * Created by halo on 29.10.2017.
 */

public class WordEntry {

    private final long id;
    private final String word;
    private final String translation;

    public WordEntry(@NonNull final String word, final String translation, long id) {
        this.word = word;
        this.translation = translation == null ? "" : translation;
        this.id = id;
    }

    /**
     * Возвращает идентификатор записи в БД.
     *
     * @return идентификатор записи в БД
     */
    public long getId() {
        return this.id;
    }

    @NonNull
    public String getWord() {
        return this.word;
    }

    @NonNull
    public String getTranslation() {
        return this.translation;
    }
}
