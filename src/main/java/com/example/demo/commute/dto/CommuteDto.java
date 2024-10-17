package com.example.demo.commute.dto;

import com.example.demo.common.dto.CommonDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CommuteDto extends CommonDto {
    private Long id;
    private LocalDate date;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
}
