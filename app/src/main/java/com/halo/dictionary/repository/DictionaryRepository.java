package com.halo.dictionary.repository;

import com.halo.dictionary.mvp.WordEntry;
import com.halo.dictionary.repository.dump.DumpCallback;
import com.halo.dictionary.repository.dump.RestoreFromDumpCallback;

import java.io.InputStream;
import java.util.Optional;

public interface DictionaryRepository {

    WordEntry createEntry(String word, String translation, boolean notifyListeners);

    WordEntry loadEntry();

    Navigator createNavigator();

    void updateEntry(WordEntry wordEntry); // TODO сообщать Listener-ам!!

    void deleteEntry(long id);

    void dump(final String directory, final DumpCallback callback);

    void restoreFromDump(final InputStream inputStream, final RestoreFromDumpCallback callback);

    void registerListener(Listener listener);

    void unregisterListener(Listener listener);

    void shutdown();


    interface Navigator {

        int getEntriesAmount();

        Optional<WordEntry> getEntryByIndex(int index);

        void refresh();
    }

    interface Listener {
        void onWordsListChanged();
    }

}
