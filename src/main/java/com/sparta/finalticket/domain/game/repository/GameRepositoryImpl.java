package com.sparta.finalticket.domain.game.repository;


import static com.sparta.finalticket.domain.game.entity.QGame.game;
import static com.sparta.finalticket.domain.review.entity.QReview.review1;
import static com.sparta.finalticket.domain.seat.entity.QSeat.seat;
import static com.sparta.finalticket.domain.ticket.entity.QTicket.ticket;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.finalticket.domain.game.dto.response.GameResponseDto;
import com.sparta.finalticket.domain.game.entity.CategoryEnum;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class GameRepositoryImpl implements CustomGameRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteGameAndRelateEntities(Long gameId) {
        queryFactory.delete(game)
            .where(game.id.eq(gameId))
            .execute();

        queryFactory.delete(review1)
            .where(review1.game.id.eq(gameId))
            .execute();

        queryFactory.delete(ticket)
            .where(ticket.game.id.eq(gameId))
            .execute();

        queryFactory.delete(seat)
            .where(seat.game.id.eq(gameId))
            .execute();

    }

    @Override
    public Optional<Game> findByIdAndStateTrue(Long gameId) {
        return Optional.ofNullable(queryFactory.selectFrom(game)
            .where(game.id.eq(gameId)
                .and(game.state.isTrue()))
            .fetchFirst());
    }


    @Override
    public List<GameResponseDto> getUserGameList(User user) {
        return queryFactory.selectFrom(game)
            .where(game.user.id.eq(user.getId())).stream().map(GameResponseDto::new).toList();
    }

    @Override
    public List<GameResponseDto> getGameOfCategory(CategoryEnum categoryEnum){
        return queryFactory.selectFrom(game).where(game.category.eq(categoryEnum)).stream().map(GameResponseDto::new).toList();
    }

    @Override
    public List<GameResponseDto> getGameOfKeyword(String keyword){
        return queryFactory.selectFrom(game).where(game.name.contains(keyword)).stream().map(GameResponseDto::new).toList();
    }
}
