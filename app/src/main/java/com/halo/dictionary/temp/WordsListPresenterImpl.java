package com.halo.dictionary.temp;

import com.halo.dictionary.WordEntry;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;

public class WordsListPresenterImpl implements WordsListPresenter, DictionaryRepository.Listener {

    private WordsListView view;

    private Set<Long> visibleTranslations = new HashSet<>();

    public WordsListPresenterImpl() {
        getRepository().registerListener(this);
    }

    @Override
    public DictionaryRepository getRepository() {
        return null;
    }

    @Override
    public WordsListView getView() {
        if (this.view == null) {
            throw new IllegalStateException("No view attached"); // TODO smth more loyal?
        }
        return this.view;
    }

    @Override
    public void onAddWordButtonClicked() {
        getView().goToAddWordScreen();
    }

    @Override
    public int getEntriesAmount() {
        return getRepository().getEntriesAmount();
    }

    @Override
    public void onWordSwiped(String wordId) {
        getRepository().deleteWord(wordId);
    }

    @Override
    public void onClickEntry(final Long entryId) {
        if (!this.visibleTranslations.remove(entryId)) {
            this.visibleTranslations.add(entryId);
        }

    }

    @Override
    public WordEntry getEntryForPosition(final int position) {
        return getEntriesNavigator().getEntryByIndex(position);
    }

    @Override
    public boolean isTranslationVisible(final Long entryId) {
        return this.visibleTranslations.contains(entryId);
    }

    @Override
    public void attachView(final WordsListView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
        //  mDbHelperWords.close(); ?
    }


    @NonNull
    private DictionaryRepository.Navigator getEntriesNavigator() {
        return null;
    }

    @Override
    public void onWordsListChanged() {
        getEntriesNavigator().refresh();
        getView().refreshList();
    }
}
