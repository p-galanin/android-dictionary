package com.halo.dictionary.mvp;

import com.halo.dictionary.mvp.ViewBase;

public interface WordsListView extends ViewBase {

    void goToAddWordScreen();

    void goToChooseFile();

    void goToChooseDirectory();

    void refreshList();

}
