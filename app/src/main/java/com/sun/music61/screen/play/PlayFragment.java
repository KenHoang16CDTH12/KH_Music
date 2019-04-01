package com.sun.music61.screen.play;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.cleveroad.sy.cyclemenuwidget.CycleMenuWidget;
import com.cleveroad.sy.cyclemenuwidget.OnMenuItemClickListener;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.sun.music61.R;
import com.sun.music61.data.model.Track;
import com.sun.music61.media.Loop;
import com.sun.music61.media.Shuffle;
import com.sun.music61.media.State;
import com.sun.music61.screen.MainActivity;
import com.sun.music61.screen.home.HomeFragment;
import com.sun.music61.screen.offline.OfflineFragment;
import com.sun.music61.screen.service.DownloadTrackService;
import com.sun.music61.screen.service.PlayTrackListener;
import com.sun.music61.screen.service.PlayTrackService;
import com.sun.music61.util.ActivityUtils;
import com.sun.music61.util.CommonUtils;
import com.sun.music61.util.PermissionUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.Objects;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class PlayFragment extends Fragment implements PlayTrackListener, CircularSeekBar.OnCircularSeekBarChangeListener,
        TrackControlListener {

    private static final int DEFAULT_PROGRESS = 0;
    private static final long TIME_DELAY = 1000;
    private static final String SHARE_TYPE = "text/plain";

    private Toolbar mToolbar;
    private KenBurnsView mImageBackground;
    private CircularSeekBar mSeekBarProccess;
    private CircleImageView mImageSong;
    private TextView mTextDuration;
    private ImageView mButtonShuffle;
    private ImageView mButtonPlay;
    private ImageView mButtonRepeat;
    private ImageView mImagePlay;
    private ConstraintLayout mChildLayout;
    private CycleMenuWidget mCycleMenuMain;
    private CycleMenuWidget mCycleMenuAction;
    private Animation mAnimation;
    private PlayTrackService mService;
    private Handler mHandlerSyncTime;

    public static PlayFragment newInstance() {
        return new PlayFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.play_fragment, container, false);
        initView(rootView);
        onListenerEvent(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mService = ((MainActivity) Objects.requireNonNull(getActivity())).getService();
        mService.addListeners(this);
        fetchDataTrack(mService.getCurrentTrack());
        handlerSyncTime();
    }

    private void initView(View rootView) {
        mToolbar = rootView.findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mImageBackground = rootView.findViewById(R.id.imageBackground);
        mSeekBarProccess = rootView.findViewById(R.id.seekBarProcess);
        mImageSong = rootView.findViewById(R.id.imageSongCircle);
        mTextDuration = rootView.findViewById(R.id.textDuration);
        mButtonShuffle = rootView.findViewById(R.id.buttonShuffle);
        mButtonPlay = rootView.findViewById(R.id.buttonPlay);
        mButtonRepeat = rootView.findViewById(R.id.buttonRepeat);
        mImagePlay = rootView.findViewById(R.id.imagePlay);
        mChildLayout = rootView.findViewById(R.id.childLayout);
        mCycleMenuMain = rootView.findViewById(R.id.cycleMenuMain);
        mCycleMenuMain.setMenuRes(R.menu.cycle_menu_play_main);
        mCycleMenuAction = rootView.findViewById(R.id.cycleMenuAction);
        mCycleMenuAction.setMenuRes(R.menu.cycle_menu_play_action);
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_image_song);
    }

    private void fetchDataTrack(Track track) {
        mToolbar.setTitle(track.getTitle());
        mToolbar.setSubtitle(track.getUser().getUsername());
        mSeekBarProccess.setProgress(DEFAULT_PROGRESS);
        CommonUtils.loadImageFromUrl(mImageBackground, track.getArtworkUrl(), CommonUtils.T500);
        CommonUtils.loadImageFromUrl(mImageSong, track.getArtworkUrl(), CommonUtils.T300);
        onSettingChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onListenerEvent(View rootView) {
        mButtonPlay.setOnClickListener(view -> play());
        mButtonShuffle.setOnClickListener(view -> shuffle());
        mButtonRepeat.setOnClickListener(view -> loop());
        mSeekBarProccess.setOnSeekBarChangeListener(this);
        mCycleMenuMain.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(View view, int itemPosition) {
                switch (view.getId()) {
                    case R.id.actionHome:
                    case R.id.actionMySong:
                    case R.id.actionFavorite:
                    case R.id.actionSetting:
                        // Do nothing
                        break;
                }
            }

            @Override
            public void onMenuItemLongClick(View view, int itemPosition) {

            }
        });
        mCycleMenuAction.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(View view, int itemPosition) {
                switch (view.getId()) {
                    case R.id.actionFavorite:
                        break;
                    case R.id.actionCamera:
                        break;
                    case R.id.actionDownload:
                        PermissionUtils.checkWriteExternalPermission(getActivity(), success -> {
                            if (success) download();
                            else Snackbar.make(mChildLayout, R.string.text_rejected_msg, Snackbar.LENGTH_SHORT).show();
                        });
                        break;
                    case R.id.actionShare:
                        share();
                        break;
                }
            }

            @Override
            public void onMenuItemLongClick(View view, int itemPosition) {
                // Do nothing
            }
        });
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_MAX_OFF_PATH = 250;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                return false;
                            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                prev();
                            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                next();
                            }
                        } catch (Exception e) {
                            // do nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });
        rootView.setOnTouchListener((v, event) -> gesture.onTouchEvent(event));
    }

    private void share() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType(SHARE_TYPE);
        intent.putExtra(Intent.EXTRA_TEXT, mService.getCurrentTrack().getStreamUrl());
        startActivity(Intent.createChooser(intent, mService.getCurrentTrack().getTitle()));
    }

    private void download() {
        Objects.requireNonNull(getActivity())
                .startService(DownloadTrackService
                        .getIntent(getContext(), mService.getCurrentTrack()));
    }

    private void handlerSyncTime() {
        mHandlerSyncTime = new Handler();
        mHandlerSyncTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mService.getState() == State.PLAY) {
                    long totalTime = mService.getDuration();
                    long currentTime = mService.getCurrentDuration();
                    mSeekBarProccess.setProgress(CommonUtils.progressPercentage(currentTime, totalTime));
                    mTextDuration.setText(CommonUtils.convertTimeInMilisToString(currentTime));
                }
                mHandlerSyncTime.postDelayed(this, TIME_DELAY);
            }
        }, TIME_DELAY);
    }

    @Override
    public void play() {
        mService.actionPlayAndPause();
    }

    @Override
    public void prev() {
        mService.previousTrack();
    }

    @Override
    public void next() {
        mService.nextTrack();
    }

    @Override
    public void shuffle() {
        if (mService.getShuffle() == Shuffle.OFF) mService.shuffleTracks();
        else mService.unShuffleTracks();
    }

    private void updateShuffleIcon() {
        switch (mService.getShuffle()) {
            case Shuffle.OFF:
                mButtonShuffle.setColorFilter(R.color.colorProgress);
                break;
            case Shuffle.ON:
                mButtonShuffle.setColorFilter(android.R.color.white);
                break;
        }
    }

    @Override
    public void loop() {
        if (mService.getLoop() == Loop.NONE) mService.loopTracks(Loop.ALL);
        else if (mService.getLoop() == Loop.ALL) mService.loopTracks(Loop.ONE);
        else mService.loopTracks(Loop.NONE);
    }

    private void updateLoopIcon() {
        mButtonRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_48dp_scaled));
        switch (mService.getLoop()) {
            case Loop.ALL:
                mButtonRepeat.setColorFilter(android.R.color.white);
                break;
            case Loop.NONE:
                mButtonRepeat.setColorFilter(R.color.colorProgress);
                break;
            case Loop.ONE:
                mButtonRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one_48dp_scaled));
                break;
        }
    }

    @Override
    public void onProgressChanged(CircularSeekBar circularSeekBar, float progress,
            boolean fromUser) {
        if (fromUser) {
            mService.seek(CommonUtils.progressToTimer(progress, mService.getDuration()));
            mTextDuration.setText(CommonUtils.convertTimeInMilisToString((long) progress));
        }
    }

    @Override
    public void onStopTrackingTouch(CircularSeekBar seekBar) {
        mTextDuration.setText(CommonUtils.convertTimeInMilisToString((long) seekBar.getProgress()));
    }

    @Override
    public void onStartTrackingTouch(CircularSeekBar seekBar) {
        mTextDuration.setText(CommonUtils.convertTimeInMilisToString((long) seekBar.getProgress()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Objects.requireNonNull(getActivity())
                .getMenuInflater().inflate(R.menu.option_menu_play, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionPlayList:
                TracksModalFragment.showInstance(getChildFragmentManager());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService.removeListener(this);
    }

    @Override
    public void onStateChanged(int state) {
        if (mService.getState() == State.PAUSE) {
            mImagePlay.setImageResource(R.drawable.ic_play_48dp);
            mImageSong.clearAnimation();
        } else {
            mImagePlay.setImageResource(R.drawable.ic_pause_48dp);
            mImageSong.startAnimation(mAnimation);
        }
    }

    @Override
    public void onSettingChanged() {
        updateLoopIcon();
        updateShuffleIcon();
    }

    @Override
    public void onTrackChanged(Track track) {
        fetchDataTrack(track);
    }
}
