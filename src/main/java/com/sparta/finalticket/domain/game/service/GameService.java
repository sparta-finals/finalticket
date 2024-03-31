package com.sparta.finalticket.domain.game.service;

import com.sparta.finalticket.domain.game.dto.GameResponseDto;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    //예매예정경기 전체 조회
    public List<GameResponseDto> getUpcomingGame() {
        List<Game> gameList = getGames();
        return gameList.stream()
                .filter(game -> game.getStartDate().isBefore(LocalDateTime.now()))
                .map(game -> new GameResponseDto(game.getName(), game.getCategory())).toList();
    }
    //예매가능경기 전체 조회
    public List<GameResponseDto> getAvailableGame() {
        List<Game> gameList = getGames();
        return gameList.stream()
                .filter(game -> game.getStartDate().isAfter(LocalDateTime.now()))
                .map(game -> new GameResponseDto(game.getName(), game.getCategory())).toList();
    }
    private List<Game> getGames() {
        List<Game> gameList = gameRepository.findAll();
        if(gameList.isEmpty()) {
            throw new IllegalArgumentException("조회할 수 있는 게임이 없습니다.");
        }
        return gameList;
    }
}
