package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.dto.ReviewRequestDto;
import com.sparta.finalticket.domain.review.dto.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public void createReview(Long id, ReviewRequestDto reviewRequestDto) {
        Review review = new Review();
        review.setReview(reviewRequestDto.getReview());
        review.setScore(reviewRequestDto.getScore());
        review.setState(reviewRequestDto.getState());
        Review getReviews =  reviewRepository.save(review);
        new ReviewResponseDto(getReviews);
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewByGameId(Long id) {
        Review review = reviewRepository.findByGameId(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 ID에 대한 경기 리뷰를 찾을 수 없습니다."));
        return new ReviewResponseDto(review);
    }

    public ReviewResponseDto updateReview(Long id, ReviewRequestDto reviewRequestDto) {
        Review review = reviewRepository.findByGameId(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 ID에 대한 경기 리뷰를 찾을 수 없습니다."));
        review.setReview(reviewRequestDto.getReview());
        review.setScore(reviewRequestDto.getScore());
        review.setState(reviewRequestDto.getState());
        Review updatedReview = reviewRepository.save(review);
        return new ReviewResponseDto(updatedReview);
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findByGameId(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 ID에 대한 경기 리뷰를 찾을 수 없습니다."));
        reviewRepository.delete(review);
    }
}
