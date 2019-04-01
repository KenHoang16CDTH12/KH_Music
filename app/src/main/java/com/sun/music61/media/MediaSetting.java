package com.sun.music61.media;

import android.media.MediaPlayer;

public abstract class MediaSetting {

    protected MediaPlayer mMediaPlayer;
    @State
    protected int mState;
    @Loop
    protected int mLoop;
    @Shuffle
    protected int mShuffle;

    protected MediaSetting() {
        mState = State.PAUSE;
        mMediaPlayer = new MediaPlayer();
    }

    public abstract void create();
    public abstract void start();
    public abstract <T> void change(T object);
    public abstract void pause();
    public abstract void previous();
    public abstract void next();
    public abstract void stop();

    public void release() {
        mMediaPlayer.release();
    }

    public void reset() {
        mMediaPlayer.reset();
    }

    public void seek(int milliseconds) {
        mMediaPlayer.seekTo(milliseconds);
    }

    public void shuffleTracks() {
        setShuffle(Shuffle.ON);
    }

    public void unShuffleTracks() {
        setShuffle(Shuffle.OFF);
    }

    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    public long getCurrentDuration() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void setState(@State int state) {
        mState = state;
    }

    @State
    public int getState() {
        return mState;
    }

    public void setLoop(int loop) {
        mLoop = loop;
    }

    public int getLoop() {
        return mLoop;
    }

    public int getShuffle() {
        return mShuffle;
    }

    public void setShuffle(int shuffle) {
        mShuffle = shuffle;
    }
}
