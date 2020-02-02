package com.halo.dictionary.mvp;

import android.content.Context;

import androidx.annotation.NonNull;

public interface ViewBase {

    Context getContext();

    void showProgress();

    void stopProgress();

    void close();

    void showMessage(String text);

    void executeOnUiThread(@NonNull final Runnable task);


}
