package com.sparta.finalticket.domain.user.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.finalticket.domain.user.dto.request.InfoRequestDto;
import com.sparta.finalticket.domain.user.dto.request.LoginRequestDto;
import com.sparta.finalticket.domain.user.dto.request.SignupRequestDto;
import com.sparta.finalticket.domain.user.dto.response.InfoResponseDto;
import com.sparta.finalticket.domain.user.entity.QUser;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.entity.UserRoleEnum;
import com.sparta.finalticket.domain.user.repository.UserRepository;
import com.sparta.finalticket.global.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final SessionUtil sessionUtil;
  private final JPAQueryFactory jpaQueryFactory;

  // ADMIN_TOKEN
  private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

  public void signup(SignupRequestDto requestDto) {
    String username = requestDto.getUsername();
    String password = passwordEncoder.encode(requestDto.getPassword());

    // 회원 중복 확인
    Optional<User> checkUsername = userRepository.findByUsername(username);
    if (checkUsername.isPresent()) {
      throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
    }

    // email 중복확인
    String email = requestDto.getEmail();
    Optional<User> checkEmail = userRepository.findByEmail(email);
    if (checkEmail.isPresent()) {
      throw new IllegalArgumentException("중복된 Email 입니다.");
    }

    String nickname = requestDto.getNickname();
    Optional<User> checkNickname = userRepository.findByNickname(nickname);
    if (checkNickname.isPresent()) {
      throw new IllegalArgumentException("중복된 Nickname 입니다.");
    }

    // 사용자 ROLE 확인
    UserRoleEnum role = UserRoleEnum.USER;
    if (requestDto.isAdmin()) {
      if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
        throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
      }
      role = UserRoleEnum.ADMIN;
    }

    // 사용자 등록
    User user = new User(requestDto, password, role);
    userRepository.save(user);
  }

  public void login(LoginRequestDto requestDto, HttpServletResponse response) {
    QUser qUser = QUser.user;
    User user = jpaQueryFactory.selectFrom(qUser).
        where(qUser.username.eq(requestDto.getUsername())
            .and(qUser.state.eq(true))).fetchOne();
    if (user!=null && passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
      sessionUtil.addSessionKey(response, user);
    }else{
      throw new IllegalArgumentException("일치하는 유저가 없습니다.");
    }
  }

  public void logout(HttpServletRequest request) {
    sessionUtil.logout(request);
  }

  @Transactional
  public void modifyInfo(User user, InfoRequestDto requestDto) {
    requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
    User modifyUser = new User(requestDto, user.getRole());
    QUser qUser = QUser.user;
    jpaQueryFactory.update(qUser).
        where(qUser.id.eq(user.getId())).
        set(qUser.username, requestDto.getUsername()).
        set(qUser.password, requestDto.getPassword()).
        set(qUser.email, requestDto.getEmail()).
        set(qUser.nickname, requestDto.getNickname()).
        set(qUser.address, requestDto.getAddress()).
        execute();
  }

  @Transactional
  public void withdrawal(User user,HttpServletRequest request) {
    QUser qUser = QUser.user;
    jpaQueryFactory.update(qUser).
        where(qUser.id.eq(user.getId())).
        set(qUser.state, false).
        execute();
    logout(request);
  }
}
