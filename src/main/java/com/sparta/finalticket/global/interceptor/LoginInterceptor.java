package com.sparta.finalticket.global.interceptor;

import com.sparta.finalticket.global.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

	private final SessionUtil sessionUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
		Object handler) throws Exception {//true controller false 처리안함
		log.info("LoginInterceptor.preHandle");
		if (sessionUtil.isLoggedIn(request)) {
			return true;
		}
		response.sendRedirect("/v1/users/login-page");
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
		ModelAndView modelAndView) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

}
