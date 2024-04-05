package com.sparta.finalticket.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.finalticket.domain.user.entity.QUser;
import com.sparta.finalticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void modifyUserInfo(User user) {
        jpaQueryFactory.update(QUser.user).
            where(QUser.user.id.eq(user.getId())).
            set(QUser.user.username, user.getUsername()).
            set(QUser.user.password, user.getPassword()).
            set(QUser.user.email, user.getEmail()).
            set(QUser.user.nickname, user.getNickname()).
            set(QUser.user.address, user.getAddress()).
            set(QUser.user.role, user.getRole()).
            execute();
    }

    @Override
    public void withdrawal(User user) {
        jpaQueryFactory.update(QUser.user).
            where(QUser.user.id.eq(user.getId())).
            set(QUser.user.state, false).
            execute();
    }

    @Override
    public User findUser(String username) {
        return jpaQueryFactory.selectFrom(QUser.user).
            where(QUser.user.username.eq(username)).fetchOne();
    }
}
