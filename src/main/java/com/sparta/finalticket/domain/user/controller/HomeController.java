package com.sparta.finalticket.domain.user.controller;

import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home(HttpServletRequest request) {
		User user = (User) request.getAttribute("user");
		System.out.println("user.getUsername() = " + user.getUsername());
		return "index";
	}
}