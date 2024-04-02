package com.sparta.finalticket.domain.ticket.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.finalticket.domain.ticket.entity.QTicket;
import com.sparta.finalticket.domain.ticket.entity.Ticket;
import com.sparta.finalticket.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final JPAQueryFactory jpaQueryFactory;

    public List<TicketResponseDto> getUserTicketList(User user) {
        QTicket ticket = QTicket.ticket;
        return jpaQueryFactory.selectFrom(ticket).where(ticket.user.id.eq(user.getId())).fetchAll().stream().map(TicketResponseDto::new).toList();
    }
}
