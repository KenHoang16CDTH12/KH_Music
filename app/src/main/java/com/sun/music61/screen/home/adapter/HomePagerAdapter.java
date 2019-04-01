package com.sun.music61.screen.home.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.sun.music61.screen.home.AllSongsFragment;
import com.sun.music61.screen.home.GenresFragment;

import static com.sun.music61.util.CommonUtils.Genres;
import static com.sun.music61.util.CommonUtils.TitleFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGE = 6;

    private interface Page {
        int ALL_SONG = 0;
        int ALL_AUDIO = 1;
        int ALTERNATIVE_ROCK = 2;
        int AMBIENT = 3;
        int CLASSICAL = 4;
        int COUNTRY = 5;
    }

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Page.ALL_SONG:
                return AllSongsFragment.newInstance();
            case Page.ALL_AUDIO:
                return GenresFragment.newInstance(Genres.ALL_AUDIO);
            case Page.ALTERNATIVE_ROCK:
                return GenresFragment.newInstance(Genres.ALTERNATIVE_ROCK);
            case Page.AMBIENT:
                return GenresFragment.newInstance(Genres.AMBIENT);
            case Page.CLASSICAL:
                return GenresFragment.newInstance(Genres.CLASSICAL);
            case Page.COUNTRY:
                return GenresFragment.newInstance(Genres.COUNTRY);
            default:
                return AllSongsFragment.newInstance();
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
            case Page.ALL_SONG:
                return TitleFragment.ALL;
            case Page.ALL_AUDIO:
                return TitleFragment.AUDIO;
            case Page.ALTERNATIVE_ROCK:
                return TitleFragment.ALTERNATIVE_ROCK;
            case Page.AMBIENT:
                return TitleFragment.AMBIENT;
            case Page.CLASSICAL:
                return TitleFragment.CLASSICAL;
            case Page.COUNTRY:
                return TitleFragment.COUNTRY;
            default:
                return TitleFragment.ALL;
        }
    }
}
