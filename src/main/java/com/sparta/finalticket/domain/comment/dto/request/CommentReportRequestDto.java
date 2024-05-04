package com.sparta.finalticket.domain.comment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentReportRequestDto {

    private String reason; // 신고 사유
    private LocalDateTime reportDate; // 신고 날짜
}