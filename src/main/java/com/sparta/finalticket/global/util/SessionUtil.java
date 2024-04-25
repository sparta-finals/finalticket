package com.sparta.finalticket.global.util;

import com.google.gson.Gson;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionUtil {

	private final RedisTemplate<String, String> redisTemplate;
	private final PasswordEncoder encoder;
	private final Gson gson;

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String LOGIN_PREFIX = "LOGIN: ";

	public void addSessionKey(HttpServletResponse response, User user) {
		log.info("addSessionKey");
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		String username = user.getUsername();
		String sessionKey = encoder.encode(user.getId().toString());
		Set<String> keySet = redisTemplate.keys(LOGIN_PREFIX+"*");
		String jsonStringUser = gson.toJson(user);
    for (String key : keySet) {
			if (valueOperations.get(key).equals(jsonStringUser)) {
				redisTemplate.delete(key);
			}
		}
		valueOperations.set(LOGIN_PREFIX+sessionKey, jsonStringUser);
		Cookie cookie = new Cookie(AUTHORIZATION_HEADER, sessionKey);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public boolean isLoggedIn(HttpServletRequest request) {
		log.info("LoggedIn 인가 확인 진행");
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
					String sessionKey = cookie.getValue();
					User user = gson.fromJson(valueOperations.get(LOGIN_PREFIX+sessionKey), User.class);
					request.setAttribute("user",user);
					return true;
				}
			}
		}
		return false;
	}

	public boolean logout(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
					String sessionKey = cookie.getValue();
					log.info(sessionKey);
					cookie.setMaxAge(0);
					cookie.setPath("/");
					response.addCookie(cookie);
					redisTemplate.delete(LOGIN_PREFIX+sessionKey);
					log.info("Logout success");
					return true;
				}
			}
		}
		return false;
	}
}
