package com.sparta.finalticket.domain.comment.entity;

import com.sparta.finalticket.domain.comment.dto.response.CommentListResponseDto;
import lombok.Getter;

import java.util.Comparator;

@Getter
public enum CommentSortType {

    LATEST(Comparator.comparing(CommentListResponseDto::getCreatedAt).reversed()), // 최신 댓글
    OLDEST(Comparator.comparing(CommentListResponseDto::getCreatedAt)), // 오래된 댓글
    MOST_LIKED(Comparator.comparing(CommentListResponseDto::getLikes).reversed()), // 좋아요 순
    LIKE_COUNT(Comparator.comparing(CommentListResponseDto::getLikes).thenComparing(CommentListResponseDto::getDislikes).reversed()); // 좋아요 수

    private final Comparator<CommentListResponseDto> comparator;

    CommentSortType(Comparator<CommentListResponseDto> comparator) {
        this.comparator = comparator;
    }
}
