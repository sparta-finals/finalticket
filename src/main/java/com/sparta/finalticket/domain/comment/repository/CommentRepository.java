package com.sparta.finalticket.domain.comment.repository;

import com.sparta.finalticket.domain.comment.entity.Comment;
import com.sparta.finalticket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    List<Comment> findByUserAndGameIdAndReviewId(User user, Long gameId, Long reviewId);
}
