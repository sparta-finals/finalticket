package com.sparta.finalticket.domain.comment.controller;

import com.sparta.finalticket.domain.comment.dto.request.CommentRequestDto;
import com.sparta.finalticket.domain.comment.dto.request.CommentSortTypeRequestDto;
import com.sparta.finalticket.domain.comment.dto.request.ParentCommentRequestDto;
import com.sparta.finalticket.domain.comment.dto.response.CommentListResponseDto;
import com.sparta.finalticket.domain.comment.dto.response.CommentResponseDto;
import com.sparta.finalticket.domain.comment.dto.response.CommentUpdateResponseDto;
import com.sparta.finalticket.domain.comment.dto.response.ParentCommentResponseDto;
import com.sparta.finalticket.domain.comment.entity.CommentSortType;
import com.sparta.finalticket.domain.comment.entity.ReactionType;
import com.sparta.finalticket.domain.comment.service.CommentService;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/games/{gameId}/review/{reviewId}")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public ResponseEntity<CommentResponseDto> postComment(@PathVariable(name = "gameId") Long gameId,
                                                          @PathVariable(name = "reviewId") Long reviewId,
                                                          @RequestBody @Valid CommentRequestDto requestDto,
                                                          HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        CommentResponseDto commentResponseDto = commentService.createComment(gameId, reviewId, requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDto);
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<CommentResponseDto> getComment(@PathVariable(name = "gameId") Long gameId,
                                                         @PathVariable(name = "reviewId") Long reviewId,
                                                         @PathVariable(name = "commentId") Long commentId) {
        CommentResponseDto responseDto = commentService.getCommentByReviewId(gameId, reviewId, commentId);
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentListResponseDto>> getAllComment(@PathVariable(name = "gameId") Long gameId,
                                                                      @PathVariable(name = "reviewId") Long reviewId,
                                                                      HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        List<CommentListResponseDto> commentList = commentService.getAllComment(gameId, reviewId, user);
        return ResponseEntity.ok().body(commentList);
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<CommentUpdateResponseDto> putComment(@PathVariable(name = "gameId") Long gameId,
                                                         @PathVariable(name = "reviewId") Long reviewId,
                                                         @PathVariable(name = "commentId") Long commentId,
                                                         @RequestBody @Valid CommentRequestDto requestDto,
                                                         HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        CommentUpdateResponseDto responseDto = commentService.updateComment(gameId, reviewId, commentId, requestDto, user);
        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "gameId") Long gameId,
                                              @PathVariable(name = "reviewId") Long reviewId,
                                              @PathVariable(name = "commentId") Long commentId,
                                              HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        commentService.deleteComment(gameId, reviewId, commentId, user);
        return ResponseEntity.noContent().build();
    }

    // 댓글에 대한 좋아요 기능 추가
    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<CommentResponseDto> likeComment(@PathVariable(name = "commentId") Long commentId,
                                                          HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        CommentResponseDto responseDto = commentService.reactToComment(commentId, ReactionType.LIKE, user);
        return ResponseEntity.ok().body(responseDto);
    }

    // 댓글에 대한 싫어요 기능 추가
    @PostMapping("/comment/{commentId}/dislike")
    public ResponseEntity<CommentResponseDto> dislikeComment(@PathVariable(name = "commentId") Long commentId,
                                                             HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        CommentResponseDto responseDto = commentService.reactToComment(commentId, ReactionType.DISLIKE, user);
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/comments/sorted")
    public ResponseEntity<List<CommentListResponseDto>> getAllCommentSorted(@PathVariable(name = "gameId") Long gameId,
                                                                            @PathVariable(name = "reviewId") Long reviewId,
                                                                            @RequestParam(name = "sortType", defaultValue = "LATEST") CommentSortTypeRequestDto sortType,
                                                                            HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        CommentSortType sortTypeEnum = sortType.mapToSortType();
        List<CommentListResponseDto> commentList = commentService.getAllCommentSorted(gameId, reviewId, user, sortTypeEnum);
        return ResponseEntity.ok().body(commentList);
    }

    @PostMapping("/comment/{commentId}/parent")
    public ResponseEntity<ParentCommentResponseDto> postParentComment(@PathVariable(name = "gameId") Long gameId,
                                                                      @PathVariable(name = "reviewId") Long reviewId,
                                                                      @PathVariable(name = "commentId") Long commentId,
                                                                      @RequestBody @Valid ParentCommentRequestDto requestDto,
                                                                      HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        ParentCommentResponseDto parentCommentResponseDto = commentService.createParentComment(gameId, reviewId, commentId, requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(parentCommentResponseDto);
    }

}
