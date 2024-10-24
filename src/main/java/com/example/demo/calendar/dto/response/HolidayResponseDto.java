package com.example.demo.calendar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class HolidayResponseDto {
    private String dateName;
    private Boolean isHoliday;
    private LocalDate date;
}
