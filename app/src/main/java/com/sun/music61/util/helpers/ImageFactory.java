package com.sun.music61.util.helpers;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.sun.music61.util.BitmapUtils;
import com.sun.music61.util.CommonUtils;
import com.sun.music61.util.listener.FetchImageCallBack;

public class ImageFactory extends AsyncTask<Void, Void, Bitmap> {

    private static final String ANDROID_RESOURCE = "android.resource";
    private static final String ANDROID_PROVIDERS = "android.providers";

    private String mUrl;
    private int mWidth;
    private int mHeight;
    private FetchImageCallBack mCallBack;

    public static Builder Builder() {
        return new Builder();
    }

    private ImageFactory(final Builder builder) {
        mUrl = builder.getUrl();
        mWidth = builder.getWidth();
        mHeight = builder.getHeight();
        mCallBack = builder.getCallback();
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        if (mUrl == null) return null;
        if (mUrl.contains(ANDROID_RESOURCE) || mUrl.contains(ANDROID_PROVIDERS))
            return BitmapUtils.getBitmapFromAssets(mUrl, mWidth, mHeight);
        return BitmapUtils.getBitmapFromURL(mUrl, mWidth, mHeight);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            mCallBack.onCompleted(result);
        } else {
            mCallBack.onFailure();
        }
    }

    public static class Builder {
        private String mUrl;
        private int mWidth;
        private int mHeight;
        private FetchImageCallBack mCallback;

        public Builder url(String urlImg) {
            mUrl = urlImg;
            return this;
        }

        public Builder type(String imageType) {
            switch (imageType) {
                case CommonUtils.T300:
                    mUrl = mUrl.replace(CommonUtils.LARGE, CommonUtils.T300);
                    break;
                case CommonUtils.T500:
                    mUrl = mUrl.replace(CommonUtils.LARGE, CommonUtils.T500);
                    break;
                case CommonUtils.LARGE:
                default:
                    // do nothing
                    break;
            }
            return this;
        }

        public Builder width(int width) {
            mWidth = width;
            return this;
        }

        public Builder height(int height) {
            mHeight = height;
            return this;
        }

        public Builder onListener(FetchImageCallBack callback) {
            mCallback = callback;
            return this;
        }

        public void build() {
            if (mUrl.isEmpty())
                throw new IllegalStateException();
            if (mWidth < 0 || mHeight < 0)
                throw new ArithmeticException();
            if (mCallback == null)
                throw new NullPointerException();
            new ImageFactory(this).execute();
        }

        private String getUrl() {
            return mUrl;
        }

        private int getWidth() {
            return mWidth;
        }

        private int getHeight() {
            return mHeight;
        }

        private FetchImageCallBack getCallback() {
            return mCallback;
        }
    }
}
