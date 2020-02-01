package com.halo.dictionary.temp;

public interface AddEntryPresenter extends PresenterBase<AddEntryView> {

    void onSaveButtonClicked();

    boolean validateInput(String word, String translation);

}
