package com.sun.music61.screen.offline;

import android.support.annotation.NonNull;
import com.sun.music61.data.model.Track;
import com.sun.music61.data.source.RepositoryCallBack;
import com.sun.music61.data.source.repository.TracksRepository;
import java.util.ArrayList;
import java.util.List;

public class OfflinePagePresenter implements OfflinePageContract.Presenter {

    @NonNull
    private final TracksRepository mRepository;
    @NonNull
    private final OfflinePageContract.View mView;

    public OfflinePagePresenter(@NonNull TracksRepository repository,
            @NonNull OfflinePageContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void loadAllTracks(int type) {
        mRepository.getTracksStorage(type, new RepositoryCallBack() {
            @Override
            public <T> void onSuccess(List<T> objects) {
                mView.onGetTracksSuccess((ArrayList<Track>) objects);
            }

            @Override
            public void onFailed(Throwable throwable) {
                mView.onDataTracksNotAvailable();
            }
        });
    }
}
