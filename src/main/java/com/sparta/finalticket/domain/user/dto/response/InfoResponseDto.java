package com.sparta.finalticket.domain.user.dto.response;

import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.entity.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfoResponseDto {

  private String username;
  private String email;
  private String address;
  private String nickname;
  private UserRoleEnum role;

  public InfoResponseDto(User user) {
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.address = user.getAddress();
    this.nickname = user.getNickname();
    this.role = user.getRole();
  }

}
