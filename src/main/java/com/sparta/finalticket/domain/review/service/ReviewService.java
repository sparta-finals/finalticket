package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.review.dto.request.ReviewRequestDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.repository.ReviewRepository;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;

    @Transactional
    public void createReview(Long id, ReviewRequestDto reviewRequestDto, User user) {
        Review review = new Review();
        review.setReview(reviewRequestDto.getReview());
        review.setScore(reviewRequestDto.getScore());
        review.setState(reviewRequestDto.getState());
        review.setUser(user);
        review.setGame(gameRepository.findById(id).orElseThrow());
        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewByGameId(Long id) {
        Review review = reviewRepository.findByGameId(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 ID에 대한 경기 리뷰를 찾을 수 없습니다."));
        return new ReviewResponseDto(review);
    }

    public ReviewResponseDto updateReview(Long id, ReviewRequestDto reviewRequestDto, User user) {
        Review review = reviewRepository.findByGameId(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 ID에 대한 경기 리뷰를 찾을 수 없습니다."));
        review.setReview(reviewRequestDto.getReview());
        review.setScore(reviewRequestDto.getScore());
        review.setState(reviewRequestDto.getState());
        review.setUser(user);
        Review updatedReview = reviewRepository.save(review);
        return new ReviewResponseDto(updatedReview);
    }

    public void deleteReview(Long id, User user) {
        Review review = reviewRepository.findByGameId(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 ID에 대한 경기 리뷰를 찾을 수 없습니다."));
        if (!review.getUser().equals(user)) {
            throw new SecurityException("사용자는 이 리뷰를 삭제할 권한이 없습니다.");
        }
        reviewRepository.delete(review);
    }

    public List<ReviewResponseDto> getUserReviewList(User user) {
        return reviewRepository.getUserReviewList(user);
    }
}
