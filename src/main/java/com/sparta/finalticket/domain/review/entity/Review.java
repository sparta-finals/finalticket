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

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review", indexes = {
	@Index(name = "idx_game_id", columnList = "game_id"),
	@Index(name = "idx_user_id", columnList = "user_id"),
	@Index(name = "idx_state", columnList = "state")
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
}
