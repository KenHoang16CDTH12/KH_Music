package com.sun.music61.screen.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.sun.music61.R;
import com.sun.music61.data.model.Track;
import com.sun.music61.util.CommonUtils;
import com.sun.music61.util.listener.ItemRecyclerOnClickListener;
import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private static final int POSITION_START = 0;

    private List<Track> mTracks;
    private int mResource;
    private ItemRecyclerOnClickListener mListener;

    public void setOnItemClickListener(ItemRecyclerOnClickListener listener) {
        mListener = listener;
    }

    public TrackAdapter(int resource) {
        mResource = resource;
        mTracks = new ArrayList<>();
    }

    public void updateData(List<Track> tracks) {
        mTracks.clear();
        mTracks.addAll(tracks);
        notifyItemRangeChanged(POSITION_START, tracks.size());
    }

    public void add(Track track) {
        mTracks.add(track);
        notifyItemInserted(getItemCount() - 1);
    }

    public void loadMoreData(List<Track> tracks) {
        for (Track track: tracks)
            add(track);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View rootView =
                LayoutInflater.from(viewGroup.getContext()).inflate(mResource, viewGroup, false);
        return new ViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bindData(mTracks.get(position));
    }

    @Override
    public int getItemCount() {
        return mTracks != null ? mTracks.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Track mTrack;
        private ImageView mImageSong;
        private TextView mTextSong;
        private TextView mTextAuthor;
        private ImageView mButtonPlay;
        private ItemRecyclerOnClickListener mListener;

        private ViewHolder(@NonNull View itemView, ItemRecyclerOnClickListener listener) {
            super(itemView);
            mListener = listener;
            mImageSong = itemView.findViewById(R.id.imageSong);
            mTextSong = itemView.findViewById(R.id.textSong);
            mTextAuthor = itemView.findViewById(R.id.textAuthor);
            mButtonPlay = itemView.findViewById(R.id.buttonPlay);
            itemView.setOnClickListener(this);
        }

        private void bindData(Track track) {
            mTrack = track;
            // Set up animation
            loadAnimation(mImageSong, R.anim.fade_transition_animation);
            loadAnimation(mTextSong, R.anim.fade_scale_animation);
            loadAnimation(mTextAuthor, R.anim.fade_scale_animation);
            loadAnimation(mButtonPlay, R.anim.fade_scale_animation);
            if (CommonUtils.checkNotNull(track.getArtworkUrl())) {
                CommonUtils.loadImageFromUrl(mImageSong, track.getArtworkUrl(), CommonUtils.T300);
            } else {
                mImageSong.setImageResource(R.drawable.ic_filter_hdr_black_124dp);
            }
            mTextSong.setText(track.getTitle());
            mTextAuthor.setText(track.getUser().getUsername());
        }

        private void loadAnimation(View view, int resource) {
            view.setAnimation(AnimationUtils.loadAnimation(itemView.getContext(), resource));
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) mListener.onRecyclerItemClick(mTrack, getAdapterPosition());
        }
    }
}
