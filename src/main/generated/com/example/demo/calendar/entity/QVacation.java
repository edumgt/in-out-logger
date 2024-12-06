package com.example.demo.calendar.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVacation is a Querydsl query type for Vacation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVacation extends EntityPathBase<Vacation> {

    private static final long serialVersionUID = 2044764018L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVacation vacation = new QVacation("vacation");

    public final com.example.demo.common.entity.QCommonProperties _super;

    public final DateTimePath<java.time.LocalDateTime> approvedAt = createDateTime("approvedAt", java.time.LocalDateTime.class);

    public final com.example.demo.employee.entity.QEmployee approvedBy;

    public final QCalendarEvent calendarEvent;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.example.demo.employee.entity.QEmployee createdBy;

    public final DatePath<java.time.LocalDate> end = createDate("end", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath reason = createString("reason");

    public final StringPath rejectedReason = createString("rejectedReason");

    public final DatePath<java.time.LocalDate> start = createDate("start", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt;

    // inherited
    public final com.example.demo.employee.entity.QEmployee updatedBy;

    public final EnumPath<com.example.demo.calendar.enums.VacationStatus> vacationStatus = createEnum("vacationStatus", com.example.demo.calendar.enums.VacationStatus.class);

    public final EnumPath<com.example.demo.calendar.enums.VacationType> vacationType = createEnum("vacationType", com.example.demo.calendar.enums.VacationType.class);

    public QVacation(String variable) {
        this(Vacation.class, forVariable(variable), INITS);
    }

    public QVacation(Path<? extends Vacation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVacation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVacation(PathMetadata metadata, PathInits inits) {
        this(Vacation.class, metadata, inits);
    }

    public QVacation(Class<? extends Vacation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.example.demo.common.entity.QCommonProperties(type, metadata, inits);
        this.approvedBy = inits.isInitialized("approvedBy") ? new com.example.demo.employee.entity.QEmployee(forProperty("approvedBy"), inits.get("approvedBy")) : null;
        this.calendarEvent = inits.isInitialized("calendarEvent") ? new QCalendarEvent(forProperty("calendarEvent"), inits.get("calendarEvent")) : null;
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.updatedAt = _super.updatedAt;
        this.updatedBy = _super.updatedBy;
    }

}

