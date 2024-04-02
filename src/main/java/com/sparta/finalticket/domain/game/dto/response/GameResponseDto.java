package com.sparta.finalticket.domain.game.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.finalticket.domain.game.entity.CategoryEnum;
import com.sparta.finalticket.domain.game.entity.Game;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameResponseDto {

    private Long id;

    private String name;

    private int count;

    private LocalDateTime startDate;

    private String place;

    private String category;

    public GameResponseDto(Game game) {
        this.id = game.getId();
        this.name = game.getName();
        this.count = game.getCount();
        this.startDate = game.getStartDate();
        this.place = String.valueOf(game.getPlace());
        this.category = String.valueOf(game.getCategory());
    }

    public GameResponseDto(String name, CategoryEnum category) {
        this.name = name;
        this.category = String.valueOf(category);
    }
}
