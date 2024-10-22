package com.example.demo.commute.controller;

import com.example.demo.common.annotation.MeasureTime;
import com.example.demo.commute.service.CommuteService;
import com.example.demo.employee.dto.response.LateEmployeeDetailsDto;
import com.example.demo.employee.dto.response.LateEmployeeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/commute")
@RestController
@RequiredArgsConstructor
@Secured("ROLE_INTERN")
public class CommuteController {
    private final CommuteService commuteService;



    @PostMapping
    public ResponseEntity<?> checkIn() {
        String message = commuteService.checkIn();
        return ResponseEntity.status(200).body(message);
    }

    @PatchMapping
    public ResponseEntity<?> checkOut() {
        String message = commuteService.checkOut();
        return ResponseEntity.status(200).body(message);
    }

    @GetMapping("/employees/late")
    @MeasureTime
    public ResponseEntity<?> getLateEmployees() {
        List<LateEmployeeResponseDto> data = commuteService.getLateEmployees();
        return ResponseEntity.status(200).body(data);
    }
    @GetMapping("/employees/{employeeId}/late/years/{year}/months/{month}")
    @MeasureTime
    public ResponseEntity<?> getLateEmployee(
            @PathVariable Long employeeId,
            @PathVariable(required = false) Integer month,
            @PathVariable(required = false) Integer year) {
        List<LateEmployeeDetailsDto> data = commuteService.getLateEmployee(employeeId, year, month);
        return ResponseEntity.status(200).body(data);
    }

}
