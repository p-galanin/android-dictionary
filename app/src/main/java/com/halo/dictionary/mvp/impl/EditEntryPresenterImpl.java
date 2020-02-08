package com.halo.dictionary.mvp.impl;

import com.halo.dictionary.mvp.EditEntryPresenter;
import com.halo.dictionary.mvp.EditEntryView;
import com.halo.dictionary.mvp.WordEntry;
import com.halo.dictionary.repository.DictionaryRepository;
import com.halo.dictionary.repository.DictionaryRepositoryFactory;

import androidx.annotation.NonNull;

class EditEntryPresenterImpl implements EditEntryPresenter {

    private EditEntryView view;
    private DictionaryRepository repository;
    
    public EditEntryPresenterImpl(@NonNull final EditEntryView view) {
        attachView(view);
        this.repository = DictionaryRepositoryFactory.createDictionaryRepository(view);
    }
    
    @Override
    public void onViewInitialized(@NonNull final Long editingEntryId) {
        this.repository.loadEntry(editingEntryId).ifPresent(wordEntry -> {
            getView().setId(wordEntry.getId());
            getView().setWordText(wordEntry.getWord());
            getView().setTranslationText(wordEntry.getTranslation());
        });
    }

    @Override
    public void onSaveButtonClicked() {
        this.repository.updateEntry(new WordEntry(getView().getWordText(), getView().getTranslationText(), getView().getId()));
        getView().close();
    }
    
    @Override
    public void attachView(@NonNull final EditEntryView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public EditEntryView getView() {
        if (this.view == null) {
            throw new IllegalStateException("No view attached");    
        }
        return this.view;
    }
}
