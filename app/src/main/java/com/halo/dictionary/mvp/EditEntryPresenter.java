package com.halo.dictionary.mvp;

import com.halo.dictionary.mvp.base.PresenterBase;

import androidx.annotation.NonNull;

public interface EditEntryPresenter extends PresenterBase<EditEntryView> {

    void onViewInitialized(@NonNull Long editingEntryId);

    void onSaveButtonClicked();

}
