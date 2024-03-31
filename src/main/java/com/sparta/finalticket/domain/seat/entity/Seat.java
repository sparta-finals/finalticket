package com.sparta.finalticket.domain.seat.entity;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.seatsetting.entity.Seatsetting;
import com.sparta.finalticket.domain.timeStamped.TimeStamped;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Seat extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seatsetting_id")
	private Seatsetting seatsetting;

	public Seat(Game game, Seatsetting seatSetting, User user) {
		this.game = game;
		this.seatsetting = seatSetting;
		this.user = user;
	}
}
