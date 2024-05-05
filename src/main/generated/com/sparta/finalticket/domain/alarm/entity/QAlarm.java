package com.sparta.finalticket.domain.alarm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAlarm is a Querydsl query type for Alarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAlarm extends EntityPathBase<Alarm> {

    private static final long serialVersionUID = -1777072837L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlarm alarm = new QAlarm("alarm");

    public final com.sparta.finalticket.domain.timeStamped.QTimeStamped _super = new com.sparta.finalticket.domain.timeStamped.QTimeStamped(this);

    public final DateTimePath<java.time.LocalDateTime> alarmTime = createDateTime("alarmTime", java.time.LocalDateTime.class);

    public final EnumPath<AlarmType> alarmType = createEnum("alarmType", AlarmType.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> deliveryAttempts = createNumber("deliveryAttempts", Integer.class);

    public final com.sparta.finalticket.domain.game.entity.QGame game;

    public final QAlarmGroup group;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isRead = createBoolean("isRead");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final EnumPath<Priority> priority = createEnum("priority", Priority.class);

    public final DateTimePath<java.time.LocalDateTime> scheduledTime = createDateTime("scheduledTime", java.time.LocalDateTime.class);

    public final BooleanPath state = createBoolean("state");

    public final StringPath teamName = createString("teamName");

    public final com.sparta.finalticket.domain.user.entity.QUser user;

    public QAlarm(String variable) {
        this(Alarm.class, forVariable(variable), INITS);
    }

    public QAlarm(Path<? extends Alarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAlarm(PathMetadata metadata, PathInits inits) {
        this(Alarm.class, metadata, inits);
    }

    public QAlarm(Class<? extends Alarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.game = inits.isInitialized("game") ? new com.sparta.finalticket.domain.game.entity.QGame(forProperty("game"), inits.get("game")) : null;
        this.group = inits.isInitialized("group") ? new QAlarmGroup(forProperty("group")) : null;
        this.user = inits.isInitialized("user") ? new com.sparta.finalticket.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

