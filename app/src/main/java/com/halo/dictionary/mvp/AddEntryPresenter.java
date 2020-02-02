package com.halo.dictionary.mvp;

public interface AddEntryPresenter extends PresenterBase<AddEntryView> {

    void onSaveButtonClicked();

    boolean validateInput(String word, String translation);

}
