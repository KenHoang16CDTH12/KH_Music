package com.sun.music61.screen.offline.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.sun.music61.data.source.local.TracksLocalFactory;
import com.sun.music61.screen.offline.OfflinePageFragment;

import static com.sun.music61.util.CommonUtils.TitleFragment;

public class OfflinePagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGE = 3;

    private interface Page {
        int ALL = 0;
        int DOWNLOAD = 1;
        int OTHER = 2;
    }

    public OfflinePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Page.ALL:
                return OfflinePageFragment.newInstance(TracksLocalFactory.Type.TRACK_ALL);
            case Page.DOWNLOAD:
                return OfflinePageFragment.newInstance(TracksLocalFactory.Type.TRACK_DOWNLOAD);
            case Page.OTHER:
                return OfflinePageFragment.newInstance(TracksLocalFactory.Type.TRACK_OTHER);
            default:
                return OfflinePageFragment.newInstance(TracksLocalFactory.Type.TRACK_ALL);
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case Page.ALL:
                return TitleFragment.ALL;
            case Page.DOWNLOAD:
                return TitleFragment.DOWNLOAD;
            case Page.OTHER:
                return TitleFragment.OTHER;
            default:
                return TitleFragment.ALL;
        }
    }
}
