package com.sun.music61.data.source.local;

import com.sun.music61.data.model.Track;
import java.util.List;

public interface TracksLocalCallback {
    void onSuccess(List<Track> tracks);
    void onFailure(Throwable throwable);
}
