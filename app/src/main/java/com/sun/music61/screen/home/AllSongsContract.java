package com.sun.music61.screen.home;

import com.sun.music61.BasePresenter;
import com.sun.music61.BaseView;
import com.sun.music61.data.model.Track;
import java.util.List;

public interface AllSongsContract {

    interface View extends BaseView<Presenter> {
        void onGetBannersSuccess(List<Track> banners);
        void onDataBannersNotAvailable();
        void onGetTracksSuccess(List<Track> tracks);
        void onDataTracksNotAvailable();
    }

    interface Presenter extends BasePresenter {
        void loadAllBanners();
        void loadAllTracks(String genres, int offset);
    }
}
