package com.sun.music61.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.sun.music61.BuildConfig;
import com.sun.music61.R;
import com.sun.music61.util.helpers.ImageFactory;
import com.sun.music61.util.listener.FetchImageCallBack;
import java.util.concurrent.TimeUnit;

public class CommonUtils {

    public static final String URL_SERVER = "http://api.soundcloud.com";
    public static final String AUTHORIZED_SERVER = "?" + CommonUtils.KeyParams.CLIENT_ID + BuildConfig.API_KEY;
    public static final String API_TRACKS = CommonUtils.URL_SERVER
            + CommonUtils.APIReference.TRACKS + AUTHORIZED_SERVER + "&";
    /**
     * API SoundCloud return:
     * JPEG, PNG and GIF are  will be encoded to multiple JPEGs in these formats:
     * large: 100×100 (default)
     * t300x300: 300×300
     * t500x500: 500×500
     */
    public static final String LARGE = "large";
    public static final String T500 = "t500x500";
    public static final String T300 = "t300x300";

    public static boolean sFirstLoad = true;

    public interface Number {
        int ZERO = 0;
        int ONE = 1;
        int TWO = 2;
        int TEN = 10;
        int SIXTY = 60;
        int HUNDRED = 100;
        int THOUSAND = 1000;
    }

    public interface Action {
        String ACTION_PLAY_AND_PAUSE = "com.sun.music61.ACTION_PLAY_AND_PAUSE";
        String ACTION_NEXT = "com.sun.music61.ACTION_NEXT";
        String ACTION_PREVIOUS = "com.sun.music61.ACTION_PREVIOUS";
        String ACTION_FAVORITE = "com.sun.music61.ACTION_FAVORITE";
    }

    public interface Constants {
        String TAG_SONG = "songs";
        String ORDER = "created_at";
        int LIMIT_BANNER = 5;
        int LIMIT_DEFAULT = 20;
        int DEFAULT_OFFSET = 1;
    }

    public interface APIReference {
        String TRACKS = "/tracks";
    }

    public interface KeyParams {
        String CLIENT_ID = "client_id=";
        String TAGS = "tags=";
        String LIMIT = "limit=";
        String OFFSET = "offset=";
        String ORDER = "order=";
        String GENRES = "genres=";
    }

    public interface Genres {
        String ALL_MUSIC = "music";
        String ALL_AUDIO = "audio";
        String ALTERNATIVE_ROCK = "alternativerock";
        String AMBIENT = "ambient";
        String CLASSICAL = "classical";
        String COUNTRY = "country";
    }

    public interface TitleFragment {
        String ALL = "All";
        String AUDIO = "Audio";
        String ALTERNATIVE_ROCK = "Rock";
        String AMBIENT = "Ambient";
        String CLASSICAL = "Classical";
        String COUNTRY = "Country";
        String DOWNLOAD = "Download";
        String OTHER = "Other";
    }

    public interface Font {
        String ARKHIP = "fonts/arkhip.ttf";
        String NABILA = "fonts/nabila.ttf";
    }

    public static <T> boolean checkNotNull(T reference) {
        return reference != null && !reference.equals("") && !reference.equals("null");
    }

    public static void loadImageFromUrl(ImageView image, String url, String type) {
        ImageFactory.Builder()
                .url(url)
                .type(type)
                .width(image.getMeasuredWidth())
                .height(image.getMeasuredHeight())
                .onListener(new FetchImageCallBack() {
                    @Override
                    public void onCompleted(Bitmap bitmap) {
                        image.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFailure() {
                        image.setImageResource(R.drawable.ic_filter_hdr_black_124dp);
                    }
                })
                .build();
    }

    private static final String TIME_FORMAT = "%02d:%02d";

    @SuppressLint("DefaultLocale")
    public static String convertTimeInMilisToString(long duration) {
        return String.format(TIME_FORMAT,
                TimeUnit.MILLISECONDS.toMinutes(duration) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public static int progressPercentage(long currentDuration, long totalDuration) {
        Double percentage;
        long currentSeconds  = (int) (currentDuration / Number.THOUSAND);
        long totalSeconds = (int) (totalDuration / Number.THOUSAND);
        //calculating percentage
        percentage = (((double)currentSeconds) / totalSeconds) * Number.HUNDRED;
        //return percentage
        return percentage.intValue();
    }

    public static int progressToTimer(float progress, long totalDuration) {
        int currentDuration;
        totalDuration = totalDuration / Number.THOUSAND;
        currentDuration = (int) (progress / Number.HUNDRED  * totalDuration);
        //return current duration in milliseconds
        return currentDuration * Number.THOUSAND;
    }
}
