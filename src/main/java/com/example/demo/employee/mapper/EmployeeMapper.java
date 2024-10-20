package com.example.demo.employee.mapper;

import com.example.demo.employee.dto.EmployeeDto;
import com.example.demo.employee.entity.Employee;
import org.mapstruct.Mapper;



import com.example.demo.commute.dto.CommuteDto;
import com.example.demo.commute.entity.Commute;
import com.example.demo.common.tools.CommonMapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CommonMapper.class)
public interface EmployeeMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "createdByToId")
    @Mapping(source = "updatedBy", target = "updatedBy", qualifiedByName = "updatedByToId")
    EmployeeDto toDto(Employee employee);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Employee toEntity(EmployeeDto employeeDto);
}
