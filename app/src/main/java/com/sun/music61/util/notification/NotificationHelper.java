package com.sun.music61.util.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.sun.music61.R;
import com.sun.music61.screen.service.PlayTrackService;

public class NotificationHelper {

    private static final String TAG = NotificationHelper.class.getName();
    protected static final String NOTIFICATION_CHANNEL_ID = "sun_notify_channel";
    protected static final int NOTIFICATION_ID = 160;

    protected NotificationManager mNotificationManager;
    protected NotificationChannel mNotificationChannel;

    public NotificationHelper(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel existingNotificationChannel = mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            if (existingNotificationChannel == null) {
                mNotificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        context.getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_LOW);
                mNotificationChannel.enableLights(false);
                mNotificationChannel.enableVibration(false);
                mNotificationChannel.setSound(null,null);
                mNotificationManager.createNotificationChannel(mNotificationChannel);
            }
        }
    }

    public void notify(int notificationId, Notification notification) {
        try {
            mNotificationManager.notify(notificationId, notification);
        }  catch (RuntimeException e) {
            Log.e(TAG, "Error posting notification: ", e);
        }
    }

    public void cancel(int notificationId) {
        mNotificationManager.cancel(notificationId);
    }
}
