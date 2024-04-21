package com.sparta.finalticket.domain.review.converter;

import com.sparta.finalticket.domain.review.dto.request.ReviewRequestDto;
import com.sparta.finalticket.domain.review.dto.request.ReviewUpdateRequestDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewUpdateResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;

public class ReviewConverter {

    public static Review convertToEntity(ReviewRequestDto requestDto) {
        Review review = new Review();
        review.setReview(requestDto.getReview());
        review.setScore(requestDto.getScore());
        // Set other properties as needed
        return review;
    }

    public static Review convertToEntity(ReviewUpdateRequestDto requestDto) {
        Review review = new Review();
        review.setReview(requestDto.getReview());
        review.setScore(requestDto.getScore());
        // Set other properties as needed
        return review;
    }

    public static ReviewResponseDto convertToResponseDto(Review review) {
        ReviewResponseDto responseDto = new ReviewResponseDto();
        responseDto.setId(review.getId());
        responseDto.setReview(review.getReview());
        responseDto.setScore(review.getScore());
        responseDto.setState(review.getState());
        responseDto.setUserId(review.getUser() != null ? review.getUser().getId() : null);
        responseDto.setGameId(review.getGame() != null ? review.getGame().getId() : null);
        // Set other properties as needed
        return responseDto;
    }

    public static ReviewUpdateResponseDto convertToUpdateResponseDto(Review review) {
        ReviewUpdateResponseDto responseDto = new ReviewUpdateResponseDto();
        responseDto.setId(review.getId());
        responseDto.setReview(review.getReview());
        responseDto.setScore(review.getScore());
        responseDto.setState(review.getState());
        responseDto.setUserId(review.getUser() != null ? review.getUser().getId() : null);
        responseDto.setGameId(review.getGame() != null ? review.getGame().getId() : null);
        // Set other properties as needed
        return responseDto;
    }
}

