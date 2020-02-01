package com.halo.dictionary.temp;

import com.halo.dictionary.WordEntry;

public interface DictionaryRepository {

    WordEntry createWord(String word, String translation);

    WordEntry loadWord();

    Navigator getNavigator();

    void updateWord(WordEntry wordEntry); // TODO сообщать Listener-ам!!

    void deleteWord(String wordId);

    void loadWords();

    void dumpWords();

    void restoreWordsFromDump();

    int getEntriesAmount();

    void registerListener(Listener listener);


    interface Navigator {

        WordEntry getEntryByIndex(int index);

        void refresh();
    }

    interface Listener {
        void onWordsListChanged();
    }

}
