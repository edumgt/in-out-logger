package com.example.demo.commute.controller;

import com.example.demo.common.annotation.MeasureTime;
import com.example.demo.commute.service.CommuteService;
import com.example.demo.employee.dto.response.LateEmployeeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/commute")
@RestController
@RequiredArgsConstructor
public class CommuteController {
    private final CommuteService commuteService;


    @GetMapping("/{commuteId}")
    public ResponseEntity<?> getOne(@PathVariable Long commuteId) {
        return ResponseEntity.status(200).build();
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).build();
    }

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

    @GetMapping("/late-people")
    @MeasureTime
    public ResponseEntity<?> getLateEmployees() {
        List<LateEmployeeResponseDto> data = commuteService.getLateEmployees();
        return ResponseEntity.status(200).body(data);
    }

}
