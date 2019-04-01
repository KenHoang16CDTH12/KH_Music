package com.sun.music61.screen.home;

import android.support.annotation.NonNull;
import com.sun.music61.data.source.repository.TracksRepository;

public class HomePresenter implements HomeContract.Presenter {

    @NonNull
    private final TracksRepository mRepository;
    @NonNull
    private HomeContract.View mView;

    public HomePresenter(@NonNull TracksRepository repository,
            @NonNull HomeContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }
}
