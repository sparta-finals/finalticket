package com.sparta.finalticket.domain.comment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParentCommentRequestDto {

    private String content;
    private Boolean state;
    private Long userId; // 사용자의 ID
}
