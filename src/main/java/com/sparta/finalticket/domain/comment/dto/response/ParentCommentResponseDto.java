package com.sparta.finalticket.domain.comment.dto.response;

import com.sparta.finalticket.domain.comment.entity.ParentComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParentCommentResponseDto {

    private Long id;
    private String content;
    private Boolean state;
    private Long userId; // 사용자의 ID

    public ParentCommentResponseDto(ParentComment parentComment) {
        this.id = parentComment.getId();
        this.content = parentComment.getContent();
        this.state = parentComment.getState();
        this.userId = parentComment.getUser().getId();
    }
}
