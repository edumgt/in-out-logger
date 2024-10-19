package com.example.demo.employee.dto;

import com.example.demo.common.dto.CommonDto;
import com.example.demo.common.enums.EmploymentStatus;
import com.example.demo.common.enums.JobLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeDto extends CommonDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private LocalDate hireDate;
    private JobLevel jobLevel;
    private String address;
    private EmploymentStatus employmentStatus;
}
