package com.sparta.finalticket.domain.seat.repository;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.seat.entity.Seat;
import com.sparta.finalticket.domain.user.entity.User;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findSeatByGameIdAndSeatsettingIdAndUserIdAndState(Long gameId, Long seatId, Long userId, boolean b);

    boolean existsByUserAndGameAndSeatsettingIdAndState(User user, Game game, Long seatId, Boolean b);

    boolean existsByUserAndGameIdAndSeatsettingIdAndState(User user, Long gameId, Long seatId, boolean b);

    Optional<Seat> findByGameId(Long id);
    List<Seat> findALlByGameIdAndStateTrue(Long id);

    Optional<Seat> findByGameIdAndSeatsettingId(Long gameId,Long seatSettingId);
}
