package com.loopyzort.gus.presentation.search;

import com.loopyzort.gus.data.remote.model.User;
import com.loopyzort.gus.presentation.base.MvpPresenter;
import com.loopyzort.gus.presentation.base.MvpView;

import java.util.List;

public interface UserSearchContract {
    interface View extends MvpView {
        void showSearchResults(List<User> githubUserList);

        void showError(String message);

        void showLoading();

        void hideLoading();
    }

    interface Presenter extends MvpPresenter<View> {
        void search(String term);
    }
}
