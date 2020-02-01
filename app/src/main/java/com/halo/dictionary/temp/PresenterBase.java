package com.halo.dictionary.temp;

public interface PresenterBase<T> {

    void attachView(T view);

    void detachView();

    T getView();

    DictionaryRepository getRepository();

}
