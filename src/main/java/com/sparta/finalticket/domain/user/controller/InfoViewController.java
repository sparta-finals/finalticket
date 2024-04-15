package com.sparta.finalticket.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/users/info/view")
public class InfoViewController {
  @GetMapping
  public String infoPage(){
    return "info-page";
  }

  @GetMapping("/edit")
  public String infoViewPage(){
    return "info-edit-page";
  }

  @GetMapping("/ticket")
  public String infoTicket(){
    return "ticket";
  }

  @GetMapping("/review")
  public String infoReview(){
    return "my-review";
  }

  @GetMapping("/game")
  public String infoGame(){
    return "my-game";
  }

}
