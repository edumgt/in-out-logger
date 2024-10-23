package com.example.demo.employee.controller;

import com.example.demo.calendar.dto.VacationDto;
import com.example.demo.calendar.service.VacationService;
import com.example.demo.employee.dto.EmployeeDto;
import com.example.demo.employee.dto.response.AnnualLeaveResponseDto;
import com.example.demo.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Secured("ROLE_INTERN")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final VacationService vacationService;

    @GetMapping
    public ResponseEntity<?> getEmployees(){
        List<EmployeeDto> data = employeeService.getEmployees();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/annual-leave")
    public ResponseEntity<?> getAllAnnualLeave(){
        List<AnnualLeaveResponseDto> data = employeeService.getAllAnnualLeave();
        return ResponseEntity.status(200).body(data);
    }
    @GetMapping("/{employeeId}/annual-leave")
    public ResponseEntity<?> getAnnualLeave(@PathVariable Long employeeId){
        List<VacationDto> data = vacationService.getVacationLogs(employeeId);
        return ResponseEntity.status(200).body(data);
    }
    @PatchMapping("/{employeeId}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long employeeId, @RequestBody EmployeeDto employeeDto){
        employeeService.updateEmployee(employeeDto, employeeId);
        return ResponseEntity.status(200).body("수정되었습니다.");
    }
}
