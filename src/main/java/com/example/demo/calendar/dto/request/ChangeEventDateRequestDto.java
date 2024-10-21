package com.example.demo.calendar.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ChangeEventDateRequestDto {
    private LocalDate start;
    private LocalDate end;
}
