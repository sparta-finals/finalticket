package com.sparta.finalticket.domain.comment.repository;

import com.sparta.finalticket.domain.comment.entity.Comment;
import com.sparta.finalticket.domain.comment.entity.CommentReaction;
import com.sparta.finalticket.domain.comment.entity.ReactionType;
import com.sparta.finalticket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    Optional<CommentReaction> findByCommentAndUser(Comment comment, User user);

    Long countByCommentAndReaction(Comment comment, ReactionType reactionType);
}
