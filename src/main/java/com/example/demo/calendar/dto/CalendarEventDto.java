package com.example.demo.calendar.dto;

import com.example.demo.common.dto.CommonDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CalendarEventDto extends CommonDto {
    private Long id;
    private String title;
    private LocalDate start;
    private LocalDate end;
    private String backgroundColor;
}
