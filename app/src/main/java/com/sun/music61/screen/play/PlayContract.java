package com.sun.music61.screen.play;

import com.sun.music61.BasePresenter;
import com.sun.music61.BaseView;

public interface PlayContract {

    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
    }
}
