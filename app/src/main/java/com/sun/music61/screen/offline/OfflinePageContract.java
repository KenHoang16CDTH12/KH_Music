package com.sun.music61.screen.offline;

import com.sun.music61.BasePresenter;
import com.sun.music61.BaseView;
import com.sun.music61.data.model.Track;
import java.util.List;

public interface OfflinePageContract {
    interface View extends BaseView<Presenter> {
        void onGetTracksSuccess(List<Track> tracks);
        void onDataTracksNotAvailable();
    }

    interface Presenter extends BasePresenter {
        void loadAllTracks(int type);
    }
}
