package com.sparta.finalticket.domain.alarm.entity;

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
	@Index(name = "idx_user_id", columnList = "user_id")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public Alarm(String content, Boolean state, User user) {
		this.content = content;
		this.state = state;
		this.user = user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setState(boolean state) {
		this.state = state;
	}
}
