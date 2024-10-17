package com.example.demo.common.tools;

import com.example.demo.employee.entity.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring", // 스프링 빈으로 만듦
        unmappedTargetPolicy = ReportingPolicy.IGNORE // 매핑되지않은 속성 무시
        )
public interface CommonMapper {


    @Named("createdByToId")
    default Long createdByToId(Employee createdBy){
        return createdBy.getId();
    }
    @Named("updatedByToId")
    default Long updatedByToId(Employee updatedBy){
        return updatedBy.getId();
    }
}