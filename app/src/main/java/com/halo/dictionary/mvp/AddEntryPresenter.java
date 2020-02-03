package com.halo.dictionary.mvp;

import com.halo.dictionary.mvp.base.PresenterBase;

/**
 * Presenter of adding new entries.
 */
public interface AddEntryPresenter extends PresenterBase<AddEntryView> {

    /**
     * Performs actions on save button clicked.
     */
    void onSaveButtonClicked();

    /**
     * Validates new entry input.
     * May send messages to view during validation.
     *
     * @param word word value
     * @param translation translation value
     * @return {@code true} if the values are correct; {@code false} otherwise
     */
    boolean validateInput(String word, String translation);

}
