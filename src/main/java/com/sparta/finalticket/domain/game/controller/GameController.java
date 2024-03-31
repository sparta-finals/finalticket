package com.sparta.finalticket.domain.game.controller;

import com.sparta.finalticket.domain.game.dto.GameResponseDto;
import com.sparta.finalticket.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/games")
public class GameController {

    private final GameService gameService;

    //예매예정경기 전체 조회
    @GetMapping("/upcoming")
    public ResponseEntity<List<GameResponseDto>> getUpcomingGame () {
        List<GameResponseDto> GameResponseDtos = gameService.getUpcomingGame();
        return ResponseEntity.status(200).body(GameResponseDtos);
    }

    //예매가능경기 전체 조회
    @GetMapping("/available")
    public ResponseEntity getAvailableGame () {
        List<GameResponseDto> GameResponseDtos = gameService.getAvailableGame();
        return ResponseEntity.status(200).body(GameResponseDtos);
    }
}
