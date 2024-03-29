package com.halo.dictionary.repository.impl.sql;

import android.database.Cursor;
import android.util.Log;

import com.halo.dictionary.mvp.WordEntry;
import com.halo.dictionary.repository.DictionaryRepository;
import com.halo.dictionary.repository.dump.DumpCallback;
import com.halo.dictionary.repository.dump.DumpFormat;
import com.halo.dictionary.repository.dump.RestoreFromDumpCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Singleton
public class SqLiteDictionaryRepository implements DictionaryRepository {

    private static final String TAG = "SqLiteRepository";
    private final Set<Listener> listeners;
    private WordDbHelper dbHelper;

    @Inject
    public SqLiteDictionaryRepository(WordDbHelper wordDbHelper) {
        this.dbHelper = wordDbHelper;
        this.listeners = new LinkedHashSet<>();   
    }

    @Override
    public WordEntry saveEntry(@NonNull final String word, final String translation, final boolean notifyListeners) {
        final WordEntry entry = this.dbHelper.saveWordEntry(new WordEntry(word, translation));
        if (notifyListeners) {
            this.listeners.forEach(Listener::onEntriesListChanged);
        }
        return entry;
    }

    @Override
    public WordEntry saveEntry(
            @NonNull final String word,
            final String translation,
            final boolean isArchived,
            final int weight,
            final boolean notifyListeners) {
        final WordEntry entry = this.dbHelper.saveWordEntry(new WordEntry(word, translation, weight, isArchived));
        if (notifyListeners) {
            this.listeners.forEach(Listener::onEntriesListChanged);
        }
        return entry;
    }

    @Override
    public Optional<WordEntry> loadEntryWithWord(@NonNull final String word) {
        return this.dbHelper.getWordEntryByWord(word);
    }

    @Override
    public Optional<WordEntry> loadEntry(@NonNull final Long entryId) {
        return this.dbHelper.getEntryById(entryId);
    }

    @Override
    public void updateEntry(final WordEntry wordEntry) {
        if (this.dbHelper.update(wordEntry)) {
            this.listeners.forEach(Listener::onEntriesListChanged);
        }
    }

    @Override
    public void deleteEntry(long id) {
        this.dbHelper.removeWordEntry(id);
        this.listeners.forEach(Listener::onEntriesListChanged);
    }

    @Override
    public void dump(@NonNull final String directory, @NonNull final DumpCallback callback) {

        final File file = new File(directory, "dump" + System.currentTimeMillis() + ".txt");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    callback.onError("Unable to create file " + file.getName());
                    return;
                }
            } catch (IOException e) {
                callback.onError("Unable to create file " + file.getName() + ": " + e.getMessage());
                return;
            }
        }


        try (final PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {

            final Cursor wordsCursor = dbHelper.getAllWordsEntries();
            final int columnWord = wordsCursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_WORD);
            final int columnTrns = wordsCursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_TRANSLATION);
            final int columnIsArchived = wordsCursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_IS_ARCHIVED);
            final int columnWeight = wordsCursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_WEIGHT);

            for (wordsCursor.moveToFirst(); !wordsCursor.isAfterLast(); wordsCursor.moveToNext()) {
                pw.println(DumpFormat.serializeEntry(
                        wordsCursor.getString(columnWord),
                        wordsCursor.getString(columnTrns),
                        wordsCursor.getInt(columnWeight),
                        wordsCursor.getInt(columnIsArchived) == 1
                ));
            }

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            callback.onError("Unable to dump: " + e.getMessage());
        }


        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        callback.onComplete();
    }

    @Override
    public void restoreFromDump(@NonNull final InputStream inputStream, final RestoreFromDumpCallback callback) {

        int overallCount = 0;
        int restoredCount = 0;

        try (final Scanner scanner = new Scanner(inputStream)) {
            while (scanner.hasNextLine()) {
                final Optional<WordEntry> wordEntry = DumpFormat.parseEntry(scanner.nextLine());
                if (wordEntry.isPresent() && !loadEntryWithWord(wordEntry.get().getWord()).isPresent()) {
                    saveEntry(wordEntry.get().getWord(), wordEntry.get().getTranslation(), false);
                    restoredCount++;
                }
                overallCount++;
            }
        }

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Overall: " + overallCount + " Restored: " + restoredCount);
        }

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.listeners.forEach(Listener::onEntriesListChanged);

        if (callback != null) {
            callback.onComplete(restoredCount);
        }
    }

    @Override
    public void registerListener(@NonNull final Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void unregisterListener(@NonNull final Listener listener) {
        this.listeners.remove(listener); // TODO may cause concurrent modification?
    }

    Cursor getAllEntriesCursor() {
        return this.dbHelper.getAllWordsEntries();
    }

    Cursor getNotArchivedEntriesCursor() {
        return this.dbHelper.getNotArchivedWordsEntries();
    }
}
