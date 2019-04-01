package com.sun.music61.screen.play;

import android.support.annotation.NonNull;
import com.sun.music61.data.source.repository.TracksRepository;

public class PlayPresenter implements PlayContract.Presenter {

    @NonNull
    private final TracksRepository mTracksRepository;
    @NonNull
    private final PlayContract.View mView;

    public PlayPresenter(@NonNull TracksRepository repository,
                         @NonNull PlayContract.View view) {
        mTracksRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }
}
