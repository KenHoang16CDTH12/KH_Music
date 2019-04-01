package com.sun.music61.custom;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

public class CubeInRotationTransformation implements ViewPager.PageTransformer {

    private static final int CAMERA_DISTANCE = 20000;
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int NINETY = 90;

    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setCameraDistance(CAMERA_DISTANCE);
        if (position < -ONE) {     // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(ZERO);
        } else if (position <= ZERO) {    // [-1,0]
            page.setAlpha(ONE);
            page.setPivotX(page.getWidth());
            page.setRotationY(NINETY * Math.abs(position));
        } else if (position <= ONE) {    // (0,1]
            page.setAlpha(ONE);
            page.setPivotX(ZERO);
            page.setRotationY(-NINETY * Math.abs(position));
        } else {    // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(ZERO);
        }
    }
}
