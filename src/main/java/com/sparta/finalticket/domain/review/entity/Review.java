package com.sparta.finalticket.domain.review.entity;

import com.sparta.finalticket.domain.comment.entity.Comment;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.timeStamped.TimeStamped;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review", indexes = {
	@Index(name = "idx_game_id", columnList = "game_id"),
	@Index(name = "idx_user_id", columnList = "user_id"),
	@Index(name = "idx_state", columnList = "state"),
    @Index(name = "idx_genre", columnList = "genre"),
	@Index(name = "idx_created_at", columnList = "created_at")
})
@SQLDelete(sql = "UPDATE review SET state = false WHERE id = ?")
@Where(clause = "state = true")
public class Review extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String review;

	@Column
	private Long score;

	@Column
	private Boolean state;

	@Column
	private Long totalReviewCount;

	@Column
	private Double averageReviewScore;

	@Column
	private Long likeCount;

	@Column
	private Long dislikeCount;

	@Column
	private Boolean reported;

	@Column
	private Long reportCount;

	@Column
	private Long recommendationCount;

	@Column
	private Long viewCount;

	@Column
	private LocalDateTime reviewTime; // 리뷰 작성 시간대를 저장할 필드 추가

	@Column
	private Double userTrustScore;

	@Column
	private String sharePlatform; // 공유 플랫폼 (e.g., "Facebook", "Twitter", "WhatsApp", etc.)

	@Column
	private String shareMessage; // 공유 메시지

	@Column
	private String shareLink; // 공유 링크

	@Enumerated(EnumType.STRING)
	private Genre genre; // 게임의 장르 정보

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "game_id")
	private Game game;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments;

	public void setId(Long id) {
		this.id = id;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public void setScore(Long score) {
		this.score = score;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
	}

	public void setDislikeCount(Long dislikeCount) {
		this.dislikeCount = dislikeCount;
	}

	public long getLikeCount() {
		return likeCount != null ? likeCount.longValue() : 0L; // null일 경우 0을 반환하도록 수정
	}

	public long getDislikeCount() {
		return dislikeCount != null ? dislikeCount.longValue() : 0L; // null일 경우 0을 반환하도록 수정
	}

	public void setReported(boolean reported) {
		this.reported = reported;
	}

	public void setReportCount(long reportCount) {
		this.reportCount = reportCount;
	}

	public void setRecommendationCount(long recommendationCount) {
		this.recommendationCount = recommendationCount;
	}

	public void setReviewTime(LocalDateTime now) {
		this.reviewTime = now;
	}

	public void setUserTrustScore(Double userTrustScore) {
		this.userTrustScore = userTrustScore;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}
}
