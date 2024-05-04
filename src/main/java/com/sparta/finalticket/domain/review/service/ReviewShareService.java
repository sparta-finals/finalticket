package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.dto.request.ReviewShareRequestDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewShareResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewShareService {

    private final ReviewService reviewService;

    public ReviewShareResponseDto shareReview(Long gameId, Long reviewId, ReviewShareRequestDto requestDto) {
        // 리뷰 ID를 사용하여 리뷰를 조회
        Review review = reviewService.getReviewById(reviewId);

        // 공유 메시지 생성
        String shareMessage = generateShareMessage(requestDto.getShareMessage(), review);

        // 공유 링크 생성
        String shareLink = generateShareLink(gameId, reviewId);

        // 공유 링크와 메시지를 담은 응답 DTO 반환
        return new ReviewShareResponseDto(shareLink);
    }

    private String generateShareMessage(String customMessage, Review review) {
        // 사용자가 입력한 커스텀 메시지와 리뷰 내용을 합쳐서 반환
        return customMessage + "\n" + review.getReview();
    }

    private String generateShareLink(Long gameId, Long reviewId) {
        // 간단한 예시 URL 생성 (실제로는 해당 게임 페이지의 URL 등을 생성)
        return "https://example.com/game/" + gameId + "/review/" + reviewId;
    }
}
