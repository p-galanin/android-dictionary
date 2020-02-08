package com.halo.dictionary.repository.sql;

import android.database.Cursor;

import com.halo.dictionary.mvp.WordEntry;
import com.halo.dictionary.repository.DictionaryRepository;

import java.util.Optional;

import androidx.annotation.NonNull;

public class SqLiteNavigator implements DictionaryRepository.Navigator {

    private Cursor cursor;
    final private SqLiteDictionaryRepository repository;

    SqLiteNavigator(@NonNull final SqLiteDictionaryRepository repository) {
        this.repository = repository;
        this.cursor = this.repository.getAllEntriesCursor();
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
        return Optional.of(new WordEntry(word, translation, id));
    }

    @Override
    public void refresh() {
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = this.repository.getAllEntriesCursor();
    }

}
