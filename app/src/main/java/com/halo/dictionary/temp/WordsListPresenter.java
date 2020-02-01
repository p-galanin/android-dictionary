package com.halo.dictionary.temp;

import com.halo.dictionary.WordEntry;

public interface WordsListPresenter extends PresenterBase<WordsListView> {

    void onAddWordButtonClicked();

    void onWordSwiped(String wordId);

    void onClickEntry(Long entryId);

    boolean isTranslationVisible(Long entryId);

    int getEntriesAmount();
    WordEntry getEntryForPosition(int position);
}
