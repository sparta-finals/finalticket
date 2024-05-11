package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RealTimeReviewUpdateService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void updateReviewAndNotify(Long gameId, Review review) {
        // 리뷰를 저장하고 상태를 갱신
        Review savedReview = reviewRepository.save(review);

        // 실시간으로 업데이트된 리뷰를 클라이언트에 알림
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/review", new ReviewResponseDto(savedReview));
    }
}
