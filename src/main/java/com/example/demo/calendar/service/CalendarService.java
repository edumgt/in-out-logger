package com.example.demo.calendar.service;


import com.example.demo.calendar.dto.CalendarEventDto;
import com.example.demo.calendar.dto.request.ChangeEventDateRequestDto;
import com.example.demo.calendar.entity.CalendarEvent;
import com.example.demo.calendar.entity.Vacation;
import com.example.demo.calendar.enums.VacationType;
import com.example.demo.calendar.mapper.CalendarMapper;
import com.example.demo.calendar.repository.CalendarEventRepository;
import com.example.demo.common.exception.HttpException;
import com.example.demo.common.utils.SecurityUtils;
import com.example.demo.employee.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarService {
    private final CalendarMapper calendarMapper;
    private final CalendarEventRepository calendarEventRepository;
    private final VacationService vacationService;

    public CalendarEventDto createEvent(CalendarEventDto calendarEventDto) {
        Employee currentUser = SecurityUtils.getCurrentUser();
        Vacation vacation = null;
        if (calendarEventDto.getIsVacation()) { // 휴가라면
            // 휴가라면 휴가 타입과 사용자명 추가
            VacationType vacationType = VacationType.descriptionOf(calendarEventDto.getVacationType());
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
            vacation = vacationService.vacationRequest(calendarEventDto, vacationType, currentUser); // 휴가 요청 생성
            calendarEventDto.setTitle("%s (%s)" .formatted(vacationType.getDescription(), currentUser.getName()));
        } else { // 휴가가 아니라면 일정에 사용자명 추가
            calendarEventDto.setTitle("%s (%s)" .formatted(calendarEventDto.getTitle(), currentUser.getName()));
        }
        CalendarEvent calendarEvent = calendarMapper.toEntity(calendarEventDto);
        if (vacation != null) {
            calendarEvent.setVacation(vacation);
        }

        calendarEvent = calendarEventRepository.save(calendarEvent);
        calendarEventDto = calendarMapper.toDto(calendarEvent);
        return calendarEventDto;
    }
    public void deleteEvent(Long eventId) {
        Employee employee = SecurityUtils.getCurrentUser();
        CalendarEvent calendarEvent = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new HttpException(HttpStatus.BAD_REQUEST, "이미 삭제된 이벤트입니다."));
        if (!Objects.equals(employee.getId(), calendarEvent.getCreatedBy().getId())) {
            throw new HttpException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        calendarEventRepository.delete(calendarEvent);

    }

    public List<CalendarEventDto> getEventsByYear(int eventYear) {
        LocalDate startDate = LocalDate.of(eventYear, 1, 1);
        LocalDate endDate = LocalDate.of(eventYear, 12, 31);
        return calendarEventRepository.findAllByStartBetween(startDate, endDate).stream().map(calendarMapper::toDto).toList();
    }

    public void changeEventDate(Long eventId, ChangeEventDateRequestDto changeEventDateRequestDto) {
        Employee currentUser = SecurityUtils.getCurrentUser();
        CalendarEvent calendarEvent = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new HttpException(400, "잘못된 요청입니다."));
        if (!Objects.equals(currentUser.getId(), calendarEvent.getCreatedBy().getId())) {
            throw new HttpException(403, "권한이 없습니다.");
        }
        calendarEvent.setStart(changeEventDateRequestDto.getStart());
        calendarEvent.setEnd(changeEventDateRequestDto.getEnd());
        calendarEventRepository.save(calendarEvent);

    }
}
