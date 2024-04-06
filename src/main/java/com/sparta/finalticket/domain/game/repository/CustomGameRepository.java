package com.sparta.finalticket.domain.game.repository;

import com.sparta.finalticket.domain.game.dto.response.GameResponseDto;
import com.sparta.finalticket.domain.user.entity.User;
import java.util.List;

public interface CustomGameRepository {

    void deleteGameAndRelateEntities(Long gameId);

    List<GameResponseDto> getUserGameList(User user);
}
