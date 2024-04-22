package com.sparta.finalticket.domain.game.repository;

import com.sparta.finalticket.domain.game.dto.response.GameResponseDto;
import com.sparta.finalticket.domain.game.entity.CategoryEnum;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface CustomGameRepository {

    void deleteGameAndRelateEntities(Long gameId);

    List<GameResponseDto> getUserGameList(User user);

    Optional<Game> findByIdAndStateTrue(Long gameId);

    List<GameResponseDto> getGameOfCategory(CategoryEnum categoryEnum);

    List<GameResponseDto> getGameOfKeyword(String keyword);
}
