package com.example.demo.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommonProperties is a Querydsl query type for CommonProperties
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QCommonProperties extends EntityPathBase<CommonProperties> {

    private static final long serialVersionUID = -60469440L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommonProperties commonProperties = new QCommonProperties("commonProperties");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.example.demo.employee.entity.QEmployee createdBy;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final com.example.demo.employee.entity.QEmployee updatedBy;

    public QCommonProperties(String variable) {
        this(CommonProperties.class, forVariable(variable), INITS);
    }

    public QCommonProperties(Path<? extends CommonProperties> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommonProperties(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommonProperties(PathMetadata metadata, PathInits inits) {
        this(CommonProperties.class, metadata, inits);
    }

    public QCommonProperties(Class<? extends CommonProperties> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.createdBy = inits.isInitialized("createdBy") ? new com.example.demo.employee.entity.QEmployee(forProperty("createdBy"), inits.get("createdBy")) : null;
        this.updatedBy = inits.isInitialized("updatedBy") ? new com.example.demo.employee.entity.QEmployee(forProperty("updatedBy"), inits.get("updatedBy")) : null;
    }

}

