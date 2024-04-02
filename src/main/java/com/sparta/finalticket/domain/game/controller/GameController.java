package com.sparta.finalticket.domain.game.controller;

import com.sparta.finalticket.domain.game.dto.request.GameRequestDto;
import com.sparta.finalticket.domain.game.dto.response.GameResponseDto;
import com.sparta.finalticket.domain.game.service.GameService;
import com.sparta.finalticket.domain.user.dto.response.CommonResponse;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/games")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<CommonResponse<GameResponseDto>> createGame(
            @Valid @RequestBody GameRequestDto gameRequestDto, HttpServletRequest request
    ) {
        User user = (User) request.getAttribute("user");
        GameResponseDto gameResponseDto = gameService.createGame(gameRequestDto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.<GameResponseDto>builder()
                        .data(gameResponseDto).build()
        );
    }

    @PutMapping("{id}")
    public ResponseEntity<CommonResponse<GameResponseDto>> updateGame(@PathVariable Long id,
                                                                      @RequestBody GameRequestDto gameRequestDto, HttpServletRequest request) {

        User user = (User) request.getAttribute("user");
        GameResponseDto gameResponseDto = gameService.updateGame(gameRequestDto, user.getId(),
                id);

        return ResponseEntity.ok(CommonResponse.<GameResponseDto>builder()
                .data(gameResponseDto)
                .build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<CommonResponse<String>> deleteGame(@PathVariable Long id,
                                                             HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        gameService.deleteGame(id, user.getId());
        return ResponseEntity.ok(
                CommonResponse.<String>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data("삭제가 왼료되었습니다.")
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<GameResponseDto>>> getAllGameList() {
        List<GameResponseDto> responseList = gameService.getAllGameList();

        return ResponseEntity.status(HttpStatus.OK.value()).body(
                CommonResponse.<List<GameResponseDto>>builder()
                        .data(responseList)
                        .build()
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<CommonResponse<List<GameResponseDto>>> getGameList(@PathVariable Long id) {
        List<GameResponseDto> responseList = gameService.getGameList(id);

        return ResponseEntity.status(HttpStatus.OK.value()).body(
                CommonResponse.<List<GameResponseDto>>builder()
                        .data(responseList)
                        .build()
        );
    }

    //예매예정경기 전체 조회
    @GetMapping("/upcoming")
    public ResponseEntity<List<GameResponseDto>> getUpcomingGame () {
        List<GameResponseDto> GameResponseDtos = gameService.getUpcomingGame();
        return ResponseEntity.status(200).body(GameResponseDtos);
    }

    //예매가능경기 전체 조회
    @GetMapping("/available")
    public ResponseEntity<List<GameResponseDto>> getAvailableGame () {
        List<GameResponseDto> GameResponseDtos = gameService.getAvailableGame();
        return ResponseEntity.status(200).body(GameResponseDtos);
    }
}
