package com.halo.dictionary.mvp;

import com.halo.dictionary.mvp.base.ViewBase;

/**
 * Entries list view (main view).
 */
public interface WordsListView extends ViewBase {

    /**
     * Updates the list of entries.
     */
    void refreshList();

    /**
     * Navigates to words adding screen.
     */
    void goToAddWordScreen();

    /**
     * Navigates to file picker.
     */
    void goToChooseFile();

    /**
     * Navigates to directory picker.
     */
    void goToChooseDirectory();

    void goToEditEntryScreen(Long entryId);

}
