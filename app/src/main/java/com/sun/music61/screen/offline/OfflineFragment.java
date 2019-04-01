package com.sun.music61.screen.offline;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.sun.music61.R;
import com.sun.music61.custom.CubeInRotationTransformation;
import com.sun.music61.screen.offline.adapter.OfflinePagerAdapter;
import java.util.Objects;

public class OfflineFragment extends Fragment {

    public static OfflineFragment newInstance() {
        return new OfflineFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.offline_fragment, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        ViewPager viewPager = rootView.findViewById(R.id.viewPager);
        viewPager.setAdapter(new OfflinePagerAdapter(getChildFragmentManager()));
        CubeInRotationTransformation transformation = new CubeInRotationTransformation();
        viewPager.setPageTransformer(true, transformation);
        TabLayout tabLayout = rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Objects.requireNonNull(getActivity())
                .getMenuInflater()
                .inflate(R.menu.option_menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSearch:
                // Code late
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
