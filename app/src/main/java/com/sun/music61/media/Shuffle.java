package com.sun.music61.media;

import android.support.annotation.IntDef;

@IntDef({Shuffle.OFF, Shuffle.ON})
public @interface Shuffle {
    int OFF = 0;
    int ON = 1;
}
