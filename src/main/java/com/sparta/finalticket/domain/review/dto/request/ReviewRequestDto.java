package com.sparta.finalticket.domain.review.dto.request;

import com.sparta.finalticket.domain.review.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto implements Serializable {

    private String review;

    private Long score;

    private Boolean state;

    private Long likeCount;

    private Long dislikeCount;

    private Genre genre;

    private Double userTrustScore;
}
