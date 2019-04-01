package com.sun.music61.data.source.remote;

import android.util.Log;
import com.sun.music61.data.model.Response;
import com.sun.music61.data.model.Track;
import com.sun.music61.data.source.RepositoryCallBack;
import com.sun.music61.data.source.TracksDataSource;
import com.sun.music61.util.CommonUtils;
import com.sun.music61.util.helpers.APIFactory;
import com.sun.music61.util.listener.APICallback;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sun.music61.util.CommonUtils.Constants;
import static com.sun.music61.util.StatusCodeUtils.OK;

public class TracksRemoteDataSource implements TracksDataSource.RemoteDataSource {

    private static final String TAG = TracksRemoteDataSource.class.getName();

    private static TracksRemoteDataSource sInstance;

    public static TracksRemoteDataSource getInstance() {
        if (sInstance == null) {
            sInstance = new TracksRemoteDataSource();
        }
        return sInstance;
    }

    private TracksRemoteDataSource() {
    }

    @Override
    public void getBanners(RepositoryCallBack callback) {
        APIFactory.Builder()
                .baseUrl(CommonUtils.API_TRACKS
                        + CommonUtils.KeyParams.TAGS + Constants.TAG_SONG + "&"
                        + CommonUtils.KeyParams.LIMIT + Constants.LIMIT_BANNER + "&"
                        + CommonUtils.KeyParams.OFFSET + Constants.DEFAULT_OFFSET)
                .method(APIFactory.Method.GET)
                .enqueue(new APICallback() {
                    @Override
                    public void onResponse(Response response) {
                        callback.onSuccess(parseTracks(response));
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onFailed(throwable);
                    }
                })
                .build();
    }

    @Override
    public void getTracksByGenres(String genres, int offset, RepositoryCallBack callback) {
        APIFactory.Builder()
                .baseUrl(CommonUtils.API_TRACKS
                        + CommonUtils.KeyParams.GENRES + genres + "&"
                        + CommonUtils.KeyParams.ORDER + Constants.ORDER + "&"
                        + CommonUtils.KeyParams.LIMIT + Constants.LIMIT_DEFAULT + "&"
                        + CommonUtils.KeyParams.OFFSET + offset)
                .method(APIFactory.Method.GET)
                .enqueue(new APICallback() {
                    @Override
                    public void onResponse(Response response) {
                        callback.onSuccess(parseTracks(response));
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onFailed(throwable);
                    }
                })
                .build();
    }

    private List<Track> parseTracks(Response response) {
        ArrayList<Track> tracks = new ArrayList<>();
        switch (response.getStatusCode()) {
            case OK:
                try {
                    StringBuffer data = response.getResult();
                    JSONArray array = new JSONArray(data.toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Track track = new Track(obj);
                        tracks.add(track);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "parseTracks: " + e.getMessage());
                }
                break;
            default:
                Log.e(TAG, "parseTracks: " + response.getStatusCode());
                break;
        }
        return tracks;
    }
}
