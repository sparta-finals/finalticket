package com.sparta.finalticket.domain.ticket.service;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.seat.entity.Seat;
import com.sparta.finalticket.domain.seat.repository.SeatRepository;
import com.sparta.finalticket.domain.seatsetting.entity.SeatSetting;
import com.sparta.finalticket.domain.seatsetting.repository.SeatSettingRepository;
import com.sparta.finalticket.domain.ticket.dto.TicketResponseDto;
import com.sparta.finalticket.domain.ticket.entity.Ticket;
import com.sparta.finalticket.domain.ticket.repository.TicketRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.global.config.redis.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final GameRepository gameRepository;

    private final SeatRepository seatRepository;

    private final SeatSettingRepository seatsettingRepository;

    private final TicketRepository ticketRepository;


    public List<TicketResponseDto> getUserTicketList(User user) {
        return ticketRepository.getUserTicketList(user.getId());
    }

    //티켓팅
    @DistributedLock(key = "#seatId")
    public void createTicket(Long gameId, Long seatId, User user) {
        if (seatRepository.existsByUserAndGameIdAndSeatsettingIdAndState(user, gameId, seatId, true)) {
            throw new IllegalArgumentException("해당 좌석은 이미 예매 되었습니다.");
        }
        boolean existingTicket = seatRepository.existsByUserAndGameIdAndSeatsettingIdAndState(user, gameId, seatId, false);
        if (!existingTicket) {
            Game game = getGame(gameId);
            SeatSetting seatSetting = getSeatsetting(seatId);

            int price = seatSetting.getSeatType().getPrice();
            Seat seat = new Seat(game, seatSetting, user, true, price);
            seatRepository.save(seat);

            Ticket ticket = new Ticket(user, game, seat, true);
            ticketRepository.save(ticket);
        } else {
            Seat seat = getSeat(gameId, seatId, user.getId(), false);
            seat.update(true);

            Ticket ticket = getTicket(seat.getId());
            ticket.update(true);
        }
    }

    //티켓팅 취소
    @DistributedLock(key = "#seatId")
    public void deleteTicket(Long gameId, Long seatId, User user) {
        Seat seat = getSeat(gameId, seatId, user.getId(), true);
        seat.update(false);

        Ticket ticket = getTicket(seat.getId());
        ticket.update(false);
    }

    private Game getGame(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게임입니다."));
    }

    private Seat getSeat(Long gameId, Long seatId, Long userId, boolean b) {
        return seatRepository.findSeatByGameIdAndSeatsettingIdAndUserIdAndState(gameId, seatId, userId, b)
                .orElseThrow(() -> new IllegalArgumentException("예약되지 않은 좌석 입니다."));
    }

    private SeatSetting getSeatsetting(Long seatId) {
        return seatsettingRepository.findById(seatId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다"));
    }

    private Ticket getTicket(Long gameId) {
        return ticketRepository.findBySeatId(gameId)
                .orElseThrow(() -> new IllegalArgumentException("예약되지 않은 티켓 입니다."));
    }

}
