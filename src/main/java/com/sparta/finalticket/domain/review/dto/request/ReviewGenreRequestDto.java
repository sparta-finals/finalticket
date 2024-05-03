package com.sparta.finalticket.domain.review.dto.request;

import com.sparta.finalticket.domain.review.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewGenreRequestDto {

    private String review;

    private Long score;

    private Boolean state;

    private Long likeCount;

    private Long dislikeCount;

    private Genre genre;
}
