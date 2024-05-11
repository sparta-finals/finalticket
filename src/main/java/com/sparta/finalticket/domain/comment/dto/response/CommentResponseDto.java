package com.sparta.finalticket.domain.comment.dto.response;

import com.sparta.finalticket.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String content;
    private Boolean state;
    private Long userId;
    private Long gameId;
    private Long reviewId;
    private Long likes; // 좋아요 수
    private Long dislikes; // 싫어요 수

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.state = comment.getState();
        this.userId = comment.getUser().getId();
        this.gameId = comment.getGame().getId();
        this.reviewId = comment.getReview().getId();
        this.likes = comment.getLikes();
        this.dislikes = comment.getDislikes();
    }
}
