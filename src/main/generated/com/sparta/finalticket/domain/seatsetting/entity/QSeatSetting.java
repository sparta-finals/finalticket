package com.sparta.finalticket.domain.seatsetting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSeatSetting is a Querydsl query type for SeatSetting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSeatSetting extends EntityPathBase<SeatSetting> {

    private static final long serialVersionUID = -1142308517L;

    public static final QSeatSetting seatSetting = new QSeatSetting("seatSetting");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath seatNumber = createString("seatNumber");

    public final EnumPath<SeatTypeEnum> seatType = createEnum("seatType", SeatTypeEnum.class);

    public QSeatSetting(String variable) {
        super(SeatSetting.class, forVariable(variable));
    }

    public QSeatSetting(Path<? extends SeatSetting> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSeatSetting(PathMetadata metadata) {
        super(SeatSetting.class, metadata);
    }

}

