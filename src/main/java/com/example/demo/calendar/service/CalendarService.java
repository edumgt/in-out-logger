package com.example.demo.calendar.service;


import com.example.demo.calendar.dto.CalendarEventDto;
import com.example.demo.calendar.dto.request.ChangeEventDateRequestDto;
import com.example.demo.calendar.dto.response.HolidayResponseDto;
import com.example.demo.calendar.entity.CalendarEvent;
import com.example.demo.calendar.entity.Vacation;
import com.example.demo.calendar.enums.VacationStatus;
import com.example.demo.calendar.enums.VacationType;
import com.example.demo.calendar.mapper.CalendarMapper;
import com.example.demo.calendar.repository.CalendarEventRepository;
import com.example.demo.common.exception.HttpException;
import com.example.demo.common.httpclient.holiday.exchanger.HolidayExchanger;
import com.example.demo.common.httpclient.holiday.model.HolidayResponse;
import com.example.demo.common.model.SecretConfig;
import com.example.demo.common.utils.SecurityUtils;
import com.example.demo.employee.entity.Employee;
import com.example.demo.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CalendarService {
    private final CalendarMapper calendarMapper;
    private final CalendarEventRepository calendarEventRepository;
    private final VacationService vacationService;
    private final EmployeeRepository employeeRepository;
    private final HolidayExchanger holidayExchanger;
    private final RestTemplate restTemplate;
    private final SecretConfig secretConfig;

    public CalendarEventDto createEvent(CalendarEventDto calendarEventDto) {
        Employee currentUser = SecurityUtils.getCurrentUser();
        CalendarEvent calendarEvent = calendarMapper.toEntity(calendarEventDto);
        Vacation vacation = null;
        if (calendarEventDto.getIsVacation()) { // 휴가라면
            // 휴가라면 휴가 타입과 사용자명 추가
            VacationType vacationType = VacationType.of(calendarEventDto.getVacationType());
            if (vacationType == null) {
                throw new HttpException(400, "잘못된 요청입니다.");
            }
            if (VacationType.isHalf(vacationType)) {
                long dateDiff = ChronoUnit.DAYS.between(calendarEventDto.getStart(), calendarEventDto.getEnd());
                boolean isLongerThanOneDay = dateDiff > 1;
                if (isLongerThanOneDay) {
                    throw new HttpException(400, "반차는 1일 단위로 추가해주시기 바랍니다.");
                }
            }
            vacation = vacationService.createVacation(calendarEvent, vacationType, currentUser); // 휴가 요청 생성
            if (SecurityUtils.isAdmin(currentUser)) { // 관리자라면 바로 승인
                vacation.setVacationStatus(VacationStatus.APPROVED);
                vacation.setApprovedBy(currentUser);
                vacation.setApprovedAt(LocalDateTime.now());
            }
            calendarEvent.setTitle("%s (%s)".formatted(vacationType.getValue(), currentUser.getName()));
        } else { // 휴가가 아니라면 일정에 사용자명 추가
            calendarEvent.setTitle("%s (%s)".formatted(calendarEventDto.getTitle(), currentUser.getName()));
        }
        if (vacation != null) {
            calendarEvent.setVacation(vacation);
        }
        calendarEventRepository.save(calendarEvent);
        calendarEventDto = calendarMapper.toDto(calendarEvent);
        return calendarEventDto;
    }

    public void deleteEvent(Long eventId) {
        CalendarEvent calendarEvent = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new HttpException(HttpStatus.BAD_REQUEST, "이미 삭제된 이벤트입니다."));
        SecurityUtils.checkPermission(calendarEvent.getCreatedBy().getId());
        Vacation vacation = calendarEvent.getVacation();

        // 반려당했을땐 즉시 연차 갯수 돌려 받으므로
        if (vacation != null && vacation.getVacationStatus() != VacationStatus.REJECTED) {
            Employee requester = vacation.getCreatedBy();
            double increaseValue = ChronoUnit.DAYS.between(vacation.getStart(), vacation.getEnd()) + 1;
            if (VacationType.isHalf(vacation.getVacationType())) {
                increaseValue -= 0.5;
            }
            // 특별 휴가일땐 돌려받지 못함
            if (!VacationType.isFree(vacation.getVacationType())) {
                employeeRepository.increaseAnnualLeave(requester.getId(), increaseValue);
            }
        }

        calendarEventRepository.delete(calendarEvent);
    }

    public List<CalendarEventDto> getEventsByYear(int eventYear) {
        LocalDate startDate = LocalDate.of(eventYear, 1, 1);
        LocalDate endDate = LocalDate.of(eventYear, 12, 31);
        return calendarEventRepository.findAllByStartBetween(startDate, endDate).stream().map(calendarMapper::toDto).toList();
    }

    public void changeEventDate(Long eventId, ChangeEventDateRequestDto changeEventDateRequestDto) {
        CalendarEvent calendarEvent = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new HttpException(400, "잘못된 요청입니다."));
        SecurityUtils.checkPermission(calendarEvent.getCreatedBy().getId());
        calendarEvent.setStart(changeEventDateRequestDto.getStart());
        calendarEvent.setEnd(changeEventDateRequestDto.getEnd());

        Vacation vacation = calendarEvent.getVacation();
        if (vacation != null) {
            LocalDate start = changeEventDateRequestDto.getStart();
            LocalDate end = changeEventDateRequestDto.getEnd().minusDays(1);
            Employee requester = vacation.getCreatedBy();
            vacationService.validateVacationDate(start, end, requester);
            vacation.setStart(start);
            vacation.setEnd(end);
        }
        calendarEventRepository.save(calendarEvent);
    }

    public List<HolidayResponseDto> getHolidayInfo(Integer year) {
        Map<String, Object> params = Map.of(
                "solYear", year,
                "numOfRows", 100
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            HolidayResponse response = holidayExchanger.get(params);
            log.info("response {}", response);
            List<HolidayResponse.Body.Items.Item> items = response.getBody().getItems().getItem();
            return items.stream().map(item -> new HolidayResponseDto(
                            item.getDateName(),
                            item.getIsHoliday().equals("Y"),
                            LocalDate.parse(item.getLocdate(), formatter)))
                    .toList();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HttpException(500, "공휴일 정보를 가져오지 못했습니다.");
        }
    }
}
