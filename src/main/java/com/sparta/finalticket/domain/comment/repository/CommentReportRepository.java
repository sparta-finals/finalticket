package com.sparta.finalticket.domain.comment.repository;

import com.sparta.finalticket.domain.comment.entity.Comment;
import com.sparta.finalticket.domain.comment.entity.CommentReport;
import com.sparta.finalticket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    Optional<CommentReport> findByCommentAndUser(Comment comment, User user);
}
