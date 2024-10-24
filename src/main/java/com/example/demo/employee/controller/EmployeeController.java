package com.example.demo.employee.controller;

import com.example.demo.calendar.dto.VacationDto;
import com.example.demo.calendar.dto.response.PendingVacationDto;
import com.example.demo.calendar.service.VacationService;
import com.example.demo.employee.dto.EmployeeDto;
import com.example.demo.employee.dto.response.AnnualLeaveResponseDto;
import com.example.demo.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Secured("ROLE_INTERN")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final VacationService vacationService;

    @GetMapping
    public ResponseEntity<?> getEmployees(){
        List<EmployeeDto> data = employeeService.getEmployees();
        return ResponseEntity.ok(data);
    }

    /**
     * 모든 연차 정보 조회
     * @return
     */
    @GetMapping("/vacations")
    public ResponseEntity<?> getAllVacations(){
        List<AnnualLeaveResponseDto> data = employeeService.getAllVacations();
        return ResponseEntity.status(200).body(data);
    }

    @GetMapping("/vacations/pending")
    public ResponseEntity<?> getAllPendingVacations(){
        List<PendingVacationDto> data = vacationService.getPendingVacations();
        return ResponseEntity.status(200).body(data);
    }

    @GetMapping("/{employeeId}/vacations")
    public ResponseEntity<?> getVacationLogs(@PathVariable Long employeeId){
        List<VacationDto> data = vacationService.getVacationLogs(employeeId);
        return ResponseEntity.status(200).body(data);
    }
    @PatchMapping("/vacations/{vacationId}/approval")
    @Secured("ROLE_HEAD")
    public ResponseEntity<?> approvalVacation(@PathVariable Long vacationId){
        vacationService.approvalVacation(vacationId);
        return ResponseEntity.status(200).body("승인되었습니다.");
    }
    @DeleteMapping("/vacations/{vacationId}/rejection")
    @Secured("ROLE_HEAD")
    public ResponseEntity<?> rejectionVacation(@PathVariable Long vacationId, @RequestParam String reason){
        vacationService.rejectionVacation(vacationId, reason);
        return ResponseEntity.status(200).body("반려되었습니다.");
    }
    @PatchMapping("/{employeeId}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long employeeId, @RequestBody EmployeeDto employeeDto){
        employeeService.updateEmployee(employeeDto, employeeId);
        return ResponseEntity.status(200).body("수정되었습니다.");
    }
}
