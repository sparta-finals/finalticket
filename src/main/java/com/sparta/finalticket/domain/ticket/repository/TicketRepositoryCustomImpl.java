package com.sparta.finalticket.domain.ticket.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.finalticket.domain.game.entity.QGame;
import com.sparta.finalticket.domain.seat.entity.QSeat;
import com.sparta.finalticket.domain.seatsetting.entity.QSeatSetting;
import com.sparta.finalticket.domain.ticket.dto.TicketResponseDto;
import com.sparta.finalticket.domain.ticket.entity.QTicket;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryCustomImpl implements TicketRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TicketResponseDto> getUserTicketList(Long userId) {

        return queryFactory.select(QTicket.ticket)
            .from(QTicket.ticket)
            .where(QTicket.ticket.user.id.eq(userId))
            .leftJoin(QTicket.ticket.game, QGame.game)
            .fetchJoin()
            .leftJoin(QTicket.ticket.seat, QSeat.seat)
            .fetchJoin()
            .leftJoin(QSeat.seat.seatsetting, QSeatSetting.seatSetting)
            .fetchJoin()
            .distinct()
            .fetch().stream().map(TicketResponseDto::new).toList();
    }
}
