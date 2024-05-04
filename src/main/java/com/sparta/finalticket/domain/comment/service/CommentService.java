package com.sparta.finalticket.domain.comment.service;

import com.sparta.finalticket.domain.comment.dto.request.CommentRequestDto;
import com.sparta.finalticket.domain.comment.dto.response.CommentListResponseDto;
import com.sparta.finalticket.domain.comment.dto.response.CommentResponseDto;
import com.sparta.finalticket.domain.comment.dto.response.CommentUpdateResponseDto;
import com.sparta.finalticket.domain.comment.entity.Comment;
import com.sparta.finalticket.domain.comment.entity.CommentReaction;
import com.sparta.finalticket.domain.comment.entity.ReactionType;
import com.sparta.finalticket.domain.comment.repository.CommentReactionRepository;
import com.sparta.finalticket.domain.comment.repository.CommentRepository;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.repository.ReviewRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.global.exception.comment.CommentNotFoundException;
import com.sparta.finalticket.global.exception.review.GameIdRequiredException;
import com.sparta.finalticket.global.exception.review.ReviewNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final GameRepository gameRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;

    @Transactional
    public CommentResponseDto createComment(Long gameId, Long reviewId, CommentRequestDto requestDto, User user) {
        Comment comment = new Comment();
        comment.setContent(requestDto.getContent());
        comment.setState(true);
        comment.setUser(user);

        Game game = GameById(gameId);
        comment.setGame(game);

        Review review = ReviewById(reviewId);
        comment.setReview(review);

        Comment createdComment = commentRepository.save(comment);
        return new CommentResponseDto(createdComment);
    }

    public CommentResponseDto getCommentByReviewId(Long gameId, Long reviewId, Long commentId) {
        Comment comment = getCommentById(commentId);

        Game game = GameById(gameId);
        comment.setGame(game);

        Review review = ReviewById(reviewId);
        comment.setReview(review);

        return new CommentResponseDto(comment);
    }

    public List<CommentListResponseDto> getAllComment(Long gameId, Long reviewId, User user) {
        List<Comment> commentList = commentRepository.findByUserAndGameIdAndReviewId(user, gameId, reviewId);
        return commentList.stream()
                .map(CommentListResponseDto::new)
                .sorted(Comparator.comparing(CommentListResponseDto::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public CommentUpdateResponseDto updateComment(Long gameId, Long reviewId, Long commentId, CommentRequestDto requestDto, User user) {
        Comment comment = updateCommentById(commentId);
        comment.setContent(requestDto.getContent());
        comment.setState(true);
        comment.setUser(user);

        Game game = GameById(gameId);
        comment.setGame(game);

        Review review = ReviewById(reviewId);
        comment.setReview(review);

        Comment updatedComment = commentRepository.save(comment);
        return new CommentUpdateResponseDto(updatedComment);
    }

    @Transactional
    public void deleteComment(Long gameId, Long reviewId, Long commentId, User user) {
        Comment comment = deleteCommentById(commentId);
        comment.setUser(user);

        Game game = GameById(gameId);
        comment.setGame(game);

        Review review = ReviewById(reviewId);
        comment.setReview(review);

        commentRepository.delete(comment);
    }

    // 좋아요 및 싫어요 수를 업데이트하고 새로운 반응을 저장합니다.
    @Transactional
    public CommentResponseDto reactToComment(Long commentId, ReactionType reactionType, User user) {
        Comment comment = getCommentById(commentId);

        // 해당 유저가 이미 해당 댓글에 대해 반응했는지 확인
        Optional<CommentReaction> existingReaction = commentReactionRepository.findByCommentAndUser(comment, user);

        if (existingReaction.isPresent()) {
            // 만약 이미 반응이 있으면 반응을 업데이트
            CommentReaction reaction = existingReaction.get();
            reaction.setReaction(reactionType);
            commentReactionRepository.save(reaction);
        } else {
            // 반응이 없으면 새로운 반응 생성
            CommentReaction newReaction = new CommentReaction();
            newReaction.setComment(comment);
            newReaction.setUser(user);
            newReaction.setReaction(reactionType);
            commentReactionRepository.save(newReaction);
        }

        // 좋아요 및 싫어요 수 업데이트
        updateReactionCounts(comment);

        return new CommentResponseDto(comment);
    }

    // 댓글에 대한 좋아요 및 싫어요 수를 업데이트합니다.
    private void updateReactionCounts(Comment comment) {
        Long likes = commentReactionRepository.countByCommentAndReaction(comment, ReactionType.LIKE);
        Long dislikes = commentReactionRepository.countByCommentAndReaction(comment, ReactionType.DISLIKE);
        comment.setLikes(likes);
        comment.setDislikes(dislikes);
        commentRepository.save(comment);
    }


    private Game GameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameIdRequiredException("경기를 찾을 수 없습니다."));
    }

    private Review ReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
    }

    private Comment updateCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
    }

    private Comment deleteCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다.."));
    }
}
