package com.example.demo.calendar.service;


import com.example.demo.calendar.dto.CalendarEventDto;
import com.example.demo.calendar.entity.CalendarEvent;
import com.example.demo.calendar.mapper.CalendarMapper;
import com.example.demo.calendar.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
        calendarEventRepository.deleteById(eventId);
    }

    public List<CalendarEventDto> getAllEvents() {
        return calendarEventRepository.findAll().stream().map(calendarMapper::toDto).toList();
    }
}
