package com.sparta.finalticket.domain.ticket.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTicket is a Querydsl query type for Ticket
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTicket extends EntityPathBase<Ticket> {

    private static final long serialVersionUID = 1180422797L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTicket ticket = new QTicket("ticket");

    public final com.sparta.finalticket.domain.timeStamped.QTimeStamped _super = new com.sparta.finalticket.domain.timeStamped.QTimeStamped(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final com.sparta.finalticket.domain.game.entity.QGame game;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.sparta.finalticket.domain.payment.entity.QPayments payments;

    public final com.sparta.finalticket.domain.seat.entity.QSeat seat;

    public final BooleanPath state = createBoolean("state");

    public final StringPath ticketUid = createString("ticketUid");

    public final com.sparta.finalticket.domain.user.entity.QUser user;

    public QTicket(String variable) {
        this(Ticket.class, forVariable(variable), INITS);
    }

    public QTicket(Path<? extends Ticket> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTicket(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTicket(PathMetadata metadata, PathInits inits) {
        this(Ticket.class, metadata, inits);
    }

    public QTicket(Class<? extends Ticket> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.game = inits.isInitialized("game") ? new com.sparta.finalticket.domain.game.entity.QGame(forProperty("game"), inits.get("game")) : null;
        this.payments = inits.isInitialized("payments") ? new com.sparta.finalticket.domain.payment.entity.QPayments(forProperty("payments"), inits.get("payments")) : null;
        this.seat = inits.isInitialized("seat") ? new com.sparta.finalticket.domain.seat.entity.QSeat(forProperty("seat"), inits.get("seat")) : null;
        this.user = inits.isInitialized("user") ? new com.sparta.finalticket.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

