package com.halo.dictionary.repository.sql;

import android.provider.BaseColumns;

/**
 * Контракт для работы с БД
 * Created by halo on 16.09.2017.
 */

public class WordContract {

    private WordContract() {}

    public static class WordEntry implements BaseColumns {

        public static final String TABLE_NAME = "words";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_TRANSLATION = "translation";

    }

}