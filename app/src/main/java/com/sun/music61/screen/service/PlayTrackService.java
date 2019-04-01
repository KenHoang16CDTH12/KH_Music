package com.sun.music61.screen.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import com.sun.music61.data.model.Track;
import com.sun.music61.media.Loop;
import com.sun.music61.media.MediaPlayerManager;
import com.sun.music61.media.Shuffle;
import com.sun.music61.media.State;
import com.sun.music61.util.notification.MusicNotificationHelper;
import java.util.ArrayList;
import java.util.List;

import static com.sun.music61.util.CommonUtils.Action;

public class PlayTrackService extends Service implements
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener  {

    interface Status {
        int STATE_NOTIFICATION = 0;
        int STATE_TRACK_NOTIFICATION = 1;
        int SETTING = 2;
        int TRACK = 3;
    }

    private IBinder mBinder;
    private MediaPlayerManager mPlayerManager;
    private List<PlayTrackListener> mListeners;
    private MusicNotificationHelper mNotificationHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new TrackBinder();
        mPlayerManager = MediaPlayerManager.getInstance(this);
        mListeners = new ArrayList<>();
        mNotificationHelper = new MusicNotificationHelper(this);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, PlayTrackService.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == null) return START_NOT_STICKY;
        switch (intent.getAction()) {
            case Action.ACTION_PLAY_AND_PAUSE:
                actionPlayAndPause();
                break;
            case Action.ACTION_NEXT:
                nextTrack();
                break;
            case Action.ACTION_PREVIOUS:
                previousTrack();
                break;
            case Action.ACTION_FAVORITE:
            default:
                // Code late
                break;
        }
        return START_NOT_STICKY;
    }

    public void actionPlayAndPause() {
        if (getState() == State.PAUSE) startTrack();
        else pauseTrack();
    }

    private void update(int type) {
        switch (type) {
            case Status.STATE_TRACK_NOTIFICATION:
                notifyTrackChange();
            case Status.STATE_NOTIFICATION:
                notifyStateChange();
                notificationChange();
                break;
            case Status.SETTING:
                notifySettingChange();
                break;
            case Status.TRACK:
                notifyTrackChange();
                break;
        }
    }

    private void notifyStateChange() {
        for (PlayTrackListener listener : mListeners) {
            listener.onStateChanged(getState());
        }
    }

    private void notifySettingChange() {
        for (PlayTrackListener listener : mListeners) {
            listener.onSettingChanged();
        }
    }

    private void notifyTrackChange() {
        for (PlayTrackListener listener : mListeners) {
            listener.onTrackChanged(getCurrentTrack());
        }
    }

    private void notificationChange() {
        mNotificationHelper.updateStateNotification();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        switch (mPlayerManager.getLoop()) {
            case Loop.ONE:
                changeTrack(getCurrentTrack());
                break;
            case Loop.ALL:
                nextTrack();
                break;
            case Loop.NONE:
                if (mPlayerManager.isLastTracks(getCurrentTrack())) {
                    stopTrack();
                } else {
                    nextTrack();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        startTrack();
    }

    public void startTrack() {
        mPlayerManager.start();
        update(Status.STATE_NOTIFICATION);
    }

    public void changeTrack(Track track) {
        mPlayerManager.change(track);
        update(Status.STATE_TRACK_NOTIFICATION);
    }

    public void pauseTrack() {
        mPlayerManager.pause();
        update(Status.STATE_NOTIFICATION);    }

    public void previousTrack() {
        mPlayerManager.previous();
        update(Status.TRACK);
    }

    public void nextTrack() {
        mPlayerManager.next();
        update(Status.TRACK);
    }

    public void stopTrack() {
        mPlayerManager.stop();
        update(Status.STATE_NOTIFICATION);
    }

    public void seek(int milliseconds) {
        mPlayerManager.seek(milliseconds);
    }

    public long getDuration() {
        return mPlayerManager.getDuration();
    }

    public long getCurrentDuration() {
        return mPlayerManager.getCurrentDuration();
    }

    @State
    public int getState() {
        return mPlayerManager.getState();
    }

    public void setTracks(List<Track> tracks) {
        mPlayerManager.setTracks(tracks);
    }

    public void shuffleTracks() {
        mPlayerManager.shuffleTracks();
        update(Status.SETTING);
    }

    public void unShuffleTracks() {
        mPlayerManager.unShuffleTracks();
        update(Status.SETTING);
    }

    @Shuffle
    public int getShuffle() {
        return mPlayerManager.getShuffle();
    }

    public void loopTracks(@Loop int loop) {
        mPlayerManager.setLoop(loop);
        update(Status.SETTING);
    }

    @Loop
    public int getLoop() {
        return mPlayerManager.getLoop();
    }

    public List<Track> getTracks() {
        return mPlayerManager.getTracks();
    }

    public void removeTrack(Track track) {
        mPlayerManager.removeTrack(track);
    }

    public Track getCurrentTrack() {
        return mPlayerManager.getCurrentTrack();
    }

    public void addListeners(PlayTrackListener listener) {
        mListeners.add(listener);
    }

    public void removeAllListeners() {
        mListeners.clear();
    }

    public void removeListener(PlayTrackListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class TrackBinder extends Binder {
        public PlayTrackService getService() {
            return PlayTrackService.this;
        }
    }
}
