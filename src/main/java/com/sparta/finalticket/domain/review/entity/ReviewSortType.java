package com.sparta.finalticket.domain.review.entity;

import java.util.Comparator;

public enum ReviewSortType {
    LATEST {
        @Override
        public Comparator<Review> getComparator() {
            return Comparator.comparing(Review::getCreatedAt).reversed();
        }
    },
    HIGHEST_SCORE {
        @Override
        public Comparator<Review> getComparator() {
            return Comparator.comparingLong(Review::getScore).reversed();
        }
    };

    public abstract Comparator<Review> getComparator();
}
