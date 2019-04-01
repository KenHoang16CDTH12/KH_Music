package com.sun.music61;

public interface BaseView<T> {
    void setPresenter(T presenter);
    void showErrors(String message);
}
