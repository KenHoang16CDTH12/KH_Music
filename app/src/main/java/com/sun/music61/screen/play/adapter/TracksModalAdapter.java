package com.sun.music61.screen.play.adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.sun.music61.R;
import com.sun.music61.data.model.Track;
import com.sun.music61.util.CommonUtils;
import com.sun.music61.util.listener.ItemTouchAdapterListener;
import com.sun.music61.util.listener.ItemTouchViewHolderListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TracksModalAdapter extends RecyclerView.Adapter<TracksModalAdapter.ViewHolder>
        implements ItemTouchAdapterListener {

    private static final int POSITION_START = 0;

    private List<Track> mTracks;
    private Track mTrackPlaying;
    private TrackModalClickListener mListener;

    public void setListener(TrackModalClickListener listener) {
        mListener = listener;
    }

    public TracksModalAdapter() {
        mTracks = new ArrayList<>();
    }

    public void setTrackPlaying(Track trackPlaying) {
        mTrackPlaying = trackPlaying;
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

    public void removeTrack(Track track) {
        int position = mTracks.indexOf(track);
        mTracks.remove(track);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mTracks.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_track_line, viewGroup, false);
        return new ViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        boolean isPlaying = mTracks.get(position).equals(mTrackPlaying);
        viewHolder.hoverPlaying(isPlaying);
        viewHolder.bindData(mTracks.get(position));
    }

    @Override
    public int getItemCount() {
        return mTracks != null ? mTracks.size() : 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mTracks, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemMoved() {
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            ItemTouchViewHolderListener {

        private Track mTrack;
        private TextView mTextCount;
        private TextView mTextSong;
        private TextView mTextAuthor;
        private ImageView mButtonRemove;
        private TrackModalClickListener mListener;
        private boolean isPlaying;

        private ViewHolder(@NonNull View itemView, TrackModalClickListener listener) {
            super(itemView);
            mListener = listener;
            mTextCount = itemView.findViewById(R.id.textCount);
            mTextSong = itemView.findViewById(R.id.textSong);
            mTextAuthor = itemView.findViewById(R.id.textAuthor);
            mButtonRemove = itemView.findViewById(R.id.buttonRemove);
            mButtonRemove.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void hoverPlaying(boolean isPlaying) {
            this.isPlaying = isPlaying;
            if (isPlaying) stylePlaying();
            else styleNotPlaying();
        }

        private void stylePlaying() {
            Resources resources = itemView.getResources();
            mTextCount.setTextColor(resources.getColor(R.color.textHover));
            mTextSong.setTextColor(resources.getColor(R.color.textHover));
            mTextAuthor.setTextColor(resources.getColor(R.color.textHover));
            mButtonRemove.setImageResource(R.drawable.ic_surround_sound_white_24dp);
            mButtonRemove.setColorFilter(resources.getColor(R.color.textHover));
        }

        private void styleNotPlaying() {
            Resources resources = itemView.getResources();
            mTextCount.setTextColor(resources.getColor(android.R.color.black));
            mTextSong.setTextColor(resources.getColor(R.color.textSongWhite));
            mTextAuthor.setTextColor(resources.getColor(R.color.textAuthorGrey));
            mButtonRemove.setImageResource(R.drawable.ic_remove_circle_outline_24dp);
            mButtonRemove.setColorFilter(resources.getColor(R.color.buttonColorGrey));
        }

        private void bindData(Track track) {
            mTrack = track;
            // Set up animation
            loadAnimation(mTextCount, R.anim.fade_transition_animation);
            loadAnimation(mTextSong, R.anim.fade_scale_animation);
            loadAnimation(mTextAuthor, R.anim.fade_scale_animation);
            loadAnimation(mButtonRemove, R.anim.fade_scale_animation);
            mTextCount.setText(String.format(Locale.US, "%d", getAdapterPosition() + CommonUtils.Number.ONE));
            mTextSong.setText(track.getTitle());
            mTextAuthor.setText(track.getUser().getUsername());
        }

        private void loadAnimation(View view, int resource) {
            view.setAnimation(AnimationUtils.loadAnimation(itemView.getContext(), resource));
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                switch (view.getId()) {
                    case R.id.buttonRemove:
                        if (isPlaying) return;
                        else mListener.onRemoveTrackClick(mTrack);
                        break;
                    default:
                        if (isPlaying) return;
                        else mListener.onTrackClick(mTrack);
                        break;
                }
            }
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public interface TrackModalClickListener {
        void onTrackClick(Track track);
        void onRemoveTrackClick(Track track);
    }
}
