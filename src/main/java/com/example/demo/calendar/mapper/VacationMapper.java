package com.example.demo.calendar.mapper;

import com.example.demo.calendar.dto.VacationDto;
import com.example.demo.calendar.entity.Vacation;
import com.example.demo.common.tools.CommonMapper;
import com.example.demo.employee.entity.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true), // @Builder 있어도 생성자 사용
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CommonMapper.class)
public interface VacationMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "createdByToId")
    @Mapping(source = "updatedBy", target = "updatedBy", qualifiedByName = "updatedByToId")
    @Mapping(source = "approvedBy", target = "approvedBy", qualifiedByName = "approvedByToString")
    VacationDto toDto(Vacation vacation);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true) // id는 null로 받음
    @Mapping(target = "approvedBy", ignore = true)
    Vacation toEntity(VacationDto vacationDto);

    @Named("approvedByToString")
    default String approvedByToString(Employee approvedBy){
        if(approvedBy == null){
            return null;
        }
        return approvedBy.getName();
    }

}
