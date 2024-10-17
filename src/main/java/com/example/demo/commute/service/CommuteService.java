package com.example.demo.commute.service;

import com.example.demo.common.dto.CommonDto;
import com.example.demo.common.utils.SecurityUtils;
import com.example.demo.commute.dto.CommuteDto;
import com.example.demo.commute.entity.Commute;
import com.example.demo.commute.mapper.CommonMapper;
import com.example.demo.commute.mapper.commute.CommuteMapper;
import com.example.demo.commute.repository.CommuteRepository;
import com.example.demo.employee.entity.Employee;
import lombok.RequiredArgsConstructor;
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

    public Commute createCommute() {
        Commute commute = Commute.builder()
                .date(LocalDate.now())
                .checkInTime(LocalTime.now())
                .build();
        commute = commuteRepository.save(commute);
        return commute;
    }
}
