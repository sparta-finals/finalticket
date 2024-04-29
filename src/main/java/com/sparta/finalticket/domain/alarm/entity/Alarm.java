package com.sparta.finalticket.domain.alarm.entity;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.timeStamped.TimeStamped;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "alarm", indexes = {
		@Index(name = "idx_game_id", columnList = "game_id"),
		@Index(name = "idx_user_id", columnList = "user_id"),
		@Index(name = "idx_state", columnList = "state")
})
@SQLDelete(sql = "UPDATE alarm SET state = true WHERE id = ?")
@Where(clause = "state = true")
public class Alarm extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private Boolean state;

	@Column(nullable = false)
	private Boolean isRead; // 읽음 여부 추가

	// Alarm 엔티티에 우선순위 필드 추가
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Priority priority;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;

	public Alarm(String content, Boolean state, Boolean read, User user, Game game, Priority priority) {
		this.content = content;
		this.state = state;
		this.isRead = read;
		this.user = user;
		this.game = game;
		this.priority = priority;
	}

	public void setIsRead(boolean isRead) {
		this.isRead = isRead;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
}
