package com.sparta.finalticket.domain.ticket.repository;

import com.sparta.finalticket.domain.ticket.dto.TicketResponseDto;
import java.util.List;

public interface TicketRepositoryCustom {

    List<TicketResponseDto> getUserTicketList(Long id);
}
