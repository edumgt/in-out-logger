package com.example.demo.employee.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LateEmployeeResponseDto {
    private String lateEmployeeName;
    private Long lateCount;
    private String date;
}
