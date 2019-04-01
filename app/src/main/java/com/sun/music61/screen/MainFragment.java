package com.sun.music61.screen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.sun.music61.R;
import com.sun.music61.screen.home.HomeFragment;
import com.sun.music61.screen.offline.OfflineFragment;
import com.sun.music61.util.ActivityUtils;
import com.sun.music61.util.CommonUtils;
import com.sun.music61.util.PermissionUtils;
import java.util.Objects;

public class MainFragment extends Fragment {

    private BottomNavigationView mMenuBottom;
    private ConstraintLayout mChildLayout;

    public static Fragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onEventListener();
        if (CommonUtils.sFirstLoad) {
            mMenuBottom.setSelectedItemId(R.id.actionHome);
            CommonUtils.sFirstLoad = false;
        }
    }

    private void initView(View rootView) {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.sub_title_slogan);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        mMenuBottom = rootView.findViewById(R.id.menuBottom);
        mChildLayout = rootView.findViewById(R.id.childLayout);
    }

    private void onEventListener() {
        mMenuBottom.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        switch (menuItem.getItemId()) {
            case R.id.actionHome:
                ActivityUtils.replaceFragmentToActivity(getChildFragmentManager(),
                        HomeFragment.newInstance(), R.id.contentFrame);
                break;
            case R.id.actionMySong:
                PermissionUtils.checkReadExternalPermission(getActivity(), success -> {
                    if (success) {
                        ActivityUtils.replaceFragmentToActivity(getChildFragmentManager(),
                                OfflineFragment.newInstance(), R.id.contentFrame);
                    }
                    else Snackbar.make(mChildLayout, R.string.text_rejected_msg, Snackbar.LENGTH_SHORT).show();
                });
                break;
            case R.id.actionFavorite:
            case R.id.actionSetting:
                Toast.makeText(getContext(), getString(R.string.text_coming_soon),
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }
}
