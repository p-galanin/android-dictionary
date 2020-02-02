package com.halo.dictionary.mvp;

import android.content.Context;
import android.net.Uri;

import java.util.Optional;

import androidx.annotation.NonNull;

public interface WordsListPresenter extends PresenterBase<WordsListView> {

    void onAddWordButtonClicked();

    void onWordSwiped(String wordId);

    void onClickEntry(Long entryId);

    boolean isTranslationVisible(Long entryId);

    int getEntriesAmount();

    Optional<WordEntry> getEntryForPosition(int position);

    void onDumpFilePicked(@NonNull final Context context, @NonNull final Uri uri);

    void onDumpDirectoryPicked(final Uri uri);

    void onRestoreFromDumpClicked();

    void onDumpToFileClicked();

    void onFinish();
}
