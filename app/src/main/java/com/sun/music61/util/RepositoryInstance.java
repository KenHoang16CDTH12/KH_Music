package com.sun.music61.util;

import android.content.Context;
import com.sun.music61.data.source.local.TracksLocalDataSource;
import com.sun.music61.data.source.remote.TracksRemoteDataSource;
import com.sun.music61.data.source.repository.TracksRepository;

public class RepositoryInstance {

    public static TracksRepository getInstanceTrackRepository(Context context) {
        return TracksRepository.getInstance(
                TracksRemoteDataSource.getInstance(),
                TracksLocalDataSource.getInstance(context)
        );
    }
}
