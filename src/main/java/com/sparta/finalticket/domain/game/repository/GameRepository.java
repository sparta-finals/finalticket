package com.sparta.finalticket.domain.game.repository;

import com.sparta.finalticket.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long>, CustomGameRepository {


}
