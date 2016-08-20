package com.loopyzort.gus.data;

import com.loopyzort.gus.data.remote.model.User;

import java.util.List;

import rx.Observable;

public interface UserRepository {
    Observable<List<User>> searchUsers(final String searchTerm);
}
