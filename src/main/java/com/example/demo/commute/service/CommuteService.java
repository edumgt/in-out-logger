package com.example.demo.commute.service;

import com.example.demo.common.exception.HttpException;
import com.example.demo.common.utils.SecurityUtils;
import com.example.demo.commute.dto.CommuteDto;
import com.example.demo.commute.entity.Commute;
import com.example.demo.commute.mapper.CommuteMapper;
import com.example.demo.commute.repository.CommuteRepository;
import com.example.demo.employee.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommuteService {
    private final CommuteRepository commuteRepository;
    private final CommuteMapper commuteMapper;

    public List<CommuteDto> getAllCommutes() {
        List<Commute> commutes = commuteRepository.findAll();
        List<CommuteDto> commuteDtos = commutes.stream().map(commuteMapper::toDto).toList();
        return commuteDtos;
    }

    public Commute checkIn() {
        Employee employee = SecurityUtils.getCurrentUser();
        boolean isCheckedIn = commuteRepository.findByCreatedByAndDate(employee, LocalDate.now()).isPresent();
        if(isCheckedIn){
            throw new HttpException(HttpStatus.BAD_REQUEST, "이미 출근처리 되었습니다.");
        }
        Commute commute = Commute.builder()
                .date(LocalDate.now())
                .checkInTime(LocalTime.now())
                .build();
        commute = commuteRepository.save(commute);
        return commute;
    }
    public Commute checkOut() {
        Employee employee = SecurityUtils.getCurrentUser();
        Commute commute = commuteRepository.findByCreatedByAndDate(employee, LocalDate.now())
                .orElseThrow(() -> new HttpException(HttpStatus.BAD_REQUEST, "오늘 출근하지 않았습니다."));
        if(commute.getCheckOutTime() != null){
            throw new HttpException(HttpStatus.BAD_REQUEST, "이미 퇴근처리 되었습니다.");
        }
        commute.setCheckOutTime(LocalTime.now());
        commute = commuteRepository.save(commute);
        return commute;
    }
}