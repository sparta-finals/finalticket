package com.sparta.finalticket.domain.ticket.entity;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.payment.entity.PaymentStatus;
import com.sparta.finalticket.domain.payment.entity.Payments;
import com.sparta.finalticket.domain.seat.entity.Seat;
import com.sparta.finalticket.domain.timeStamped.TimeStamped;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Ticket extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Boolean state;

	private String ticketUid;

	private PaymentStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seat_id")
	private Seat seat;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payments_id")
	private Payments payments;

	public Ticket(User user, Game game, Seat seat, boolean b,String ticketUid) {
		this.user = user;
		this.game = game;
		this.seat = seat;
		this.state = b;
		this.ticketUid = UUID.randomUUID().toString();
	}

	public void update(boolean b) {
		this.state = b;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}
}
