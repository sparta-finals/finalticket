package com.sparta.finalticket.domain.user.service;

import com.sparta.finalticket.domain.user.dto.request.LoginRequestDto;
import com.sparta.finalticket.domain.user.dto.request.SignupRequestDto;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.entity.UserRoleEnum;
import com.sparta.finalticket.domain.user.repository.UserRepository;
import com.sparta.finalticket.global.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final SessionUtil sessionUtil;

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
		User user = userRepository.findByUsername(requestDto.getUsername())
			.orElseThrow(() -> new IllegalArgumentException("일치하는 유저가 없습니다."));
		if (passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			sessionUtil.addSessionKey(response, user);
		}
	}

	public void logout(HttpServletRequest request) {
		sessionUtil.logout(request);
	}
}
