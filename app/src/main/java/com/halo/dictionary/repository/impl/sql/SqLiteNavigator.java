package com.halo.dictionary.repository.impl.sql;

import android.database.Cursor;

import com.halo.dictionary.mvp.WordEntry;
import com.halo.dictionary.repository.DictionaryRepository;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.NonNull;

public class SqLiteNavigator implements DictionaryRepository.Navigator {

    private final Provider<Cursor> cursorProvider;
    private Cursor cursor;

    @Inject
    SqLiteNavigator(@NonNull final Provider<Cursor> cursorProvider) {
        this.cursorProvider = cursorProvider;
        this.cursor = cursorProvider.get();
    }

    @Override
    public int getEntriesAmount() {
        return this.cursor.getCount();
    }

    @Override
    public Optional<WordEntry> getEntryByIndex(final int index) {
        if (!this.cursor.moveToPosition(index)) {
            return Optional.empty();
        }

        final String word = this.cursor.getString(this.cursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_WORD));
        final String translation = this.cursor.getString(this.cursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_TRANSLATION));
        final long id = this.cursor.getLong(this.cursor.getColumnIndex(WordContract.Entry._ID));
        final boolean isArchived = this.cursor.getInt(this.cursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_IS_ARCHIVED)) == 1;
        final int weight = this.cursor.getInt(this.cursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_WEIGHT));
        // todo one time from db
        return Optional.of(new WordEntry(word, translation, weight, isArchived, id));
    }

    @Override
    public void refresh() {
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = this.cursorProvider.get();
    }
}
