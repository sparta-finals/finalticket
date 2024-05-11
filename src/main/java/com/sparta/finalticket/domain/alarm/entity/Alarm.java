package com.sparta.finalticket.domain.alarm.entity;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.timeStamped.TimeStamped;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

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

	@Column(nullable = false)
	private Integer deliveryAttempts; // 전송 시도 횟수

	@Column(nullable = false)
	private LocalDateTime scheduledTime; // 예약된 알림 시간

	@Column(nullable = false)
	private String teamName; // 특정 팀의 경기 시작 전 알림을 받을 경우 해당 팀의 이름

	@Column(nullable = false)
	private LocalDateTime alarmTime; // 알람 시간

	// Alarm 엔티티에 우선순위 필드 추가
	@Enumerated(EnumType.STRING)
	private Priority priority;

	// 알림 그룹을 위한 필드 추가
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private AlarmGroup group;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;

	@Enumerated(EnumType.STRING)
	private AlarmType alarmType;

	public Alarm(String content, Boolean state, Boolean read, User user, Game game, Priority priority, AlarmGroup group) {
		this.content = content;
		this.state = state;
		this.isRead = read;
		this.user = user;
		this.game = game;
		this.priority = priority;
		this.group = group;
		this.deliveryAttempts = 0; // 초기 전송 시도 횟수는 0으로 설정
	}

	// deliveryAttempts 필드의 getter 및 setter 추가
	public Integer getDeliveryAttempts() {
		return deliveryAttempts;
	}

	public void setDeliveryAttempts(Integer deliveryAttempts) {
		this.deliveryAttempts = deliveryAttempts;
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

	public void setGroup(AlarmGroup group) {
		this.group = group;
	}

	public void setScheduledTime(LocalDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public void setAlarmType(AlarmType alarmType) {
		this.alarmType = alarmType;
	}

	public void setAlarmTime(LocalDateTime alarmTime) {
		this.alarmTime = alarmTime;
	}
}
