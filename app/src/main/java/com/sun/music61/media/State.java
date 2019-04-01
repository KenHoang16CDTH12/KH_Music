package com.sun.music61.media;

import android.support.annotation.IntDef;

@IntDef({State.PLAY, State.PAUSE})
public @interface State {
    int PLAY = 0;
    int PAUSE = 1;
}
