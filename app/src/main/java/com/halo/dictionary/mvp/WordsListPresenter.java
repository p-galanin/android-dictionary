package com.halo.dictionary.mvp;

import android.content.Context;
import android.net.Uri;

import com.halo.dictionary.mvp.base.PresenterBase;

import java.util.Optional;

import androidx.annotation.NonNull;

/**
 * Presenter of entries list and corresponding interactions.
 */
public interface WordsListPresenter extends PresenterBase<WordsListView> {

    /**
     * Performs actions on view initialization finished.
     */
    void onViewInitialized();

    /**
     * Performs actions on add word button clicked.
     */
    void onAddWordButtonClicked();

    /**
     * Performs actions on swiping entry in list.
     * @param wordId swiped entry id, not-null
     */
    void onWordSwiped(@NonNull Long wordId);

    /**
     * Performs actions on long press on entry in list.
     * @param entryId chosen entry id
     */
    void onWordLongPressed(@NonNull Long entryId);

    /**
     * Performs actions on touching some entry.
     * @param entryId touched entry id
     */
    void onClickEntry(Long entryId);

    /**
     * Performs actions on restore from dump option chosen.
     */
    void onRestoreFromDumpClicked();

    /**
     * Performs actions on dump option chosen.
     */
    void onDumpToFileClicked();

    /**
     * Performs actions on disable notifications option chosen.
     */
    void onDisableNotificationsClicked();

    /**
     * Performs restoring after dump file picked.
     *
     * @param context context, not-null
     * @param uri dump file URI, not-null
     */
    void onDumpFilePicked(@NonNull final Context context, @NonNull final Uri uri);

    /**
     * Performs dump after directory picked.
     * @param uri directory URI, not-null
     */
    void onDumpDirectoryPicked(final Uri uri);

    /**
     * Returns the current amount of entries.
     * @return current amount of entries
     */
    int getEntriesAmount();

    /**
     * Returns the entry from specified position.
     *
     * @param position specified position, positive
     * @return entry from specified position or empty object, if this position is empty
     */
    Optional<WordEntryKt> getEntryForPosition(int position);

    /**
     * Determines whether an entry translation should be displayed or hidden.
     * @param entryId entry ID
     * @return {@code true} if the translation for this entry should be displayed; {@code false} otherwise
     */
    boolean isTranslationVisible(Long entryId);


    void onArchiveClicked(@NonNull Long wordId);
}
