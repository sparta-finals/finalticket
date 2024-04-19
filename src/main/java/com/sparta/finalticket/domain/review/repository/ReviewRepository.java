package com.sparta.finalticket.domain.review.repository;

import com.sparta.finalticket.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    Long countByGameId(Long gameId);

    @Query("SELECT AVG(r.score) FROM Review r WHERE r.game.id = :gameId")
    Double calculateAverageScoreByGameId(Long gameId);
}
