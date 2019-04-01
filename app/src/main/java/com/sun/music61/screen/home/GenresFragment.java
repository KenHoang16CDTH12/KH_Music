package com.sun.music61.screen.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import com.sun.music61.R;
import com.sun.music61.data.model.Track;
import com.sun.music61.screen.MainActivity;
import com.sun.music61.screen.home.adapter.TrackAdapter;
import com.sun.music61.screen.play.PlayFragment;
import com.sun.music61.screen.service.PlayTrackListener;
import com.sun.music61.screen.service.PlayTrackService;
import com.sun.music61.util.CommonUtils;
import com.sun.music61.util.RepositoryInstance;
import com.sun.music61.util.helpers.OnScrollPagination;
import com.sun.music61.util.listener.ItemRecyclerOnClickListener;
import dmax.dialog.SpotsDialog;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class GenresFragment extends Fragment implements GenresContract.View,
        ItemRecyclerOnClickListener, PlayTrackListener {

    private static final String ARGUMENT_GENRES = "ARGUMENT_GENRES";
    private static final int ZERO = 0;

    private GenresContract.Presenter mPresenter;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerGenres;
    private TrackAdapter mAdapter;
    private AlertDialog mDialogWaiting;
    private int mOffset;
    private String mGenres;
    private List<Track> mTracks;
    private PlayTrackService mService;

    public static GenresFragment newInstance(String genres) {
        GenresFragment fragment = new GenresFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_GENRES, genres);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Fetch Arguments
        mGenres = Objects.requireNonNull(getArguments()).getString(ARGUMENT_GENRES);
        View rootView = inflater.inflate(R.layout.genres_fragment, container, false);
        initView(rootView);
        mPresenter = new GenresPresenter(RepositoryInstance.getInstanceTrackRepository(
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
        mDialogWaiting = new SpotsDialog.Builder().setContext(getContext())
                .setMessage(R.string.text_waiting)
                .setCancelable(false)
                .build();
        mRecyclerGenres = rootView.findViewById(R.id.recyclerGenres);
        mRecyclerGenres.setHasFixedSize(true);
        mAdapter = new TrackAdapter(R.layout.item_track_list);
        mAdapter.setOnItemClickListener(this);
        mRecyclerGenres.setAdapter(mAdapter);
        mRecyclerGenres.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down));
        mSwipeLayout = rootView.findViewById(R.id.swipeLayout);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);
    }

    private void onListenerEvent() {
        mSwipeLayout.post(() -> {
            mDialogWaiting.show();
            loadData();
            mSwipeLayout.setRefreshing(true);
        });
        mSwipeLayout.setOnRefreshListener(this::loadData);
        mRecyclerGenres.addOnScrollListener(new OnScrollPagination() {
            @Override
            protected void loadMoreItems() {
                mOffset += CommonUtils.Constants.LIMIT_DEFAULT;
                mPresenter.loadAllTracks(mGenres, mOffset);
            }
        });
    }

    private void loadData() {
        mOffset = ZERO;
        mPresenter.loadAllTracks(mGenres, CommonUtils.Constants.DEFAULT_OFFSET);
    }

    @Override
    public void setPresenter(GenresContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onGetTracksSuccess(List<Track> tracks) {
        mTracks = tracks;
        if (mOffset == ZERO) {
            mAdapter.updateData(tracks);
            if (mSwipeLayout.isRefreshing())
                mSwipeLayout.setRefreshing(false);
        } else {
            mAdapter.loadMoreData(tracks);
        }
        if (mDialogWaiting.isShowing() && mDialogWaiting != null)
            mDialogWaiting.dismiss();
    }

    @Override
    public void onDataTracksNotAvailable() {
        mRecyclerGenres.setVisibility(View.GONE);
        mDialogWaiting.dismiss();
    }

    @Override
    public void showErrors(String message) {
    }

    @Override
    public void onDestroy() {
        mService.removeListener(this);
        super.onDestroy();
    }

    @Override
    public void onRecyclerItemClick(Object object, int position) {
        mService.setTracks(mTracks);
        mService.changeTrack((Track) object);
        MainActivity.replaceFragment((AppCompatActivity) Objects.requireNonNull(getActivity()),
                PlayFragment.newInstance());
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
