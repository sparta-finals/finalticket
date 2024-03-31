package com.sparta.finalticket.domain.ticket.controller;

import com.sparta.finalticket.domain.ticket.service.TicketService;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/games")
public class TicketController {

    private final TicketService ticketService;

    //티켓팅
    @PostMapping("/{gameId}/seats/{seatId}")
    public ResponseEntity createTicket(@PathVariable Long gameId, @PathVariable Long seatId, HttpServletRequest request) {
        ticketService.createTicket(gameId, seatId, getUser(request));
        return ResponseEntity.status(201).build();
    }
    //티켓팅 취소
    @DeleteMapping("/{gameId}/seats/{seatId}")
    public ResponseEntity deleteTicket(@PathVariable Long gameId, @PathVariable Long seatId, HttpServletRequest request) {
        ticketService.deleteTicket(gameId, seatId, getUser(request));
        return ResponseEntity.status(204).build();
    }
    private static User getUser(HttpServletRequest request) {
        return (User) request.getAttribute("user");
    }
}
