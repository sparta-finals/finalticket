package com.sparta.finalticket.domain.game.service;

import com.sparta.finalticket.domain.game.dto.GameResponseDto;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    //예매예정경기 전체 조회
    public List<GameResponseDto> getUpcomingGame() {
        List<Game> gameList = getGames();
        return filterGames(game -> game.getStartDate().isBefore(LocalDateTime.now()));
    }

    //예매가능경기 전체 조회
    public List<GameResponseDto> getAvailableGame() {
        List<Game> gameList = getGames();
        return filterGames(game -> game.getStartDate().isAfter(LocalDateTime.now()));
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
