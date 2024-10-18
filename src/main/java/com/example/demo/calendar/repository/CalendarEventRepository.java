package com.example.demo.calendar.repository;

import com.example.demo.calendar.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findAllByStartBetween(LocalDate startDate, LocalDate endDate);
}
