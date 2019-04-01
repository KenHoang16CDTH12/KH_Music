package com.sun.music61.screen.home;

import android.support.annotation.NonNull;
import com.sun.music61.data.model.Track;
import com.sun.music61.data.source.RepositoryCallBack;
import com.sun.music61.data.source.repository.TracksRepository;
import java.util.ArrayList;
import java.util.List;

public class GenresPresenter implements GenresContract.Presenter {

    @NonNull
    private final TracksRepository mRepository;
    @NonNull
    private final GenresContract.View mView;

    public GenresPresenter(@NonNull TracksRepository repository,
            @NonNull GenresContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
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
