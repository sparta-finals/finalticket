package com.sparta.finalticket.domain.game.entity;


import com.sparta.finalticket.domain.timeStamped.TimeStamped;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "game", indexes = @Index(name = "idx_game",columnList = "id"))
@SQLDelete(sql = "UPDATE game SET state = false WHERE id = ?")
@Where(clause = "state = true")


public class Game extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated
    private CategoryEnum category;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Enumerated
    private PlaceEnum place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean state;

    public void deleteGame() {
        this.state = false;
    }

}
