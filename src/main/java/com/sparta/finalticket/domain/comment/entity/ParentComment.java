package com.sparta.finalticket.domain.comment.entity;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "parent_comment", indexes = {
        @Index(name = "idx_game_id", columnList = "game_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_review_id",columnList = "review_id"),
        @Index(name = "idx_comment_id",columnList = "comment_id"),
        @Index(name = "idx_state", columnList = "state")
})
public class ParentComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String content;

    @Column
    private Boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false) // Assuming the name of the foreign key column
    private Comment comment;

    public void setContent(String content) {
        this.content = content;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public void setComment(Comment originalComment) {
        this.comment = originalComment;
    }
}
