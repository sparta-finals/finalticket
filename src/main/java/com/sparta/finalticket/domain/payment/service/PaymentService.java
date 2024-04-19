package com.sparta.finalticket.domain.payment.service;

import com.sparta.finalticket.domain.payment.dto.request.RequestPayDto;
import com.sparta.finalticket.domain.payment.entity.PaymentStatus;
import com.sparta.finalticket.domain.ticket.entity.Ticket;
import com.sparta.finalticket.domain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final TicketRepository ticketRepository;
    public RequestPayDto getRequestPayDto(Long gameId,Long seatId) {

        log.info(seatId.toString());
        // 티켓 엔티티 조회
        Ticket ticket = ticketRepository.findByGameIdAndSeatId(gameId,seatId);
        log.info(ticket.toString());

        RequestPayDto dto = new RequestPayDto();
        dto.setTicketUid(ticket.getTicketUid());
        dto.setGameName(ticket.getSeat().getGame().getName());
        dto.setPrice(ticket.getSeat().getPrice());
        dto.setUserName(ticket.getUser().getUsername());
        dto.setUserEmail(ticket.getUser().getEmail());
        dto.setUserAddress(ticket.getUser().getAddress());

        return dto;
    }

    public void paymentSuccess(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("티켓 없음"));

        // 결제 성공시
        ticket.setStatus(PaymentStatus.OK);

        ticketRepository.save(ticket);
    }




}
