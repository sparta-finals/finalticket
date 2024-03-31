package com.sparta.finalticket.domain.ticket.service;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.seat.entity.Seat;
import com.sparta.finalticket.domain.seat.repository.SeatRepository;
import com.sparta.finalticket.domain.seatsetting.entity.Seatsetting;
import com.sparta.finalticket.domain.seatsetting.repository.SeatsettingRepository;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final GameRepository gameRepository;

    private final SeatRepository seatRepository;

    private final SeatsettingRepository seatsettingRepository;

    //티켓팅
    @Transactional
    public void createTicket(Long gameId, Long seatId, User user) {
        //예매된 티켓 조회
        getSeat(gameId, seatId, user.getId());
        //존재하는 게임 검증
        Game game = getGame(gameId);
        //존재하는 좌석 검증
        Seatsetting seatSetting = getSeatsetting(seatId);

        Seat seat = new Seat(game, seatSetting, user);
        seatRepository.save(seat);
    }
    //티켓팅 취소
    @Transactional
    public void deleteTicket(Long gameId, Long seatId, User user) {
        Seat seat = getSeat(gameId, seatId, user.getId());
        seatRepository.delete(seat);
    }
    private Game getGame(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게임입니다."));
    }
    private Seat getSeat(Long gameId, Long seatId, Long userId) {
        return seatRepository.findSeatByGameIdAndSeatsettingIdAndUserId(gameId, seatId, userId).orElseThrow(() -> new IllegalArgumentException("이미 예매된 좌석입니다."));
    }
    private Seatsetting getSeatsetting(Long seatId) {
        return seatsettingRepository.findById(seatId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다"));
    }
}
