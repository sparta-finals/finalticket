package com.sparta.finalticket.domain.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewShareRequestDto {

    private String sharePlatform; // 공유 플랫폼 (e.g., "Facebook", "Twitter", "WhatsApp", etc.)
    private String shareMessage; // 공유 메시지
}
