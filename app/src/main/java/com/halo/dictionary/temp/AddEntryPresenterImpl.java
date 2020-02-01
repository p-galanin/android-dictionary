package com.halo.dictionary.temp;

public class AddEntryPresenterImpl implements AddEntryPresenter {

    private AddEntryView view;

    @Override
    public void onSaveButtonClicked() {
        final String word = getView().getEnteredWord();
        final String translation = getView().getEnteredTranslation();

        if (validateInput(word, translation)) {
            getRepository().createWord(word, translation);
            getView().showMessage("Word added");
            getView().close();
        }
    }

    @Override
    public void attachView(final AddEntryView view) {
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
        } else if (translation == null || translation.isEmpty()) {
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

    @Override
    public DictionaryRepository getRepository() {
        return null;
    }
}
