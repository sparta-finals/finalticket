package com.sparta.finalticket.domain.ticket.repository;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.seat.entity.Seat;
import com.sparta.finalticket.domain.ticket.entity.Ticket;
import com.sparta.finalticket.domain.user.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long>, TicketRepositoryCustom {

    boolean existsByUserAndGameAndSeatAndState(User user, Game game, Seat seat, Boolean b);

    Optional<Ticket> findBySeatId(Long gameId);

    boolean existsByUserAndGameIdAndSeatIdAndState(User user, Long gameId, Long seatId, Boolean b);

    List<Ticket> findByUserId(Long userId);


    Ticket findByGameIdAndSeatId(Long gameId, Long seatId);


    @Query("select t " +
            "from Ticket t " +
            "left join t.payments p " +
            "where t.ticketUid = :ticketUid")
    Optional<Ticket> findByTicketUid(String ticketUid);

}