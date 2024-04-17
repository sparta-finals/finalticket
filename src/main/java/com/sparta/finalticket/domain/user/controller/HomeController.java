package com.sparta.finalticket.domain.user.controller;

import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home(HttpServletRequest request, Model model) {
		model.addAttribute("user",(User) request.getAttribute("user"));
		return "index";
	}
}