package com.sun.music61.screen.home;

import android.support.annotation.NonNull;
import com.sun.music61.data.model.Track;
import com.sun.music61.data.source.RepositoryCallBack;
import com.sun.music61.data.source.repository.TracksRepository;
import java.util.ArrayList;
import java.util.List;

public class AllSongPresenter implements AllSongsContract.Presenter {

    @NonNull
    private final TracksRepository mRepository;
    @NonNull
    private final AllSongsContract.View mView;

    public AllSongPresenter(@NonNull TracksRepository repository,
            @NonNull AllSongsContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void loadAllBanners() {
        mRepository.getBanners(new RepositoryCallBack() {
            @Override
            public <T> void onSuccess(List<T> objects) {
                if (!objects.isEmpty()) mView.onGetBannersSuccess((ArrayList<Track>) objects);
                else mView.onDataBannersNotAvailable();
            }

            @Override
            public void onFailed(Throwable throwable) {
                mView.showErrors(throwable.getMessage());
            }
        });
    }

    @Override
    public void loadAllTracks(String genres, int offset) {
        mRepository.getTracksByGenres(genres, offset,
                new RepositoryCallBack() {
                    @Override
                    public <T> void onSuccess(List<T> objects) {
                        if (!objects.isEmpty()) {
                            mView.onGetTracksSuccess((ArrayList<Track>) objects);
                        } else {
                            mView.onDataTracksNotAvailable();
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        mView.showErrors(throwable.getMessage());
                    }
                });
    }
}
