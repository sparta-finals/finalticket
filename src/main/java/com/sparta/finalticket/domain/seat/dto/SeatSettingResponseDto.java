package com.sparta.finalticket.domain.seat.dto;

import com.sparta.finalticket.domain.seatsetting.entity.SeatSetting;
import com.sparta.finalticket.domain.seatsetting.entity.SeatTypeEnum;
import lombok.Getter;

@Getter
public class SeatSettingResponseDto {

  private Long id;
  private String seatNumber;
  private SeatTypeEnum seatType;
  private int price;

  public SeatSettingResponseDto(SeatSetting setting){
    this.id = setting.getId();
    this.seatNumber = setting.getSeatNumber();
    this.seatType = setting.getSeatType();
    this.price = setting.getPrice();
  }

}
