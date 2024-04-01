package com.sparta.finalticket.domain.game.service;

import com.sparta.finalticket.domain.game.dto.request.GameRequestDto;
import com.sparta.finalticket.domain.game.dto.response.GameResponseDto;
import com.sparta.finalticket.domain.game.entity.CategoryEnum;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.entity.PlaceEnum;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.entity.UserRoleEnum;
import com.sparta.finalticket.domain.user.repository.UserRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public GameResponseDto createGame(GameRequestDto gameRequestDto, Long userId) {
        User user = validateCheckAdmin(userId);

        Game game = gameRepository.save(
                Game.builder()
                        .name(gameRequestDto.getName())
                        .place(PlaceEnum.valueOf(gameRequestDto.getPlace()))
                        .category(CategoryEnum.valueOf(gameRequestDto.getCategory()))
                        .count(gameRequestDto.getCount())
                        .startDate(gameRequestDto.getStartDate())
                        .user(user)
                        .build());


        return GameResponseDto.builder()
                .id(game.getId())
                .name(game.getName())
                .count(game.getCount())
                .place(String.valueOf(game.getPlace()))
                .category(String.valueOf(game.getCategory()))
                .startDate(game.getStartDate())
                .build();
    }

    public GameResponseDto updateGame(GameRequestDto gameRequestDto, Long userId, Long gameId) {
        User user = validateCheckAdmin(userId);
        Game game = validateExistGame(gameId);

        game.setName(gameRequestDto.getName());
        game.setCategory(CategoryEnum.valueOf(gameRequestDto.getCategory()));
        game.setCount(gameRequestDto.getCount());
        game.setStartDate(gameRequestDto.getStartDate());
        game.setPlace(PlaceEnum.valueOf(gameRequestDto.getPlace()));

        return GameResponseDto.builder()
                .id(gameId)
                .name(game.getName())
                .category(String.valueOf(game.getCategory()))
                .count(game.getCount())
                .startDate(game.getStartDate())
                .place(String.valueOf(game.getPlace()))
                .build();
    }

    public void deleteGame(Long gameId, Long userId) {
        User user = validateCheckAdmin(userId);
        Game game = validateExistGame(gameId);

        game.deleteGame();
        gameRepository.deleteGameAndRelateEntities(gameId);
    }


    @Transactional(readOnly = true)
    public List<GameResponseDto> getAllGameList() {
        return gameRepository.findAll().stream()
                .map(GameResponseDto::new).toList();
    }

    @Transactional(readOnly = true)
    public List<GameResponseDto> getGameList(Long gameId) {
        Game game = validateExistGame(gameId);

        return gameRepository.findById(gameId).stream()
                .map(GameResponseDto::new).toList();
    }


    private User validateCheckAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 입니다."));
        if (user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException("관리자만 경기를 등록,수정,삭제할 수 있습니다.");
        }
        return user;
    }

    private Game validateExistGame(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow(
                () -> new IllegalArgumentException("해당 게임을 찾을 수 없습니다.")
        );
    }
}
