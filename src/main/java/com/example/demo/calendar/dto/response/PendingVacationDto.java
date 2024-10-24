package com.example.demo.calendar.dto.response;

import com.example.demo.calendar.enums.VacationType;
import com.example.demo.common.dto.CommonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PendingVacationDto extends CommonDto {
    private Long vacationId;
    private String employeeName;
    private LocalDate start;
    private LocalDate end;
    private String reason;
    private VacationType vacationType;
}
