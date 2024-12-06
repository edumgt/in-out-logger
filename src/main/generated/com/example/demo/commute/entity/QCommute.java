package com.example.demo.commute.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommute is a Querydsl query type for Commute
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommute extends EntityPathBase<Commute> {

    private static final long serialVersionUID = 2133986621L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommute commute = new QCommute("commute");

    public final com.example.demo.common.entity.QCommonProperties _super;

    public final TimePath<java.time.LocalTime> checkInTime = createTime("checkInTime", java.time.LocalTime.class);

    public final TimePath<java.time.LocalTime> checkOutTime = createTime("checkOutTime", java.time.LocalTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.example.demo.employee.entity.QEmployee createdBy;

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt;

    // inherited
    public final com.example.demo.employee.entity.QEmployee updatedBy;

    public QCommute(String variable) {
        this(Commute.class, forVariable(variable), INITS);
    }

    public QCommute(Path<? extends Commute> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommute(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommute(PathMetadata metadata, PathInits inits) {
        this(Commute.class, metadata, inits);
    }

    public QCommute(Class<? extends Commute> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.example.demo.common.entity.QCommonProperties(type, metadata, inits);
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.updatedAt = _super.updatedAt;
        this.updatedBy = _super.updatedBy;
    }

}

