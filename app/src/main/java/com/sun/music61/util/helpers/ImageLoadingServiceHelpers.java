package com.sun.music61.util.helpers;

import android.widget.ImageView;
import com.sun.music61.util.CommonUtils;
import ss.com.bannerslider.ImageLoadingService;

public class ImageLoadingServiceHelpers implements ImageLoadingService {

    @Override
    public void loadImage(String url, ImageView imageView) {
        CommonUtils.loadImageFromUrl(imageView, url, CommonUtils.T500);
    }

    @Override
    public void loadImage(int resource, ImageView imageView) {
        imageView.setImageResource(resource);
    }

    @Override
    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
        CommonUtils.loadImageFromUrl(imageView, url, CommonUtils.T500);
    }
}
