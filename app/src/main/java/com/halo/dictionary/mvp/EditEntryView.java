package com.halo.dictionary.mvp;

import com.halo.dictionary.mvp.base.ViewBase;

import androidx.annotation.NonNull;

public interface EditEntryView extends ViewBase {

    @NonNull
    Long getId();

    @NonNull
    String getWordText();

    @NonNull
    String getTranslationText();

    void setId(@NonNull final Long id);
    void setWordText(final String text);
    void setTranslationText(final String text);
}
