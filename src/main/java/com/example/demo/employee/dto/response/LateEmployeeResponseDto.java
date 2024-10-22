package com.example.demo.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LateEmployeeResponseDto {
    private String lateEmployeeName;
    private Long lateCount;
    private String date;
    private Long employeeId;
}
