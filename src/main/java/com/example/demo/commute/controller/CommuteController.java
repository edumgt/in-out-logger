package com.example.demo.commute.controller;

import com.example.demo.commute.service.CommuteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/commutes")
@RestController
@RequiredArgsConstructor
public class CommuteController {
    private final CommuteService commuteService;

    @GetMapping("/{commuteId}")
    public ResponseEntity<?> getOneCommute(@PathVariable Long commuteId) {
        return ResponseEntity.status(200).build();
    }
    @GetMapping
    public ResponseEntity<?> getAllCommutes() {
        return ResponseEntity.status(200).build();
    }

    @PostMapping
    public ResponseEntity<?> checkIn() {
        commuteService.checkIn();
        return ResponseEntity.status(201).build();
    }

    @PatchMapping
    public ResponseEntity<?> checkOut() {
        commuteService.checkOut();
        return ResponseEntity.status(204).build();
    }

}
