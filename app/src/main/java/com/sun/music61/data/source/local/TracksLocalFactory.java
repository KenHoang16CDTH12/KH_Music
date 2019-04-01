package com.sun.music61.data.source.local;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import com.sun.music61.data.model.Track;
import java.util.ArrayList;
import java.util.List;

public class TracksLocalFactory extends AsyncTask<Void, Void, List<Track>> {

    private static final String SUN_FOLDER = "SunSound";
    private static final String ASTERISK = "*";
    private static final String CONDITION_EQUAL = " = ? ";

    public interface Type {
        int TRACK_ALL = 0;
        int TRACK_DOWNLOAD = 1;
        int TRACK_OTHER = 2;
    }

    private ContentResolver mResolver;
    private int mType;
    private TracksLocalCallback mCallback;

    public static Builder Builder() {
        return new Builder();
    }

    private TracksLocalFactory(final Builder builder) {
        mResolver = builder.getResolver();
        mType = builder.getType();
        mCallback = builder.getCallback();
    }

    @Override
    protected List<Track> doInBackground(Void... voids) {
        List<Track> tracks = new ArrayList<>();
        @SuppressLint("Recycle")
        Cursor cursor = mResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {ASTERISK},
                null,
                null,
                null
        );
        if (cursor != null) cursor.moveToFirst();

        int indexTrackId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
        int indexUrl = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int indexTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int indexDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int indexArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int indexAlbumID = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

        while (!cursor.isAfterLast()) {
            String id = cursor.getString(indexTrackId);
            String duration = cursor.getString(indexDuration);
            String title = cursor.getString(indexTitle);
            String artist = cursor.getString(indexArtist);
            String url = cursor.getString(indexUrl);
            int albumId = cursor.getInt(indexAlbumID);
            Track track = new Track(id, duration, title, artist, getArtWork(albumId), url);
            track.setDownloaded(true);
            tracks.add(track);
            cursor.moveToNext();
        }
        switch (mType) {
            case Type.TRACK_DOWNLOAD:
                return getTracksDownload(tracks);
            case Type.TRACK_OTHER:
                return getOtherTracks(tracks);
            case Type.TRACK_ALL:
            default:
                return tracks;
        }
    }

    private List<Track> getOtherTracks(List<Track> tracks) {
        List<Track> resultTracks = new ArrayList<>();
        for (Track track: tracks)
            if (!track.getStreamUrl().contains(SUN_FOLDER))
                resultTracks.add(track);
        return resultTracks;
    }

    private List<Track> getTracksDownload(List<Track> tracks) {
        List<Track> resultTracks = new ArrayList<>();
        for (Track track: tracks)
            if (track.getStreamUrl().contains(SUN_FOLDER))
                resultTracks.add(track);
        return resultTracks;
    }

    private String getArtWork(int albumId) {
        String artworkUrl = null;
        @SuppressLint("Recycle")
        Cursor cursor = mResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Albums.ALBUM_ART },
                MediaStore.Audio.Albums._ID + CONDITION_EQUAL,
                new String[] { String.valueOf(albumId) },
                null
        );
        if (cursor == null) return null;
        cursor.moveToFirst();
        if (!cursor.isAfterLast())
            artworkUrl = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
        return artworkUrl;
    }

    @Override
    protected void onPostExecute(List<Track> tracks) {
        super.onPostExecute(tracks);
        mCallback.onSuccess(tracks);
    }

    public static class Builder {
        private ContentResolver mResolver;
        private int mType;
        private TracksLocalCallback mCallback;

        public Builder resolver(ContentResolver resolver) {
            mResolver = resolver;
            return this;
        }

        public Builder type(int type) {
            mType = type;
            return this;
        }

        public Builder onListener(TracksLocalCallback callback) {
            mCallback = callback;
            return this;
        }

        public void query() {
            new TracksLocalFactory(this).execute();
        }

        private ContentResolver getResolver() {
            return mResolver;
        }

        private int getType() {
            return mType;
        }

        private TracksLocalCallback getCallback() {
            return mCallback;
        }
    }
}
