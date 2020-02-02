package com.halo.dictionary.repository.sql;

import android.content.Context;
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

import androidx.annotation.NonNull;

public class SqLiteDictionaryRepository implements DictionaryRepository {

    private static final String TAG = "SqLiteRepository";

    private static SqLiteDictionaryRepository INSTANCE;
    private Set<Listener> listeners;
    private WordDbHelper dbHelper;

    @NonNull
    public static SqLiteDictionaryRepository getInstance(@NonNull final Context context) {
        SqLiteDictionaryRepository instance = INSTANCE;
        if (instance == null) {
            synchronized (SqLiteDictionaryRepository.class) {
                if (INSTANCE == null) {
                    instance = new SqLiteDictionaryRepository(context);
                    INSTANCE = instance;
                }
            }
        }
        return INSTANCE;
    }

    private SqLiteDictionaryRepository(@NonNull final Context context) {
        this.dbHelper = WordDbHelper.getInstance(context);
        this.listeners = new LinkedHashSet<>();   
    }

    @Override
    public WordEntry createEntry(final String word, final String translation, final boolean notifyListeners) {
        final WordEntry entry = this.dbHelper.createWordEntry(word, translation);
        if (notifyListeners) {
            this.listeners.forEach(Listener::onWordsListChanged);
        }
        return entry;
    }

    @Override
    public WordEntry loadEntry() {
        // TODO
        return null;
    }

    @Override
    public Navigator createNavigator() {
        return new SqLiteNavigator(this);
    }

    @Override
    public void updateEntry(final WordEntry wordEntry) {
        // TODO
    }

    @Override
    public void deleteEntry(long id) {
        this.dbHelper.removeWordEntry(id);
        this.listeners.forEach(Listener::onWordsListChanged);
    }

    @Override
    public void dump(final String directory, final DumpCallback callback) {

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
            final int columnWord = wordsCursor.getColumnIndex(WordContract.WordEntry.COLUMN_NAME_WORD);
            final int columnTrns = wordsCursor.getColumnIndex(WordContract.WordEntry.COLUMN_NAME_TRANSLATION);

            for (wordsCursor.moveToFirst(); !wordsCursor.isAfterLast(); wordsCursor.moveToNext()) {
                pw.println(DumpFormat.composeStringEntry(wordsCursor.getString(columnWord), wordsCursor.getString(columnTrns)));
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
                if (wordEntry.isPresent()) {
                    // TODO check duplications
                    createEntry(wordEntry.get().getWord(), wordEntry.get().getTranslation(), false);
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

        this.listeners.forEach(Listener::onWordsListChanged);

        if (callback != null) {
            callback.onComplete(restoredCount);
        }
    }

    @Override
    public void registerListener(final Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void unregisterListener(final Listener listener) {
        this.listeners.remove(listener); // TODO may cause concurrent modification?
    }

    @Override
    public void shutdown() {
        this.listeners.clear();
        this.dbHelper.close();
    }

    Cursor getAllEntriesCursor() {
        return this.dbHelper.getAllWordsEntries();
    }
}
