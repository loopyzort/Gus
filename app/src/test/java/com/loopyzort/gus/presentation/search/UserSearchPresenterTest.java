package com.loopyzort.gus.presentation.search;

import com.loopyzort.gus.data.UserRepository;
import com.loopyzort.gus.data.remote.model.User;
import com.loopyzort.gus.data.remote.model.UsersList;
import com.loopyzort.gus.presentation.base.BasePresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.*;

public class UserSearchPresenterTest {
    private static final String USER_LOGIN_LOOPYZORT = "loopyzort";
    private static final String USER_LOGIN_2_REBECCA = "rebecca";

    @Mock UserRepository userRepository;
    @Mock UserSearchContract.View view;
    UserSearchPresenter userSearchPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userSearchPresenter = new UserSearchPresenter(userRepository, Schedulers.immediate(),
                Schedulers.immediate());
        userSearchPresenter.attachView(view);
    }

    @Test
    public void search_ValidSearchTerm_ReturnsResults() {
        UsersList userList = getDummyUserList();
        when(userRepository.searchUsers(anyString())).thenReturn(
                Observable.just(userList.getItems()));

        userSearchPresenter.search("loopyzort");

        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view).showSearchResults(userList.getItems());
        verify(view, never()).showError(anyString());
    }

    @Test
    public void search_UserRepositoryError_ErrorMsg() {
        String errorMsg = "No internet";
        when(userRepository.searchUsers(anyString())).thenReturn(Observable.error(new IOException(errorMsg)));

        userSearchPresenter.search("errorTerm");

        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view, never()).showSearchResults(anyList());
        verify(view).showError(errorMsg);
    }

    @Test(expected = BasePresenter.MvpViewNotAttachedException.class)
    public void search_NotAttached_ThrowsMvpException() {
        userSearchPresenter.detachView();

        userSearchPresenter.search("something");

        verify(view, never()).showLoading();
        verify(view, never()).showSearchResults(anyList());
    }

    UsersList getDummyUserList() {
        List<User> githubUsers = new ArrayList<>();
        githubUsers.add(user1FullDetails());
        githubUsers.add(user2FullDetails());
        return new UsersList(githubUsers);
    }

    User user1FullDetails() {
        return new User(USER_LOGIN_LOOPYZORT, "Loopy Zort", "avatar_url", "Bio1");
    }

    User user2FullDetails() {
        return new User(USER_LOGIN_2_REBECCA, "Rebecca Franks", "avatar_url2", "Bio2");
    }
}