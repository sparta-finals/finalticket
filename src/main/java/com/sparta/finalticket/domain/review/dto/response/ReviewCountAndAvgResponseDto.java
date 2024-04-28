package com.sparta.finalticket.domain.review.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
public class ReviewCountAndAvgResponseDto {

  private String avg;
  private String count;


  public ReviewCountAndAvgResponseDto(Double avg, Long count) {
    this.avg = avg.toString();
    this.count = count.toString();
  }
}