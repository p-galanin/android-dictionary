package com.halo.dictionary.repository.dump;

public interface RestoreFromDumpCallback {

    void onComplete(final int restoredAmount);

    void onError(final String errorText);

}
