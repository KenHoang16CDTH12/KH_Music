package com.sun.music61.screen.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.sun.music61.R;
import com.sun.music61.data.model.Track;
import com.sun.music61.screen.MainActivity;
import com.sun.music61.screen.home.adapter.CustomSliderAdapter;
import com.sun.music61.screen.home.adapter.TrackAdapter;
import com.sun.music61.screen.play.PlayFragment;
import com.sun.music61.screen.service.PlayTrackListener;
import com.sun.music61.screen.service.PlayTrackService;
import com.sun.music61.util.CommonUtils;
import com.sun.music61.util.RepositoryInstance;
import com.sun.music61.util.helpers.ImageLoadingServiceHelpers;
import com.sun.music61.util.helpers.OnScrollPagination;
import com.sun.music61.util.listener.ItemRecyclerOnClickListener;
import dmax.dialog.SpotsDialog;
import java.util.List;
import java.util.Objects;
import ss.com.bannerslider.Slider;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sun.music61.util.CommonUtils.Genres;

public class AllSongsFragment extends Fragment implements AllSongsContract.View,
        ItemRecyclerOnClickListener, PlayTrackListener {

    private static final int ZERO = 0;

    private AllSongsContract.Presenter mPresenter;
    private AlertDialog mDialogWaiting;
    private Slider mSlider;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecycler;
    private TrackAdapter mAdapter;
    private int mOffset;
    private PlayTrackService mService;
    private List<Track> mTracks;

    public static AllSongsFragment newInstance() {
        return new AllSongsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.all_songs_fragment, container, false);
        initView(rootView);
        mPresenter = new AllSongPresenter(RepositoryInstance.getInstanceTrackRepository(
                Objects.requireNonNull(getActivity()).getApplicationContext()), this);
        onListenerEvent();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mService = ((MainActivity) Objects.requireNonNull(getActivity())).getService();
        mService.addListeners(this);
    }

    private void initView(View rootView) {
        mDialogWaiting = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage(R.string.text_waiting)
                .setCancelable(false)
                .build();
        mSlider = rootView.findViewById(R.id.slider);
        Slider.init(new ImageLoadingServiceHelpers());
        mRecycler = rootView.findViewById(R.id.recyclerMusic);
        mAdapter = new TrackAdapter(R.layout.item_track_square);
        mAdapter.setOnItemClickListener(this);
        mRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecycler.setAdapter(mAdapter);
        mRefreshLayout = rootView.findViewById(R.id.swipeLayout);
        mRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
    }

    private void onListenerEvent() {
        mRefreshLayout.post(this::loadData);
        mRefreshLayout.setOnRefreshListener(this::loadData);
        mRecycler.addOnScrollListener(new OnScrollPagination() {
            @Override
            protected void loadMoreItems() {
                mOffset += CommonUtils.Constants.LIMIT_DEFAULT;
                mPresenter.loadAllTracks(CommonUtils.Genres.ALL_MUSIC, mOffset);
            }
        });
    }

    private void loadData() {
        mDialogWaiting.show();
        mOffset = ZERO;
        mPresenter.loadAllBanners();
        mPresenter.loadAllTracks(Genres.ALL_MUSIC, CommonUtils.Constants.DEFAULT_OFFSET);
    }

    @Override
    public void setPresenter(AllSongsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onGetBannersSuccess(List<Track> banners) {
        mSlider.setVisibility(View.VISIBLE);
        mSlider.setAdapter(new CustomSliderAdapter(banners));
        mSlider.setOnSlideClickListener(position -> {
                    mService.setTracks(banners);
                    mService.changeTrack(banners.get(position));
                    MainActivity.replaceFragment((AppCompatActivity) Objects.requireNonNull(getActivity()),
                            PlayFragment.newInstance()); }
        );
    }

    @Override
    public void onDataBannersNotAvailable() {
        mSlider.setVisibility(View.GONE);
    }

    @Override
    public void onGetTracksSuccess(List<Track> tracks) {
        mTracks = tracks;
        if (mOffset == ZERO) {
            mAdapter.updateData(tracks);
            mRefreshLayout.setRefreshing(false);
        } else {
            mAdapter.loadMoreData(tracks);
        }
        if (mDialogWaiting.isShowing() && mDialogWaiting != null)
            mDialogWaiting.dismiss();
    }

    @Override
    public void onDataTracksNotAvailable() {
        mRecycler.setVisibility(View.GONE);
        mDialogWaiting.dismiss();
    }

    @Override
    public void showErrors(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService.removeListener(this);
    }

    @Override
    public void onRecyclerItemClick(Object object, int position) {
        mService.setTracks(mTracks);
        mService.changeTrack((Track) object);
        MainActivity.replaceFragment((AppCompatActivity) Objects.requireNonNull(getActivity()), PlayFragment.newInstance());
    }

    @Override
    public void onStateChanged(int state) {

    }

    @Override
    public void onSettingChanged() {

    }

    @Override
    public void onTrackChanged(Track track) {

    }
}
