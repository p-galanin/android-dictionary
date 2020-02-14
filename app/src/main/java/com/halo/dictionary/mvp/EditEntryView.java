package com.halo.dictionary.mvp;

import com.halo.dictionary.mvp.base.ViewBase;

import androidx.annotation.NonNull;

public interface EditEntryView extends ViewBase {

    @NonNull
    Long getEntryId();

    @NonNull
    String getWordText();

    @NonNull
    String getTranslationText();

    void setWordText(final String text);
    void setTranslationText(final String text);
}
