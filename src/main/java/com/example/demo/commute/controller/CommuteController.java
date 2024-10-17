package com.example.demo.commute.controller;

import com.example.demo.commute.dto.CommuteDto;
import com.example.demo.commute.entity.Commute;
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
    public ResponseEntity<?> createCommute() {
        commuteService.createCommute();
        return ResponseEntity.status(201).build();
    }
    @PatchMapping("/{commuteId}")
    public ResponseEntity<?> updateCommute(@RequestBody Commute commute, @PathVariable String commuteId) {
        return ResponseEntity.status(200).build();
    }
    @DeleteMapping("/{commuteId}")
    public ResponseEntity<?> create(@PathVariable String commuteId){
        return ResponseEntity.status(200).build();
    }
}
