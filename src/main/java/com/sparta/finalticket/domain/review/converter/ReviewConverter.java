package com.sparta.finalticket.domain.review.converter;

import com.sparta.finalticket.domain.review.entity.Review;

import java.util.HashMap;
import java.util.Map;

public class ReviewConverter {

    public static Map<String, String> convertReviewToMap(Review review) {
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(review.getId()));
        map.put("review", review.getReview());
        map.put("score", String.valueOf(review.getScore()));
        map.put("state", String.valueOf(review.getState()));
        map.put("userId", String.valueOf(review.getUser() != null ? review.getUser().getId() : null));
        map.put("gameId", String.valueOf(review.getGame() != null ? review.getGame().getId() : null));
        return map;
    }
}
