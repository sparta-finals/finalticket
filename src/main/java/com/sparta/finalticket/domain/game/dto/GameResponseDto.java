package com.sparta.finalticket.domain.game.dto;

import com.sparta.finalticket.domain.game.entity.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameResponseDto {

    private String name;

    private CategoryEnum category;
}
