package com.example.demo.calendar.service;


import com.example.demo.calendar.dto.CalendarEventDto;
import com.example.demo.calendar.entity.CalendarEvent;
import com.example.demo.calendar.mapper.CalendarMapper;
import com.example.demo.calendar.repository.CalendarEventRepository;
import com.example.demo.common.exception.HttpException;
import com.example.demo.common.utils.SecurityUtils;
import com.example.demo.employee.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarMapper calendarMapper;
    private final CalendarEventRepository calendarEventRepository;

    @Transactional
    public CalendarEventDto createEvent(CalendarEventDto calendarEventDto) {
        CalendarEvent calendarEvent = calendarMapper.toEntity(calendarEventDto);
        calendarEvent = calendarEventRepository.save(calendarEvent);
        calendarEventDto = calendarMapper.toDto(calendarEvent);
        return calendarEventDto;
    }

    public void deleteEvent(Long eventId) {
        Employee employee = SecurityUtils.getCurrentUser();
        CalendarEvent calendarEvent = calendarEventRepository.findById(eventId)
                        .orElseThrow(() -> new HttpException(HttpStatus.BAD_REQUEST, "이미 삭제된 이벤트입니다."));
        if(!Objects.equals(employee.getId(), calendarEvent.getCreatedBy().getId())){
            throw new HttpException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        calendarEventRepository.delete(calendarEvent);
    }

    public List<CalendarEventDto> getEventsByYear(int eventYear) {
        LocalDate startDate = LocalDate.of(eventYear, 1, 1);
        LocalDate endDate = LocalDate.of(eventYear, 12, 31);
        return calendarEventRepository.findAllByStartBetween(startDate, endDate).stream().map(calendarMapper::toDto).toList();
    }
}
