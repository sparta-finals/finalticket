package com.sparta.finalticket.domain.comment.dto.response;

import com.sparta.finalticket.domain.comment.entity.CommentReport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentReportResponseDto {

    private Long id;
    private Long commentId;
    private Long userId;
    private String reason; // 신고 사유
    private LocalDateTime reportDate; // 신고 날짜
    private String reportStatus; // 신고 처리 상태
    private Long reportedById; // 신고 처리자
    private String additionalDetails; // 추가적인 설명

    public CommentReportResponseDto(CommentReport commentReport) {
        this.id = commentReport.getId();
        this.commentId = commentReport.getComment().getId();
        this.userId = commentReport.getUser().getId();
        this.reason = commentReport.getReason();
        this.reportDate = commentReport.getReportDate();
        this.reportStatus = commentReport.getReportStatus().name();
        this.reportedById = commentReport.getReportedBy() != null ? commentReport.getReportedBy().getId() : null;
        this.additionalDetails = commentReport.getAdditionalDetails();
    }
}
