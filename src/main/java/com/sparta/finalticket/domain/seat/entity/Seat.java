package com.sparta.finalticket.domain.seat.entity;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.seatsetting.entity.SeatSetting;
import com.sparta.finalticket.domain.timeStamped.TimeStamped;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer price;

    @Column
    private Boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seatsetting_id")
    private SeatSetting seatsetting;

    public Seat(Game game, SeatSetting seatSetting, User user, boolean b, int price) {
        this.game = game;
        this.seatsetting = seatSetting;
        this.user = user;
        this.state = b;
        this.price = price;
        this.price = price;
    }

    public Seat(Game game, SeatSetting seatSetting, User user, boolean b) {
        super();
    }

    public void update(boolean b) {
        this.state = b;
    }

    public Seat(Game game, SeatSetting seatSetting, User user) {
        this.game = game;
        this.seatsetting = seatSetting;
        this.user = user;
    }
}
