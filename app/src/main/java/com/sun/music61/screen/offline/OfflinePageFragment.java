package com.sun.music61.screen.offline;

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
import com.sun.music61.screen.offline.adapter.TrackOfflineAdapter;
import com.sun.music61.screen.play.PlayFragment;
import com.sun.music61.screen.service.PlayTrackListener;
import com.sun.music61.screen.service.PlayTrackService;
import com.sun.music61.util.RepositoryInstance;
import com.sun.music61.util.listener.ItemRecyclerOnClickListener;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class OfflinePageFragment extends Fragment  implements OfflinePageContract.View,
        ItemRecyclerOnClickListener, PlayTrackListener {

    private static final String ARGUMENT_TYPE = "ARGUMENT TYPE";

    private OfflinePageContract.Presenter mPresenter;
    private RecyclerView mRecyclerTracks;
    private TrackOfflineAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private int mType;
    private List<Track> mTracks;
    private PlayTrackService mService;

    public static Fragment newInstance(int type) {
        OfflinePageFragment fragment = new OfflinePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARGUMENT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.offline_page_fragment, container, false);
        // Fetch Argument
        mType = Objects.requireNonNull(getArguments()).getInt(ARGUMENT_TYPE);
        initView(rootView);
        mPresenter = new OfflinePagePresenter(RepositoryInstance.getInstanceTrackRepository(
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
        mRecyclerTracks = rootView.findViewById(R.id.recyclerTracks);
        mRecyclerTracks.setHasFixedSize(true);
        mAdapter = new TrackOfflineAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerTracks.setAdapter(mAdapter);
        mRecyclerTracks.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down));
        mSwipeLayout = rootView.findViewById(R.id.swipeLayout);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);
    }

    private void onListenerEvent() {
        mSwipeLayout.post(() -> {
            loadData();
            mSwipeLayout.setRefreshing(true);
        });
        mSwipeLayout.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        mPresenter.loadAllTracks(mType);
    }

    @Override
    public void setPresenter(OfflinePageContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onGetTracksSuccess(List<Track> tracks) {
        mTracks = tracks;
        mAdapter.updateData(tracks);
        if (mSwipeLayout.isRefreshing())
            mSwipeLayout.setRefreshing(false);
    }

    @Override
    public void onDataTracksNotAvailable() {
        mRecyclerTracks.setVisibility(View.GONE);
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
    public void onStateChanged(int state) {

    }

    @Override
    public void onSettingChanged() {

    }

    @Override
    public void onTrackChanged(Track track) {

    }

    @Override
    public void onRecyclerItemClick(Object object, int position) {
        mService.setTracks(mTracks);
        mService.changeTrack((Track) object);
        MainActivity.replaceFragment((AppCompatActivity) Objects.requireNonNull(getActivity()),
                PlayFragment.newInstance());
    }
}
