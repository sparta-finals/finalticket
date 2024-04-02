package com.sparta.finalticket.domain.ticket.repository;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.seat.entity.Seat;
import com.sparta.finalticket.domain.ticket.entity.Ticket;
import com.sparta.finalticket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByUserAndGameAndSeatAndState(User user, Game game, Seat seat, Boolean b);

    Optional<Ticket> findBySeatId(Long gameId);

    boolean existsByUserAndGameIdAndSeatIdAndState(User user, Long gameId, Long seatId, Boolean b);
}
