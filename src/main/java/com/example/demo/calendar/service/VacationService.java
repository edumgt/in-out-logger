package com.example.demo.calendar.service;

import com.example.demo.calendar.dto.VacationDto;
import com.example.demo.calendar.dto.response.PendingVacationDto;
import com.example.demo.calendar.entity.CalendarEvent;
import com.example.demo.calendar.entity.Vacation;
import com.example.demo.calendar.enums.VacationStatus;
import com.example.demo.calendar.enums.VacationType;
import com.example.demo.calendar.mapper.VacationMapper;
import com.example.demo.calendar.repository.VacationRepository;
import com.example.demo.common.exception.HttpException;
import com.example.demo.common.utils.SecurityUtils;
import com.example.demo.employee.entity.Employee;
import com.example.demo.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VacationService {
    private final VacationRepository vacationRepository;
    private final EmployeeRepository employeeRepository;
    private final VacationMapper vacationMapper;

    public void validateVacationDate(LocalDate start, LocalDate end, Employee requester) {
        List<Vacation> vacations = vacationRepository.findOverlappingVacations(requester, start, end);

        if (!vacations.isEmpty()) { // 올라간 휴가가 있을 경우
            Vacation vacation = vacations.get(0);
            switch (vacation.getVacationStatus()) {
                case PENDING ->
                        throw new HttpException(400, "%d일에 결재가 올라가있습니다.".formatted(vacation.getStart().getDayOfMonth()));
                case APPROVED -> throw new HttpException(400, "휴가가 올라간 일자가 포함되어 있습니다.");
//                case REJECTED -> { // 다시 펜딩 상태로 변경
//                    vacation.setVacationStatus(VacationStatus.PENDING);
//                    return vacationRepository.save(vacation);
//                }
            }
        }
    }

    public Vacation createVacation(CalendarEvent calendarEvent, VacationType vacationType, Employee requester) {
        LocalDate start = calendarEvent.getStart();
        LocalDate end = calendarEvent.getEnd().minusDays(1);
        validateVacationDate(start, end, requester);

        Vacation vacation = Vacation.builder()
                .vacationType(vacationType)
                .reason(calendarEvent.getTitle())
                .start(start)
                .end(end)
                .calendarEvent(calendarEvent)
                .vacationStatus(VacationStatus.PENDING)
                .build();

        // 특별휴가일땐 감소 없음
        if (!VacationType.isFree(vacationType)) {
            double decreaseValue = ChronoUnit.DAYS.between(start, end) + 1;
            if (VacationType.isHalf(vacationType)) {
                decreaseValue -= 0.5;
            }
            employeeRepository.decreaseAnnualLeave(requester.getId(), decreaseValue);
        }
        return vacation;
    }


    public List<VacationDto> getVacationLogs(Long employeeId) {
        Employee ruquester = Employee.builder()
                .id(employeeId)
                .build();
        List<Vacation> vacations = vacationRepository.findAllByCreatedBy(ruquester);
        return vacations.stream()
                .map(vacationMapper::toDto)
                .toList();
    }

    public List<PendingVacationDto> getPendingVacations() {
        List<Vacation> vacations = vacationRepository.findAllByVacationStatus(VacationStatus.PENDING);
        return vacations.stream()
                .map(vacationMapper::toPendingVacationDto)
                .toList();
    }

    public void approvalVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new HttpException(400, "잘못된 요청입니다."));
        Employee currentUser = SecurityUtils.getCurrentUser();
        vacation.setApprovedBy(currentUser);
        vacation.setApprovedAt(LocalDateTime.now());
        vacation.setVacationStatus(VacationStatus.APPROVED);
        vacationRepository.save(vacation);
    }

    public void rejectionVacation(Long vacationId, String rejectedReason) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new HttpException(400, "잘못된 요청입니다."));
        vacation.setVacationStatus(VacationStatus.REJECTED);

        Employee employee = vacation.getCreatedBy();
        VacationType vacationType = vacation.getVacationType();
        // 돌려받는 연차 갯수
        double returnValue = employee.getAnnualLeave() + 1;
        if(VacationType.isHalf(vacationType)){
            returnValue -= 0.5;
        } else if(VacationType.isFree(vacationType)){
            returnValue = 0;
        }
        employee.setAnnualLeave(returnValue);
        vacation.setRejectedReason(rejectedReason);
        vacationRepository.save(vacation);
    }
}
