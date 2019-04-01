package com.sun.music61.data.source.local;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Function;
import com.sun.music61.data.model.Track;
import com.sun.music61.data.source.RepositoryCallBack;
import com.sun.music61.data.source.TracksDataSource;
import java.util.List;

public class TracksLocalDataSource implements TracksDataSource.LocalDataSource {

    @Nullable
    private static TracksLocalDataSource sInstance;
    @NonNull
    private Function<Cursor, Track> mTrackMapperFunction;
    private Context mContext;

    private TracksLocalDataSource(Context context) {
        mContext = context;
    }

    public static TracksLocalDataSource getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TracksLocalDataSource(context);
        }
        return sInstance;
    }

    public static void destroyInstance() {
        sInstance = null;
    }

    @Override
    public void getTracksStorage(int type, RepositoryCallBack callback) {
        TracksLocalFactory.Builder()
                .resolver(mContext.getContentResolver())
                .type(type)
                .onListener(new TracksLocalCallback() {
                    @Override
                    public void onSuccess(List<Track> tracks) {
                        callback.onSuccess(tracks);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onFailed(throwable);
                    }
                })
                .query();
    }
}
