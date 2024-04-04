package com.sparta.finalticket.domain.user.service;

import com.sparta.finalticket.domain.user.dto.request.LoginRequestDto;
import com.sparta.finalticket.domain.user.dto.request.UserRequestDto;
import com.sparta.finalticket.domain.user.entity.DataConversionEnum;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.entity.UserRoleEnum;
import com.sparta.finalticket.domain.user.repository.UserRepository;
import com.sparta.finalticket.global.util.PasswordEncoder;
import com.sparta.finalticket.global.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionUtil sessionUtil;

    // ADMIN_TOKENsecret
    @Value("${token.admin.key}")
    private String admin_token;

    public void signup(UserRequestDto requestDto) {
        boolean validated = validateUserInfo(requestDto, DataConversionEnum.SIGNUP, null);
        if (validated) {
            // 사용자 등록
            User user = new User(requestDto);
            userRepository.save(user);
        }
    }

    public void login(LoginRequestDto requestDto, HttpServletResponse response) {
        User user = userRepository.findUser(requestDto.getUsername());
        if (user != null && passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            sessionUtil.addSessionKey(response, user);
        } else {
            throw new IllegalArgumentException("일치하는 유저가 없습니다.");
        }
    }

    public void logout(HttpServletRequest request) {
        sessionUtil.logout(request);
    }

    @Transactional
    public void modifyInfo(User user, UserRequestDto requestDto) {
        boolean validated = validateUserInfo(requestDto, DataConversionEnum.INFO, user);
        if (validated) {
            User modifyUser = new User(requestDto, user);
            userRepository.modifyUserInfo(modifyUser);
        }
    }

    @Transactional
    public void withdrawal(User user, HttpServletRequest request) {
        userRepository.withdrawal(user);
        logout(request);
    }

    public boolean validateUserInfo(UserRequestDto requestDto, DataConversionEnum conversionEnum,
        User user) {
        boolean finish = false;

        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        // 회원 중복 확인
        String usernamae = requestDto.getUsername();
        if(conversionEnum.equals(DataConversionEnum.INFO)&&!user.getUsername().equals(usernamae)){
            Optional<User> checkUsername = userRepository.findByUsername(usernamae);
            if (checkUsername.isPresent()) {
                throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
            }
        }



        // email 중복확인
        String email = requestDto.getEmail();
        if(conversionEnum.equals(DataConversionEnum.INFO)&&!user.getEmail().equals(email)){
            Optional<User> checkEmail = userRepository.findByEmail(email);
            if (checkEmail.isPresent()) {
                throw new IllegalArgumentException("중복된 Email 입니다.");
            }
        }

        String nickname = requestDto.getNickname();
        if(conversionEnum.equals(DataConversionEnum.INFO)&&!user.getNickname().equals(nickname)){
            Optional<User> checkNickname = userRepository.findByNickname(nickname);
            if (checkNickname.isPresent()) {
                throw new IllegalArgumentException("중복된 Nickname 입니다.");
            }
        }

        if (conversionEnum.equals(DataConversionEnum.SIGNUP)) {
            // 사용자 ROLE 확인
            if (requestDto.isAdmin()) {
                if (!admin_token.equals(requestDto.getAdminToken())) {
                    throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
                }
                requestDto.setRole(UserRoleEnum.ADMIN);
            } else {
                requestDto.setRole(UserRoleEnum.USER);
            }
            finish = true;
        } else {
            finish = true;
        }
        return finish;
    }

}
