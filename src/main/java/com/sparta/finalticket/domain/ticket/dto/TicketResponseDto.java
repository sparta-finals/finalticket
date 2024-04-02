package com.sparta.finalticket.domain.ticket.dto;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.seat.entity.Seat;
import com.sparta.finalticket.domain.ticket.entity.Ticket;
import com.sparta.finalticket.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TicketResponseDto {

    private Long id;
    private Boolean state;
    private String username;
    private Long gameId;
    private String gameName;
    private LocalDateTime startDate;
    private String place;
    private String category;
    private String seatNumber;

    public TicketResponseDto(Ticket ticket){
        User user = ticket.getUser();
        Game game = ticket.getGame();
        Seat seat = ticket.getSeat();

        this.id = ticket.getId();
        this.state = ticket.getState();
        this.username = user.getUsername();
        this.gameId = game.getId();
        this.gameName = game.getName();
        this.startDate = game.getStartDate();
        this.place = String.valueOf(game.getPlace());
        this.category = String.valueOf(game.getCategory());
        this.seatNumber = seat.getSeatsetting().getSeatNumber();
    }
}
