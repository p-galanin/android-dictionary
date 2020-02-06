package com.halo.dictionary.repository;

import com.halo.dictionary.mvp.WordEntry;
import com.halo.dictionary.repository.dump.DumpCallback;
import com.halo.dictionary.repository.dump.RestoreFromDumpCallback;

import java.io.InputStream;
import java.util.Optional;

import androidx.annotation.NonNull;

/**
 * Main repository interface.
 */
public interface DictionaryRepository {

    /**
     * Creates and persists entry with specified values.
     *
     * @param word word, not null
     * @param translation translation, nullable
     * @param notifyListeners whether notify the listeners of data changes
     * @return created entry or {@code null} if entry was not created
     */
    WordEntry createEntry(@NonNull String word, String translation, boolean notifyListeners);

    /**
     * Loads entry by it's word value.
     *
     * @param word word value, not null
     * @return first found word entry with such word value or empty object, if there is no entries with such value
     */
    Optional<WordEntry> loadEntryByWord(@NonNull final String word);

    /**
     * Creates an instance of entries navigator, connected to this repository.
     * @return created navigator, not null
     */
    @NonNull
    Navigator createNavigator();

    /**
     * Deletes an entry with specified ID.
     * @param id entry ID to delete
     */
    void deleteEntry(long id);

    /**
     * Dumps all entries to file.
     *
     * @param directory directory where dump file will be created, not null
     * @param callback dump process callback, not null
     */
    void dump(@NonNull final String directory, @NonNull final DumpCallback callback);

    /**
     * Restore entries from the dump stream.
     *
     * @param inputStream dump stream, not null
     * @param callback restore process callback
     */
    void restoreFromDump(@NonNull final InputStream inputStream, final RestoreFromDumpCallback callback);

    /**
     * Registers listener to data updates.
     *
     * @param listener listener, not null
     */
    void registerListener(@NonNull Listener listener);

    /**
     * Unregisters listener.
     *
     * @param listener listener, not null
     */
    void unregisterListener(@NonNull Listener listener);

    /**
     * Close this repository.
     * For resource cleaning purposes.
     */
    void shutdown();

    WordEntry loadEntry();

    void updateEntry(WordEntry wordEntry);


    /**
     * Entries navigator.
     * Holds some methods for communication with data set.
     */
    interface Navigator {

        /**
         * Returns the current amount of entries.
         * @return current amount of entries
         */
        int getEntriesAmount();

        /**
         * Returns the entry from the specified index.
         *
         * @param index specified index, positive
         * @return entry from the specified position or empty object, if this position is empty
         */
        Optional<WordEntry> getEntryByIndex(int index);

        /**
         * Forces the data set (entries list) refresh.
         */
        void refresh();
    }

    /**
     * Entries list changes listener.
     *
     * @see DictionaryRepository#registerListener(Listener)
     * @see DictionaryRepository#unregisterListener(Listener)
     */
    interface Listener {
        void onEntriesListChanged();
    }

}
