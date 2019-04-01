package com.sun.music61.screen.home.adapter;

import com.sun.music61.R;
import com.sun.music61.data.model.Track;
import java.util.List;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class CustomSliderAdapter extends SliderAdapter {

    private List<Track> mBanners;

    public CustomSliderAdapter(List<Track> banners) {
        mBanners = banners;
    }

    @Override
    public int getItemCount() {
        return mBanners != null ? mBanners.size() : 0;
    }

    public Track getBanner(int position) {
        return mBanners.get(position);
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        if (mBanners.get(position).getArtworkUrl() != null) {
            imageSlideViewHolder.bindImageSlide(mBanners.get(position).getArtworkUrl());
        } else {
            imageSlideViewHolder.bindImageSlide(R.drawable.ic_filter_hdr_white_124);
        }
    }
}
