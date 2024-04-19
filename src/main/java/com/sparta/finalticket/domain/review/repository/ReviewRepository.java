package com.sparta.finalticket.domain.review.repository;

import com.sparta.finalticket.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    Long countByGameId(Long gameId);

    Double calculateAverageScoreByGameId(Long gameId);
}
