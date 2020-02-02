package com.halo.dictionary.repository.dump;

public interface DumpCallback {

    void onComplete();

    void onError(final String errorText);

}
