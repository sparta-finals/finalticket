package com.sparta.finalticket.domain.game.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGame is a Querydsl query type for Game
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGame extends EntityPathBase<Game> {

    private static final long serialVersionUID = 195464601L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGame game = new QGame("game");

    public final com.sparta.finalticket.domain.timeStamped.QTimeStamped _super = new com.sparta.finalticket.domain.timeStamped.QTimeStamped(this);

    public final EnumPath<CategoryEnum> category = createEnum("category", CategoryEnum.class);

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public final EnumPath<PlaceEnum> place = createEnum("place", PlaceEnum.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final BooleanPath state = createBoolean("state");

    public final com.sparta.finalticket.domain.user.entity.QUser user;

    public QGame(String variable) {
        this(Game.class, forVariable(variable), INITS);
    }

    public QGame(Path<? extends Game> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGame(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGame(PathMetadata metadata, PathInits inits) {
        this(Game.class, metadata, inits);
    }

    public QGame(Class<? extends Game> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.sparta.finalticket.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

