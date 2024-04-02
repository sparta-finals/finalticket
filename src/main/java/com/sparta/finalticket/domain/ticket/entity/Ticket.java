package com.sparta.finalticket.domain.ticket.entity;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.seat.entity.Seat;
import com.sparta.finalticket.domain.timeStamped.TimeStamped;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Ticket extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Boolean state;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seat_id")
	private Seat seat;

	public Ticket(User user, Game game, Seat seat, boolean b) {
		this.user = user;
		this.game = game;
		this.seat = seat;
		this.state = b;
	}

	public void update(boolean b) {
		this.state = b;
	}
}
