package com.sparta.finalticket.domain.user.controller;

import com.sparta.finalticket.domain.user.dto.request.LoginRequestDto;
import com.sparta.finalticket.domain.user.dto.request.UserRequestDto;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid UserRequestDto requestDto,
        BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
                return ResponseEntity.badRequest().body(fieldError.getDefaultMessage());
            }
        }
        try{
           userService.signup(requestDto);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequestDto requestDto, BindingResult bindingResult,
        HttpServletResponse response) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/v1/users/login-page";
        }
        userService.login(requestDto, response);

        return "redirect:/";
    }

    @DeleteMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
        return "redirect:/v1/users/login-page";
    }

    @DeleteMapping("/withdrawal")
    public String withdrawal(HttpServletRequest request, HttpServletResponse response) {
        userService.withdrawal((User) request.getAttribute("user"), request, response);
        return "redirect:/v1/users/login-page";
    }

}
