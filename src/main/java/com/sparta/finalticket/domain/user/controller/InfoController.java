package com.sparta.finalticket.domain.user.controller;

import com.sparta.finalticket.domain.user.dto.request.InfoRequestDto;
import com.sparta.finalticket.domain.user.dto.response.InfoResponseDto;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class InfoController {

  private final UserService userService;

  @GetMapping("/info")
  public ResponseEntity<InfoResponseDto> getInfo(HttpServletRequest request) {
    User user = (User) request.getAttribute("user");
    return new ResponseEntity<>(new InfoResponseDto(user), HttpStatus.OK);
  }

  @PutMapping("/info")
  public ResponseEntity modifyInfo(HttpServletRequest request,
      @Valid @RequestBody InfoRequestDto infoRequestDto) {
    log.info("컨트롤러 진입");
    userService.modifyInfo((User) request.getAttribute("user"), infoRequestDto);
    log.info("서비스 처리 완료");
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
