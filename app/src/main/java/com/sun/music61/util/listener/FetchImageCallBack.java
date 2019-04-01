package com.sun.music61.util.listener;

import android.graphics.Bitmap;

public interface FetchImageCallBack {
    void onCompleted(Bitmap bitmap);
    void onFailure();
}
