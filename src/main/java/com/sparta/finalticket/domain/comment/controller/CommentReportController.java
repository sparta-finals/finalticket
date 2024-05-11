package com.sparta.finalticket.domain.comment.controller;

import com.sparta.finalticket.domain.comment.dto.request.CommentReportRequestDto;
import com.sparta.finalticket.domain.comment.dto.response.CommentReportResponseDto;
import com.sparta.finalticket.domain.comment.service.CommentReportService;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/games/{gameId}/review/{reviewId}")
@RequiredArgsConstructor
public class CommentReportController {

    private final CommentReportService commentReportService;

    @PostMapping("/comment/{commentId}/report")
    public ResponseEntity<CommentReportResponseDto> reportComment(@PathVariable(name = "gameId") Long gameId,
                                                                  @PathVariable(name = "reviewId") Long reviewId,
                                                                  @PathVariable(name = "commentId") Long commentId,
                                                                  @RequestBody @Valid CommentReportRequestDto requestDto,
                                                                  HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        CommentReportResponseDto responseDto = commentReportService.reportComment(commentId, requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
