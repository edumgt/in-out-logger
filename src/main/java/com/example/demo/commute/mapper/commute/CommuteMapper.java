package com.example.demo.commute.mapper.commute;


import com.example.demo.common.dto.CommonDto;
import com.example.demo.common.entity.CommonProperties;
import com.example.demo.commute.dto.CommuteDto;
import com.example.demo.commute.entity.Commute;
import com.example.demo.commute.mapper.CommonMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CommonMapper.class)
public interface CommuteMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "createdByToId")
    @Mapping(source = "updatedBy", target = "updatedBy", qualifiedByName = "updatedByToId")
    CommuteDto toDto(Commute commute);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true) // id는 null로 받음
    Commute toEntity(CommuteDto commuteDto);
}
