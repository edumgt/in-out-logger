package com.example.demo.commute.mapper;

import com.example.demo.common.dto.CommonDto;
import com.example.demo.common.entity.CommonProperties;
import com.example.demo.commute.dto.CommuteDto;
import com.example.demo.commute.entity.Commute;
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
