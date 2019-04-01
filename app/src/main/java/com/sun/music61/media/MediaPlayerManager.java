package com.sun.music61.media;

import android.net.Uri;
import com.sun.music61.data.model.Track;
import com.sun.music61.screen.service.PlayTrackService;
import com.sun.music61.util.CommonUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.sun.music61.util.CommonUtils.Number;

public class MediaPlayerManager extends MediaSetting {

    private static MediaPlayerManager sInstance;

    private PlayTrackService mService;
    private Track mCurrentTrack;
    private List<Track> mTracks;

    private MediaPlayerManager(PlayTrackService service) {
        super();
        mService = service;
        mTracks = new ArrayList<>();
    }

    public static MediaPlayerManager getInstance(PlayTrackService service) {
        if (sInstance == null)
            sInstance = new MediaPlayerManager(service);
        return sInstance;
    }

    @Override
    public void create() {
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mService, Uri.parse(mCurrentTrack.getStreamUrl()));
        } catch (IOException e) {
            // Do nothing
        }
        mMediaPlayer.setOnErrorListener(mService);
        mMediaPlayer.setOnCompletionListener(mService);
        mMediaPlayer.setOnPreparedListener(mService);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void start() {
        mMediaPlayer.start();
        setState(State.PLAY);
    }

    @Override
    public <T> void change(T object) {
        mCurrentTrack = (Track) object;
        create();
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
        setState(State.PAUSE);
    }

    @Override
    public void previous() {
        change(getPreviousTrack());
    }

    @Override
    public void next() {
        if (mShuffle == Shuffle.OFF) change(getNextTrack());
        else change(getRandomTrack());
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
        setState(State.PAUSE);
    }

    private Track getPreviousTrack() {
        int position = mTracks.indexOf(mCurrentTrack);
        if (position == Number.ZERO) return mTracks.get(mTracks.size() - Number.ONE); // Last track
        return mTracks.get(position - Number.ONE);
    }

    private Track getNextTrack() {
        int position = mTracks.indexOf(mCurrentTrack);
        if (position == mTracks.size() - Number.ONE) return mTracks.get(Number.ZERO); // First track
        return mTracks.get(position + Number.ONE);
    }

    private Track getRandomTrack() {
        Random random = new Random();
        return mTracks.get(random.nextInt(mTracks.size()));
    }

    public void setCurrentTrack(Track track) {
        mCurrentTrack = track;
    }

    public void setTracks(List<Track> tracks) {
        if (tracks != null) {
            mTracks.clear();
            mTracks.addAll(tracks);
        }
    }

    public List<Track> getTracks() {
        return mTracks;
    }

    public void addTrack(Track track) {
        mTracks.add(track);
    }

    public void addTracks(List<Track> tracks) {
        mTracks.addAll(tracks);
    }

    public void removeTrack(Track track) {
        if (track != null) mTracks.remove(track);
    }

    public Track getCurrentTrack() {
        return mCurrentTrack;
    }

    public boolean isLastTracks(Track currentTrack) {
        return mTracks.indexOf(currentTrack) == mTracks.size() - Number.ONE;
    }
}
