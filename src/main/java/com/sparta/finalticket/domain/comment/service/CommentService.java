package com.sparta.finalticket.domain.comment.service;

import com.sparta.finalticket.domain.comment.dto.request.CommentRequestDto;
import com.sparta.finalticket.domain.comment.dto.request.ParentCommentRequestDto;
import com.sparta.finalticket.domain.comment.dto.response.CommentListResponseDto;
import com.sparta.finalticket.domain.comment.dto.response.CommentResponseDto;
import com.sparta.finalticket.domain.comment.dto.response.CommentUpdateResponseDto;
import com.sparta.finalticket.domain.comment.dto.response.ParentCommentResponseDto;
import com.sparta.finalticket.domain.comment.entity.*;
import com.sparta.finalticket.domain.comment.repository.CommentReactionRepository;
import com.sparta.finalticket.domain.comment.repository.CommentRepository;
import com.sparta.finalticket.domain.comment.repository.ParentCommentRepository;
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
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final GameRepository gameRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final ParentCommentRepository parentCommentRepository;

    // 부적절한 내용이나 욕설 패턴
    private static final Pattern INAPPROPRIATE_PATTERN = Pattern.compile("욕설|부적절한 내용|비속어");

    // 댓글 필터링 메소드
    private boolean isCommentFiltered(String content) {
        return INAPPROPRIATE_PATTERN.matcher(content).find();
    }

    @Transactional
    public CommentResponseDto createComment(Long gameId, Long reviewId, CommentRequestDto requestDto, User user) {
        Comment comment = new Comment();
        String filteredContent = requestDto.getContent();
        if (isCommentFiltered(filteredContent)) {
            // 필터링된 댓글은 자동으로 숨김 처리
            comment.setState(false);
            filteredContent = "이 댓글은 부적절한 내용을 포함하고 있습니다.";
        }
        comment.setContent(filteredContent);
        comment.setUser(user);

        Game game = GameById(gameId);
        comment.setGame(game);

        Review review = ReviewById(reviewId);
        comment.setReview(review);


        if (requestDto.getAnonymous()) {
            // 익명 댓글일 경우 작성자 정보를 저장하지 않음
            comment.setAnonymous(true);
        }

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
        String filteredContent = requestDto.getContent();
        if (isCommentFiltered(filteredContent)) {
            // 필터링된 댓글은 자동으로 숨김 처리
            comment.setState(false);
            filteredContent = "이 댓글은 부적절한 내용을 포함하고 있습니다.";
        }
        comment.setContent(filteredContent);
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

    @Transactional
    public List<CommentListResponseDto> getAllCommentSorted(Long gameId, Long reviewId, User user, CommentSortType sortType) {
        List<Comment> commentList = commentRepository.findByUserAndGameIdAndReviewId(user, gameId, reviewId);
        return commentList.stream()
                .map(CommentListResponseDto::new)
                .sorted(sortType.getComparator())
                .toList();
    }

    @Transactional
    public ParentCommentResponseDto createParentComment(Long gameId, Long reviewId, Long commentId, ParentCommentRequestDto requestDto, User user) {
        ParentComment parentComment = new ParentComment();
        parentComment.setContent(requestDto.getContent());
        parentComment.setState(true);
        parentComment.setUser(user);

        Game game = GameById(gameId);
        parentComment.setGame(game);

        Review review = ReviewById(reviewId);
        parentComment.setReview(review);

        Comment originalComment = getCommentById(commentId);
        parentComment.setComment(originalComment); // 원본 댓글에 대한 참조 설정

        ParentComment createdParentComment = parentCommentRepository.save(parentComment);
        return new ParentCommentResponseDto(createdParentComment);
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
