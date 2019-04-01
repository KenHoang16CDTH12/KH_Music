package com.sun.music61.media;

import android.support.annotation.IntDef;

@IntDef({Loop.NONE, Loop.ONE, Loop.ALL})
public @interface Loop {
    int NONE = 0;
    int ONE = 1;
    int ALL = 2;
}
