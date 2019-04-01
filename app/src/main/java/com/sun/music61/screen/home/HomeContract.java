package com.sun.music61.screen.home;

import com.sun.music61.BasePresenter;
import com.sun.music61.BaseView;
import com.sun.music61.data.model.Track;

import java.util.List;

public interface HomeContract {

    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
    }
}
