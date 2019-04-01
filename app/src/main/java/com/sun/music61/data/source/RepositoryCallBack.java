package com.sun.music61.data.source;

import java.util.List;

public interface RepositoryCallBack {
    <T> void onSuccess(List<T> objects);
    void onFailed(Throwable throwable);
}
