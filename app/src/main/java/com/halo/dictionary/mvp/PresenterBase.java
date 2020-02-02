package com.halo.dictionary.mvp;

public interface PresenterBase<T> {

    void attachView(T view);

    void detachView();

    T getView();

}
