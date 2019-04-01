package com.sun.music61.screen;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.sun.music61.R;
import com.sun.music61.data.model.Track;
import com.sun.music61.screen.play.PlayFragment;
import com.sun.music61.screen.service.PlayTrackListener;
import com.sun.music61.screen.service.PlayTrackService;
import com.sun.music61.util.ActivityUtils;
import com.sun.music61.util.PermissionUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sun.music61.util.CommonUtils.Font;

public class MainActivity extends AppCompatActivity {

    private ServiceConnection mConnection;
    private PlayTrackService mService;

    public static Intent newInstance(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public static void replaceFragment(@NonNull AppCompatActivity activity, Fragment fragment) {
        ActivityUtils.replaceFragmentToActivity(activity.getSupportFragmentManager(), fragment,
                R.id.contentMain);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Notes : add this code before setContentView
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder().setDefaultFontPath(Font.ARKHIP)
                        .setFontAttrId(R.attr.fontPath)
                        .build());
        setContentView(R.layout.main_activity);
        initServiceConnection();
    }

    private void initServiceConnection() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayTrackService.TrackBinder trackBinder = (PlayTrackService.TrackBinder) service;
                mService = trackBinder.getService();
                // Default Fragment
                ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), MainFragment.newInstance(),
                        R.id.contentMain);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(PlayTrackService.getIntent(this), mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        PermissionUtils.onActivityResult(this, requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeService();
    }

    private void removeService() {
        unbindService(mConnection);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public PlayTrackService getService() {
        return mService;
    }
}
