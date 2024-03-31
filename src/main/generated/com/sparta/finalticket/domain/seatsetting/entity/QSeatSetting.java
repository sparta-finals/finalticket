package com.sparta.finalticket.domain.seatsetting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSeatSetting is a Querydsl query type for SeatSetting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSeatSetting extends EntityPathBase<SeatSetting> {

    private static final long serialVersionUID = -1142308517L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSeatSetting seatSetting = new QSeatSetting("seatSetting");

    public final com.sparta.finalticket.domain.seat.entity.QSeat _super;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.sparta.finalticket.domain.game.entity.QGame game;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt;

    public final StringPath seat = createString("seat");

    // inherited
    public final com.sparta.finalticket.domain.user.entity.QUser user;

    public QSeatSetting(String variable) {
        this(SeatSetting.class, forVariable(variable), INITS);
    }

    public QSeatSetting(Path<? extends SeatSetting> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSeatSetting(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSeatSetting(PathMetadata metadata, PathInits inits) {
        this(SeatSetting.class, metadata, inits);
    }

    public QSeatSetting(Class<? extends SeatSetting> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.sparta.finalticket.domain.seat.entity.QSeat(type, metadata, inits);
        this.createdAt = _super.createdAt;
        this.game = _super.game;
        this.modifiedAt = _super.modifiedAt;
        this.user = _super.user;
    }

}

