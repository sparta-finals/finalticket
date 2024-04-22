package com.sparta.finalticket.domain.ticket.controller;

import com.sparta.finalticket.domain.ticket.service.TicketService;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/games")
public class TicketController {

    private final TicketService ticketService;

    //티켓팅
    @PostMapping("/{gameId}/seats/{seatId}")
    public ResponseEntity createTicket(@PathVariable Long gameId, @PathVariable Long seatId, HttpServletRequest request) {

        return ResponseEntity.status(201).body( ticketService.createTicket(gameId, seatId, getUser(request)));
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

    // 티켓팅 동시성 테스트
    @PostMapping("/{gameId}/seats/{seatId}/test")
    public ResponseEntity<Void> create2Order100(@PathVariable Long gameId, @PathVariable Long seatId, HttpServletRequest request) {
        System.out.println("\n\n\n\n[concurrencyTestUsingLockByParallel]");
        IntStream.range(0, 1000).parallel().forEach(i -> ticketService.createTicket(gameId, seatId, getUser(request)));
        return ResponseEntity.status(201).build();
    }
}
