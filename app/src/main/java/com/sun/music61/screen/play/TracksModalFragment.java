package com.sun.music61.screen.play;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sun.music61.R;
import com.sun.music61.custom.CustomItemTouchCallBack;
import com.sun.music61.data.model.Track;
import com.sun.music61.screen.MainActivity;
import com.sun.music61.screen.play.adapter.TracksModalAdapter;
import com.sun.music61.screen.service.PlayTrackListener;
import com.sun.music61.screen.service.PlayTrackService;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TracksModalFragment extends BottomSheetDialogFragment implements PlayTrackListener,
TracksModalAdapter.TrackModalClickListener {

    private static final String NUMBER_OF_TRACKS = "Tracks (%d)";
    private static final int OFFSET = 0;

    private TextView mTextNumberTracks;
    private RecyclerView mRecyclerTracks;
    private PlayTrackService mService;
    private TracksModalAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    public static void showInstance(FragmentManager transaction) {
        TracksModalFragment instance = new TracksModalFragment();
        instance.show(transaction, instance.getTag());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NO_TITLE, R.style.CustomDialogFragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tracks_modal_fragment, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mService = ((MainActivity) Objects.requireNonNull(getActivity())).getService();
        mService.addListeners(this);
        bindData(mService.getTracks());
    }

    private void initView(View rootView) {
        mTextNumberTracks = rootView.findViewById(R.id.textNumberTracks);
        mRecyclerTracks = rootView.findViewById(R.id.recyclerTracks);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerTracks.setLayoutManager(mLayoutManager);
    }

    private void bindData(List<Track> tracks) {
        mTextNumberTracks.setText(String.format(Locale.US, NUMBER_OF_TRACKS, tracks.size()));
        mAdapter = new TracksModalAdapter();
        mAdapter.updateData(tracks);
        mAdapter.setTrackPlaying(mService.getCurrentTrack());
        mAdapter.setListener(this);
        mRecyclerTracks.setAdapter(mAdapter);
        mLayoutManager.scrollToPosition(mService.getTracks()
                .indexOf(mService.getCurrentTrack()));
        ItemTouchHelper.Callback callback = new CustomItemTouchCallBack(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerTracks);
    }

    @Override
    public void onTrackClick(Track track) {
        mService.changeTrack(track);
    }

    @Override
    public void onRemoveTrackClick(Track track) {
        mAdapter.removeTrack(track);
        mService.removeTrack(track);
        mTextNumberTracks.setText(String.format(Locale.US, NUMBER_OF_TRACKS, mAdapter.getItemCount()));
    }

    @Override
    public void onStateChanged(int state) {

    }

    @Override
    public void onSettingChanged() {

    }

    @Override
    public void onTrackChanged(Track track) {
        mLayoutManager.scrollToPositionWithOffset(mService.getTracks()
                .indexOf(mService.getCurrentTrack()), OFFSET);
        mAdapter.setTrackPlaying(mService.getCurrentTrack());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        mService.removeListener(this);
        super.onDestroy();
    }
}
