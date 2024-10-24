package com.example.demo.commute.service;

import com.example.demo.common.exception.HttpException;
import com.example.demo.common.utils.SecurityUtils;
import com.example.demo.commute.dto.CommuteDto;
import com.example.demo.commute.entity.Commute;
import com.example.demo.commute.entity.QCommute;
import com.example.demo.commute.mapper.CommuteMapper;
import com.example.demo.commute.repository.CommuteRepository;
import com.example.demo.employee.dto.response.LateEmployeeDetailsDto;
import com.example.demo.employee.dto.response.LateEmployeeResponseDto;
import com.example.demo.employee.entity.Employee;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommuteService {
    private final CommuteRepository commuteRepository;
    private final CommuteMapper commuteMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private static final LocalTime LATE_THRESHOLD = LocalTime.of(9, 0); // 9시 이후로는 지각

    public List<CommuteDto> getAllCommutes() {
        List<Commute> commutes = commuteRepository.findAll();
        List<CommuteDto> commuteDtos = commutes.stream().map(commuteMapper::toDto).toList();
        return commuteDtos;
    }

    public String checkIn() {
        LocalTime checkInTime = LocalTime.now();
        Employee employee = SecurityUtils.getCurrentUser();
        Optional<Commute> optionalCommute = commuteRepository.findByCreatedByAndDate(employee, LocalDate.now());
        boolean checkInLogExists = optionalCommute.isPresent();
        if (checkInLogExists) {
            throw new HttpException(400, "이미 출근처리 되었습니다.");
        }
        Commute commute = Commute.builder()
                .date(LocalDate.now())
                .checkInTime(checkInTime)
                .build();
        commuteRepository.save(commute);
        return checkInTime.format(DateTimeFormatter.ofPattern("HH시 mm분 출근처리 되었습니다."));
    }

    public String checkOut() {
        LocalTime checkOutTime = LocalTime.now();
        Employee employee = SecurityUtils.getCurrentUser();
        Commute commute = commuteRepository.findByCreatedByAndDate(employee, LocalDate.now())
                .orElseThrow(() -> new HttpException(HttpStatus.BAD_REQUEST, "오늘 출근하지 않았습니다."));
        boolean checkOutLogExists = commute.getCheckOutTime() != null;
        if(checkOutLogExists){
            throw new HttpException(400, "이미 퇴근처리 되었습니다.");
        }
        commute.setCheckOutTime(checkOutTime);
        commuteRepository.save(commute);
        return checkOutTime.format(DateTimeFormatter.ofPattern("HH시 mm분 퇴근처리 되었습니다. 고생하셨습니다."));
    }

    /**
     * 날짜별로 지각한 사람의 이름과 지각 횟수 조회
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<LateEmployeeResponseDto> getLateEmployees() {
        QCommute qCommute = QCommute.commute;
        List<Tuple> result = jpaQueryFactory
                .select(
                        qCommute.date.year(),
                        qCommute.date.month(),
                        qCommute.count(),
                        qCommute.createdBy.name,
                        qCommute.createdBy.id
                )
                .from(qCommute)
                .where(qCommute.checkInTime.after(LATE_THRESHOLD))
                .groupBy(qCommute.date.year(), qCommute.date.month(), qCommute.createdBy)
                .fetch(); // TODO Projection으로 처음부터 dto로 매핑 -> year, month 변환이 Expressions로 제대로 안됨
        return result.stream().map(tuple -> {
            String lateEmployeeName = tuple.get(3, String.class);
            Long lateCount = tuple.get(2, Long.class);
            String date = tuple.get(0, Integer.class) + "-" + tuple.get(1, Integer.class);
            Long employeeId = tuple.get(4, Long.class);
            return new LateEmployeeResponseDto(lateEmployeeName, lateCount, date, employeeId);
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<LateEmployeeDetailsDto> getLateEmployee(Long employeeId, Integer year, Integer month) {
        validateDateParameters(year, month);
        QCommute qCommute = QCommute.commute;
//        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(qCommute.createdBy.id.eq(employeeId));
//        if (year != null) {
//            builder.and(qCommute.date.year().eq(year));
//            if(month != null){
//                builder.and(qCommute.date.month().eq(month));
//            }
//        } else if(month == null){
//            throw new HttpException(400, "월 값은 년도 값과 같이 필요합니다.");
//        }
//        builder.and(qCommute.checkInTime.after(LATE_THRESHOLD));
        List<LateEmployeeDetailsDto> result = jpaQueryFactory.select(
                        Projections.constructor(LateEmployeeDetailsDto.class,
                                qCommute.createdBy.name,
                                qCommute.date,
                                qCommute.checkInTime,
                                qCommute.checkOutTime)
                )
                .from(qCommute)
                .where(
                        employeeIdEquals(qCommute, employeeId),
                        yearEquals(qCommute, year),
                        monthEquals(qCommute, month),
                        isLateCheckIn(qCommute)
                )
                .fetch();
        return result;
    }

    private void validateDateParameters(Integer year, Integer month) {
        if (year == null && month != null) {
            throw new HttpException(400, "년도 없이 월 값만 지정할 수 없습니다.");
        }
    }

    private BooleanExpression employeeIdEquals(QCommute qCommute, Long employeeId) {
        return employeeId != null ? qCommute.createdBy.id.eq(employeeId) : null;
    }

    private BooleanExpression yearEquals(QCommute qCommute, Integer year) {
        return year != null ? qCommute.date.year().eq(year) : null;
    }

    private BooleanExpression monthEquals(QCommute qCommute, Integer month) {
        return month != null ? qCommute.date.month().eq(month) : null;
    }

    private BooleanExpression isLateCheckIn(QCommute qCommute) {
        return qCommute.checkInTime.after(LATE_THRESHOLD);
    }
}
