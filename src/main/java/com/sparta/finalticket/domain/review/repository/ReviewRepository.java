package com.sparta.finalticket.domain.review.repository;

import com.sparta.finalticket.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    Long countByGameId(Long gameId);

    @Query("SELECT AVG(r.score) FROM Review r WHERE r.game.id = :gameId")
    Double calculateAverageScoreByGameId(@Param("gameId") Long gameId);
}
