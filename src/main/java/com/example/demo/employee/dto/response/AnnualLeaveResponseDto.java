package com.example.demo.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnualLeaveResponseDto {
    private String employeeName;
    private Integer annualLeave;
}
