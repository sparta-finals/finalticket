package com.sparta.finalticket.domain.user.repository;

import com.sparta.finalticket.domain.user.entity.User;

public interface UserRepositoryCustom {

    void modifyUserInfo(User user);

    void withdrawal(User user);

    User findUser(String username);
}
