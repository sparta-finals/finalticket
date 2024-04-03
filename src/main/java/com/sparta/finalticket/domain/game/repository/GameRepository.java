package com.sparta.finalticket.domain.game.repository;

import com.sparta.finalticket.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long>, CustomGameRepository {

    Optional<Game> findByIdAndStateTrue(Long gameId);
}
