package com.halo.dictionary.mvp.base;

import androidx.annotation.NonNull;

public interface PresenterBase<T extends ViewBase> {

    /**
     * Attaches view to this presenter.
     *
     * @param view view to attach, not null
     */
    void attachView(@NonNull T view);

    /**
     * Detaches the view from this presenter, if there is one.
     */
    void detachView();

    /**
     * Returns attached view.
     * @return attached view or {@code null}
     */
    T getView();

}
