package com.sparta.finalticket.domain.comment.dto.response;

import com.sparta.finalticket.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateResponseDto {

    private Long id;
    private String content;
    private Boolean state;
    private Long userId;
    private Long gameId;
    private Long reviewId;

    public CommentUpdateResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.state = comment.getState();
        this.userId = comment.getUser().getId();
        this.gameId = comment.getGame().getId();
        this.reviewId = comment.getReview().getId();
    }
}
