package com.sun.music61.util.listener;

import com.sun.music61.data.model.Response;
import com.sun.music61.data.model.Track;
import java.util.List;

public interface APICallback {
    void onResponse(Response response);
    void onFailure(Throwable throwable);
}
