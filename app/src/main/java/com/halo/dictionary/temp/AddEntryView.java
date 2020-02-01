package com.halo.dictionary.temp;

public interface AddEntryView {

    void focusWordEditView();

    void start();

    void close();

    String getEnteredWord();

    String getEnteredTranslation();

    void showMessage(String text);

}
