package com.sun.music61.data.source.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.sun.music61.data.source.RepositoryCallBack;
import com.sun.music61.data.source.TracksDataSource;

public class TracksRepository implements TracksDataSource.RemoteDataSource,
        TracksDataSource.LocalDataSource {

    @Nullable
    private static TracksRepository sInstance;
    @NonNull
    private final TracksDataSource.RemoteDataSource mTracksRemoteDataSource;
    @NonNull
    private final TracksDataSource.LocalDataSource mTracksLocalDataSource;

    private TracksRepository(@NonNull TracksDataSource.RemoteDataSource tracksRemoteDataSource,
            @NonNull TracksDataSource.LocalDataSource tracksLocalDataSource) {
        mTracksRemoteDataSource = tracksRemoteDataSource;
        mTracksLocalDataSource = tracksLocalDataSource;
    }

    public static TracksRepository getInstance(@NonNull TracksDataSource.RemoteDataSource tracksRemoteDataSource,
            @NonNull TracksDataSource.LocalDataSource tracksLocalDataSource) {
        if (sInstance == null) {
            sInstance = new TracksRepository(tracksRemoteDataSource, tracksLocalDataSource);
        }
        return sInstance;
    }

    public static void destroyInstance() {
        sInstance = null;
    }

    @Override
    public void getBanners(RepositoryCallBack callback) {
        mTracksRemoteDataSource.getBanners(callback);
    }

    @Override
    public void getTracksByGenres(String genres, int offset, RepositoryCallBack callback) {
        mTracksRemoteDataSource.getTracksByGenres(genres, offset, callback);
    }

    @Override
    public void getTracksStorage(int type, RepositoryCallBack callback) {
        mTracksLocalDataSource.getTracksStorage(type, callback);
    }
}
