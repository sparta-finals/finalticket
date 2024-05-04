package com.sparta.finalticket.domain.comment.service;

import com.sparta.finalticket.domain.comment.dto.request.CommentReportRequestDto;
import com.sparta.finalticket.domain.comment.dto.response.CommentReportResponseDto;
import com.sparta.finalticket.domain.comment.entity.Comment;
import com.sparta.finalticket.domain.comment.entity.CommentReport;
import com.sparta.finalticket.domain.comment.entity.ReportStatus;
import com.sparta.finalticket.domain.comment.repository.CommentReportRepository;
import com.sparta.finalticket.domain.comment.repository.CommentRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.global.exception.comment.CommentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final CommentRepository commentRepository;
    private final CommentReportRepository commentReportRepository;

    @Transactional
    public CommentReportResponseDto reportComment(Long commentId, CommentReportRequestDto requestDto, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        CommentReport report = new CommentReport();
        report.setComment(comment);
        report.setUser(user);
        report.setReason(requestDto.getReason());
        report.setReportDate(LocalDateTime.now());
        report.setReportStatus(ReportStatus.PENDING);

        CommentReport savedReport = commentReportRepository.save(report);
        return new CommentReportResponseDto(savedReport);
    }
}