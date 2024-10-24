package com.example.demo.calendar.dto;

import com.example.demo.common.dto.CommonDto;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDto extends CommonDto {
    private Long id;
    private String title;
    private LocalDate start;
    private LocalDate end;
    private String backgroundColor;
    private Boolean isVacation;
    private String vacationType;
    private String vacationStatus;
}
