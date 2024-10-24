package com.example.demo.employee.service;

import com.example.demo.common.exception.HttpException;
import com.example.demo.common.utils.ReflectionUtils;
import com.example.demo.common.utils.SecurityUtils;
import com.example.demo.employee.dto.EmployeeDto;
import com.example.demo.employee.dto.response.AnnualLeaveResponseDto;
import com.example.demo.employee.entity.Employee;
import com.example.demo.employee.mapper.EmployeeMapper;
import com.example.demo.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional(readOnly = true)
    public List<AnnualLeaveResponseDto> getAllVacations() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employee -> new AnnualLeaveResponseDto(employee.getName(), employee.getAnnualLeave(), employee.getId()))
                .toList();
    }
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    public void updateEmployee(EmployeeDto employeeDto, Long employeeId) {
        if(employeeDto.getJobLevel() != null){ // 권한 변경 시도
            Employee currentUser = SecurityUtils.getCurrentUser();
            if(!SecurityUtils.isAdmin(currentUser)){ // 관리자가 아닐 경우
                throw new HttpException(403, "권한이 없습니다.");
            } else if(Objects.equals(employeeId, employeeDto.getId())){
                // TODO 관리자인데 자기 자신의 권한을 바꾸려고 하는 경우
            }
        } else {
            SecurityUtils.checkPermission(employeeId);
        }
        Employee targetEmployee  = employeeRepository.findById(employeeId).orElseThrow(() -> new HttpException(400, "잘못된 요청입니다."));
        String[] ignoreProperties = ReflectionUtils.getNullFields(employeeDto);
        BeanUtils.copyProperties(employeeDto, targetEmployee, ignoreProperties);
        employeeRepository.save(targetEmployee);
    }


}
