package com.sun.music61.screen.service;

import com.sun.music61.data.model.Track;

public interface PlayTrackListener {
    void onStateChanged(int state);
    void onSettingChanged();
    void onTrackChanged(Track track);
}
