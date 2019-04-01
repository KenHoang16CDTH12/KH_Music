package com.sun.music61.screen.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import com.sun.music61.data.model.Track;
import com.sun.music61.util.CommonUtils;
import java.util.Objects;

public class DownloadTrackService extends IntentService {

    private static final String TAG = DownloadTrackService.class.getName();
    public static final String EXTRA_BUNDLE = "EXTRA_BUNDLE";
    public static final String EXTRA_TRACK = "EXTRA_TRACK";
    private static final String BASE_FILE_PATH = "/SunSound/%s.mp3";

    public DownloadTrackService() {
        super(TAG);
    }

    public static Intent getIntent(Context context, Track track) {
        Intent intent = new Intent(context, DownloadTrackService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(DownloadTrackService.EXTRA_TRACK, track);
        intent.putExtra(DownloadTrackService.EXTRA_BUNDLE, bundle);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getBundleExtra(EXTRA_BUNDLE);
        Track track = bundle.getParcelable(EXTRA_TRACK);
        String urlDownload = Objects.requireNonNull(track).getStreamUrl();
        String path = String.format(BASE_FILE_PATH, track.getTitle().replaceAll("/", "_"));
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlDownload));
        request.setTitle(track.getTitle());
        request.setAllowedOverRoaming(true);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, path);
        downloadManager.enqueue(request);
    }
}
