package com.halo.dictionary.mvp;

public interface AddEntryView extends ViewBase {

    void focusWordEditView();

    String getEnteredWord();

    String getEnteredTranslation();

}
