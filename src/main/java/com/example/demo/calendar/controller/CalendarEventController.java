package com.example.demo.calendar.controller;

import com.example.demo.calendar.dto.CalendarEventDto;
import com.example.demo.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar/events")
public class CalendarEventController {

    private final CalendarService calendarService;

    @GetMapping("/{eventYear}")
    public ResponseEntity<?> getAllEvents(@PathVariable int eventYear){
         List<CalendarEventDto> result = calendarService.getEventsByYear(eventYear);
         return ResponseEntity.status(200).body(result);
    }
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody CalendarEventDto calendarEventDto){
         CalendarEventDto result = calendarService.createEvent(calendarEventDto);
         return ResponseEntity.status(201).body(result);
    }
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId){
        calendarService.deleteEvent(eventId);
        return ResponseEntity.status(204).build();
    }
}
