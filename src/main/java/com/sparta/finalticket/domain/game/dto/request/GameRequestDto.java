package com.sparta.finalticket.domain.game.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRequestDto {

    private String name;
    private String category;
    private int count;
    private LocalDateTime startDate;
    private String place;
    private List<Long> seatSettingIds;

}
