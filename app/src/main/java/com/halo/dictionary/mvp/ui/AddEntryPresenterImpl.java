package com.halo.dictionary.mvp.ui;

import com.halo.dictionary.mvp.AddEntryPresenter;
import com.halo.dictionary.mvp.AddEntryView;
import com.halo.dictionary.repository.DictionaryRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class AddEntryPresenterImpl implements AddEntryPresenter {

    private AddEntryView view;
    private final DictionaryRepository repository;

    @Inject
    public AddEntryPresenterImpl(
            @NonNull final AddEntryView view,
            @NonNull DictionaryRepository repository
    ) {
        attachView(view);
        this.repository = repository;
    }

    @Override
    public void onSaveButtonClicked() {
        final String word = getView().getEnteredWord();
        final String translation = getView().getEnteredTranslation();

        if (validateInput(word, translation)) {
            this.repository.saveEntry(word, translation, true);
            getView().showMessage("Word added");
            getView().close();
        }
    }

    @Override
    public void attachView(@NonNull final AddEntryView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public boolean validateInput(final String word, final String translation) {
        final boolean isCorrect;
        if (word == null || word.isEmpty()) {
            getView().showMessage("Enter word value");
            isCorrect = false;
        } else if (translation == null) {
            getView().showMessage("Enter translation value");
            isCorrect = false;
        } else {
            isCorrect = true;
        }
        return isCorrect;
    }

    @Override
    public AddEntryView getView() {
        if (this.view == null) {
            throw new IllegalStateException("No view attached");
        }
        return this.view;
    }

}
