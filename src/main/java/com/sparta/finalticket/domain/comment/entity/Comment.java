package com.sparta.finalticket.domain.comment.entity;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.timeStamped.TimeStamped;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment", indexes = {
        @Index(name = "idx_game_id", columnList = "game_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_review_id",columnList = "review_id"),
        @Index(name = "idx_state", columnList = "state")
})
@SQLDelete(sql = "UPDATE comment SET state = false WHERE id = ?")
@Where(clause = "state = true")
public class Comment extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column
    private Boolean state;

    @Column
    private Long likes; // 좋아요 수

    @Column
    private Long dislikes; // 싫어요 수

    @Column
    private Boolean anonymous; // Add the anonymous field

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParentComment> parentComment;

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

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }
}
