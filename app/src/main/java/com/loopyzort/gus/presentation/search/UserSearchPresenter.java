package com.loopyzort.gus.presentation.search;

import com.loopyzort.gus.data.UserRepository;
import com.loopyzort.gus.data.remote.model.User;
import com.loopyzort.gus.presentation.base.BasePresenter;
import com.loopyzort.gus.presentation.search.UserSearchContract.View;

import java.util.List;

import rx.Scheduler;
import rx.Subscriber;

public class UserSearchPresenter extends BasePresenter<View>
        implements UserSearchContract.Presenter {
    private final Scheduler mainScheduler, ioScheduler;
    private UserRepository userRepository;

    public UserSearchPresenter(UserRepository userRepository, Scheduler ioScheduler,
            Scheduler mainScheduler) {
        this.mainScheduler = mainScheduler;
        this.ioScheduler = ioScheduler;
        this.userRepository = userRepository;
    }

    @Override
    public void search(String term) {
        checkViewAttached();
        getView().showLoading();
        addSubscription(userRepository.searchUsers(term)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(

                        new Subscriber<List<User>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                getView().hideLoading();
                                getView().showError(e.getMessage());

                            }

                            @Override
                            public void onNext(List<User> users) {
                                getView().hideLoading();
                                getView().showSearchResults(users);

                            }
                        }));
    }
}
