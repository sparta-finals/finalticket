package com.sparta.finalticket.domain.comment.dto.request;

import com.sparta.finalticket.domain.comment.entity.CommentSortType;

import java.util.HashMap;
import java.util.Map;

public enum CommentSortTypeRequestDto {
    LATEST,
    OLDEST,
    MOST_LIKED;

    private static final Map<CommentSortTypeRequestDto, CommentSortType> SORT_TYPE_MAP = new HashMap<>();

    static {
        SORT_TYPE_MAP.put(LATEST, CommentSortType.LATEST);
        SORT_TYPE_MAP.put(OLDEST, CommentSortType.OLDEST);
        SORT_TYPE_MAP.put(MOST_LIKED, CommentSortType.MOST_LIKED);
    }

    public CommentSortType mapToSortType() {
        return SORT_TYPE_MAP.get(this);
    }
}