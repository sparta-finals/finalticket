package com.sparta.finalticket.domain.comment.entity;

import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String reason; // 신고 사유

    @Column(nullable = false)
    private LocalDateTime reportDate; // 신고 날짜

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus; // 신고 처리 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_id") // 신고 처리자
    private User reportedBy;

    @Column
    private String additionalDetails; // 추가적인 설명

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public void setReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
    }
}
