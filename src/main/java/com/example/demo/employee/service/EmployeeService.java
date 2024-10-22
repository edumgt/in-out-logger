package com.example.demo.employee.service;

import com.example.demo.employee.dto.response.AnnualLeaveResponseDto;
import com.example.demo.employee.entity.Employee;
import com.example.demo.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<AnnualLeaveResponseDto> getAllAnnualLeave() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employee -> new AnnualLeaveResponseDto(employee.getName(), employee.getAnnualLeave()))
                .toList();
    }
}
