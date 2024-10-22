package com.example.demo.calendar.mapper;

import com.example.demo.calendar.dto.CalendarEventDto;
import com.example.demo.calendar.entity.CalendarEvent;
import com.example.demo.common.tools.CommonMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true), // @Builder 있어도 생성자 사용
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CommonMapper.class)
public interface CalendarMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "createdByToId")
    @Mapping(source = "updatedBy", target = "updatedBy", qualifiedByName = "updatedByToId")
    CalendarEventDto toDto(CalendarEvent calendarEvent);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true) // id는 null로 받음
    CalendarEvent toEntity(CalendarEventDto calendarEventDto);
}
