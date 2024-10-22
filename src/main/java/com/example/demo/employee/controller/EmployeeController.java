package com.example.demo.employee.controller;

import com.example.demo.employee.dto.response.AnnualLeaveResponseDto;
import com.example.demo.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/annual-leave")
    @Secured("ROLE_INTERN")
    public ResponseEntity<?> getAllAnnualLeave(){
        List<AnnualLeaveResponseDto> data = employeeService.getAllAnnualLeave();
        return ResponseEntity.status(200).body(data);
    }
}
