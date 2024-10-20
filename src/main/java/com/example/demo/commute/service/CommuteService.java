package com.example.demo.commute.service;

import com.example.demo.common.exception.HttpException;
import com.example.demo.common.utils.SecurityUtils;
import com.example.demo.commute.dto.CommuteDto;
import com.example.demo.commute.entity.Commute;
import com.example.demo.commute.entity.QCommute;
import com.example.demo.commute.mapper.CommuteMapper;
import com.example.demo.commute.repository.CommuteRepository;
import com.example.demo.employee.dto.response.LateEmployeeResponseDto;
import com.example.demo.employee.entity.Employee;
import com.example.demo.employee.entity.QEmployee;
import com.example.demo.employee.mapper.EmployeeMapper;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommuteService {
    private final CommuteRepository commuteRepository;
    private final CommuteMapper commuteMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final EmployeeMapper employeeMapper;

    public List<CommuteDto> getAllCommutes() {
        List<Commute> commutes = commuteRepository.findAll();
        List<CommuteDto> commuteDtos = commutes.stream().map(commuteMapper::toDto).toList();
        return commuteDtos;
    }

    public void checkIn() {
        Employee employee = SecurityUtils.getCurrentUser();
        boolean isCheckedIn = commuteRepository.findByCreatedByAndDate(employee, LocalDate.now()).isPresent();
        if (isCheckedIn) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "이미 출근 처리 되었습니다.");
        }
        Commute commute = Commute.builder()
                .date(LocalDate.now())
                .checkInTime(LocalTime.now())
                .build();
        commuteRepository.save(commute);
    }

    public String checkOut() {
        Employee employee = SecurityUtils.getCurrentUser();
        Commute commute = commuteRepository.findByCreatedByAndDate(employee, LocalDate.now())
                .orElseThrow(() -> new HttpException(HttpStatus.BAD_REQUEST, "오늘 출근하지 않았습니다."));
        boolean checkOutLogExists = commute.getCheckOutTime() != null;
        commute.setCheckOutTime(LocalTime.now());
        commuteRepository.save(commute);
        return checkOutLogExists ? "퇴근 처리 갱신되었습니다." : "퇴근 처리 되었습니다.";
    }

    /**
     * 날짜별로 지각한 사람의 이름과 지각 횟수 조회
     *
     * @return
     */
    public List<LateEmployeeResponseDto> getLateEmployees() {
        QCommute qCommute = QCommute.commute;
        List<Tuple> result = jpaQueryFactory
                .select(
                        qCommute.date.year(),
                        qCommute.date.month(),
                        qCommute.count(),
                        qCommute.createdBy.name
                )
                .from(qCommute)
                .where(qCommute.checkInTime.after(LocalTime.of(9, 0)))
                .groupBy(qCommute.date.year(), qCommute.date.month(), qCommute.createdBy)
                .fetch(); // TODO Projection으로 처음부터 dto로 매핑
        return result.stream().map(tuple -> {
            String lateEmployeeName = tuple.get(3, String.class);
            Long lateCount = tuple.get(2, Long.class);
            String date = tuple.get(0, Integer.class) + "-" + tuple.get(1, Integer.class);
            return new LateEmployeeResponseDto(lateEmployeeName, lateCount, date);
        }).toList();
    }
}
