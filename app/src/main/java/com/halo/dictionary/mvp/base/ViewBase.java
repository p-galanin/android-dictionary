package com.halo.dictionary.mvp.base;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Interface with common methods for views
 */
public interface ViewBase {

    /**
     * Returns context to perform actions requiring it.
     * @return context, not null
     */
    @NonNull
    Context getViewContext();

    /**
     * Displays progress state.
     */
    default void showProgress() { }

    /**
     * Stops displaying progress state.
     */
    default void stopProgress() { }

    /**
     * Shows message to user.
     *
     * @param text message, not null
     */
    void showMessage(@NonNull String text);

    /**
     * Executes task on UI-thread.
     *
     * @param task task, not-null
     */
    void executeOnUiThread(@NonNull final Runnable task);

    /**
     * Closes this view.
     */
    void close();


}
