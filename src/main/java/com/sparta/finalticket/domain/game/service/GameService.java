package com.sparta.finalticket.domain.game.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.finalticket.domain.game.dto.request.GameRequestDto;
import com.sparta.finalticket.domain.game.dto.response.GameResponseDto;
import com.sparta.finalticket.domain.game.entity.CategoryEnum;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.entity.PlaceEnum;
import com.sparta.finalticket.domain.game.entity.QGame;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.entity.UserRoleEnum;
import com.sparta.finalticket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

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
                .state(true)
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
        return gameRepository.findByIdAndStateTrue(gameId).stream()
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
        return gameRepository.findByIdAndStateTrue(gameId).orElseThrow(
            () -> new IllegalArgumentException("해당 게임을 찾을 수 없습니다.")
        );
    }

    public List<GameResponseDto> getUserGameList(User user) {
        return gameRepository.getUserGameList(user);
    }

    //예매예정경기 전체 조회
    public List<GameResponseDto> getUpcomingGame() {
        List<Game> gameList = getGames();
        return filterGames(game -> game.getStartDate().isAfter(LocalDateTime.now()));
    }

    //예매가능경기 전체 조회
    public List<GameResponseDto> getAvailableGame() {
        List<Game> gameList = getGames();
        return filterGames(game -> game.getStartDate().isBefore(LocalDateTime.now()));
    }

    private List<Game> getGames() {
        List<Game> gameList = gameRepository.findAll();
        if (gameList.isEmpty()) {
            throw new IllegalArgumentException("조회할 수 있는 게임이 없습니다.");
        }
        return gameList;
    }

    private List<GameResponseDto> filterGames(Predicate<Game> condition) {
        return getGames().stream()
            .filter(condition)
            .map(game -> new GameResponseDto(game.getName(), game.getCategory()))
            .toList();
    }
}
