package com.example.demo.calendar.mapper;

import com.example.demo.calendar.dto.CalendarEventDto;
import com.example.demo.calendar.entity.CalendarEvent;
import com.example.demo.common.tools.CommonMapper;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-06T07:51:33+0000",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 17.0.9 (Amazon.com Inc.)"
)
@Component
public class CalendarMapperImpl implements CalendarMapper {

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public CalendarEventDto toDto(CalendarEvent calendarEvent) {
        if ( calendarEvent == null ) {
            return null;
        }

        CalendarEventDto calendarEventDto = new CalendarEventDto();

        calendarEventDto.setCreatedBy( commonMapper.createdByToId( calendarEvent.getCreatedBy() ) );
        calendarEventDto.setUpdatedBy( commonMapper.updatedByToId( calendarEvent.getUpdatedBy() ) );
        calendarEventDto.setVacationStatus( vacationToVacationStatus( calendarEvent.getVacation() ) );
        calendarEventDto.setCreatedAt( calendarEvent.getCreatedAt() );
        calendarEventDto.setUpdatedAt( calendarEvent.getUpdatedAt() );
        calendarEventDto.setId( calendarEvent.getId() );
        calendarEventDto.setTitle( calendarEvent.getTitle() );
        calendarEventDto.setStart( calendarEvent.getStart() );
        calendarEventDto.setEnd( calendarEvent.getEnd() );
        calendarEventDto.setBackgroundColor( calendarEvent.getBackgroundColor() );

        return calendarEventDto;
    }

    @Override
    public CalendarEvent toEntity(CalendarEventDto calendarEventDto) {
        if ( calendarEventDto == null ) {
            return null;
        }

        CalendarEvent calendarEvent = new CalendarEvent();

        calendarEvent.setId( calendarEventDto.getId() );
        calendarEvent.setTitle( calendarEventDto.getTitle() );
        calendarEvent.setStart( calendarEventDto.getStart() );
        calendarEvent.setEnd( calendarEventDto.getEnd() );
        calendarEvent.setBackgroundColor( calendarEventDto.getBackgroundColor() );

        return calendarEvent;
    }
}
