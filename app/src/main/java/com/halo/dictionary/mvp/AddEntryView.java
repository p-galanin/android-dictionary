package com.halo.dictionary.mvp;

import com.halo.dictionary.mvp.base.ViewBase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * View to add new entry.
 */
public interface AddEntryView extends ViewBase {

    /**
     * Returns entered word value.
     * @return entered word value, may be null (hypothetically)
     */
    @NonNull
    String getEnteredWord();

    /**
     * Returns entered translation value.
     * @return entered translation value, may be null (hypothetically)
     */
    @Nullable
    String getEnteredTranslation();

}
