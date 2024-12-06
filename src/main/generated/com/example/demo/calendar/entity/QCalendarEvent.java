package com.example.demo.calendar.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCalendarEvent is a Querydsl query type for CalendarEvent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCalendarEvent extends EntityPathBase<CalendarEvent> {

    private static final long serialVersionUID = -974588281L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCalendarEvent calendarEvent = new QCalendarEvent("calendarEvent");

    public final com.example.demo.common.entity.QCommonProperties _super;

    public final StringPath backgroundColor = createString("backgroundColor");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.example.demo.employee.entity.QEmployee createdBy;

    public final DatePath<java.time.LocalDate> end = createDate("end", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> start = createDate("start", java.time.LocalDate.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt;

    // inherited
    public final com.example.demo.employee.entity.QEmployee updatedBy;

    public final QVacation vacation;

    public QCalendarEvent(String variable) {
        this(CalendarEvent.class, forVariable(variable), INITS);
    }

    public QCalendarEvent(Path<? extends CalendarEvent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCalendarEvent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCalendarEvent(PathMetadata metadata, PathInits inits) {
        this(CalendarEvent.class, metadata, inits);
    }

    public QCalendarEvent(Class<? extends CalendarEvent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.example.demo.common.entity.QCommonProperties(type, metadata, inits);
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.updatedAt = _super.updatedAt;
        this.updatedBy = _super.updatedBy;
        this.vacation = inits.isInitialized("vacation") ? new QVacation(forProperty("vacation"), inits.get("vacation")) : null;
    }

}

