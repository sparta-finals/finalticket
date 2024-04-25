package com.sparta.finalticket.domain.user.controller;

import com.sparta.finalticket.domain.game.dto.response.GameResponseDto;
import com.sparta.finalticket.domain.game.service.GameService;
import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.service.ReviewService;
import com.sparta.finalticket.domain.ticket.dto.TicketResponseDto;
import com.sparta.finalticket.domain.ticket.service.TicketService;
import com.sparta.finalticket.domain.user.dto.request.UserRequestDto;
import com.sparta.finalticket.domain.user.dto.response.InfoResponseDto;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
  private final TicketService ticketService;
  private final GameService gameService;
  private final ReviewService reviewService;

  @GetMapping("/info")
  public ResponseEntity<InfoResponseDto> getInfo(HttpServletRequest request) {
    User user = (User) request.getAttribute("user");
    return new ResponseEntity<>(new InfoResponseDto(user), HttpStatus.OK);
  }

  @PutMapping("/info")
  public ResponseEntity<String> modifyInfo(HttpServletRequest request,
      @Valid @RequestBody UserRequestDto infoRequestDto,  BindingResult bindingResult) {
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();
    if (fieldErrors.size() > 0) {
      for (FieldError fieldError : bindingResult.getFieldErrors()) {
        log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
        return ResponseEntity.badRequest().body(fieldError.getDefaultMessage());
      }
    }
    try{
      userService.modifyInfo((User) request.getAttribute("user"), infoRequestDto);
    }catch(IllegalArgumentException e){
      return ResponseEntity.badRequest().body(e.getMessage());
    }
    return ResponseEntity.ok().build();
  }

  @GetMapping("/tickets")
  public ResponseEntity<List<TicketResponseDto>> userTicket(HttpServletRequest request){
    return new ResponseEntity<>(ticketService.getUserTicketList((User)request.getAttribute("user")), HttpStatus.OK);
  }

  @GetMapping("/reviews")
  public ResponseEntity<List<ReviewResponseDto>> userReview(HttpServletRequest request){
    return new ResponseEntity<>(reviewService.getUserReviewList((User)request.getAttribute("user")),HttpStatus.OK);
  }

  @GetMapping("/games")
  public ResponseEntity<List<GameResponseDto>> userGame(HttpServletRequest request){
    return new ResponseEntity<>(gameService.getUserGameList((User)request.getAttribute("user")),HttpStatus.OK);
  }

}
