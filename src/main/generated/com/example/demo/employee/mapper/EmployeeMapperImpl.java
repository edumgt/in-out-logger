package com.example.demo.employee.mapper;

import com.example.demo.common.tools.CommonMapper;
import com.example.demo.employee.dto.EmployeeDto;
import com.example.demo.employee.entity.Employee;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-06T07:51:33+0000",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 17.0.9 (Amazon.com Inc.)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public EmployeeDto toDto(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        EmployeeDto employeeDto = new EmployeeDto();

        employeeDto.setCreatedBy( commonMapper.createdByToId( employee.getCreatedBy() ) );
        employeeDto.setUpdatedBy( commonMapper.updatedByToId( employee.getUpdatedBy() ) );
        employeeDto.setCreatedAt( employee.getCreatedAt() );
        employeeDto.setUpdatedAt( employee.getUpdatedAt() );
        employeeDto.setId( employee.getId() );
        employeeDto.setName( employee.getName() );
        employeeDto.setEmail( employee.getEmail() );
        employeeDto.setPassword( employee.getPassword() );
        employeeDto.setPhoneNumber( employee.getPhoneNumber() );
        employeeDto.setHireDate( employee.getHireDate() );
        employeeDto.setJobLevel( employee.getJobLevel() );
        employeeDto.setAddress( employee.getAddress() );
        employeeDto.setEmploymentStatus( employee.getEmploymentStatus() );
        employeeDto.setAnnualLeave( employee.getAnnualLeave() );
        employeeDto.setDepartment( employee.getDepartment() );

        return employeeDto;
    }

    @Override
    public Employee toEntity(EmployeeDto employeeDto) {
        if ( employeeDto == null ) {
            return null;
        }

        Employee.EmployeeBuilder<?, ?> employee = Employee.builder();

        employee.createdAt( employeeDto.getCreatedAt() );
        employee.updatedAt( employeeDto.getUpdatedAt() );
        employee.id( employeeDto.getId() );
        employee.name( employeeDto.getName() );
        employee.email( employeeDto.getEmail() );
        employee.password( employeeDto.getPassword() );
        employee.phoneNumber( employeeDto.getPhoneNumber() );
        employee.hireDate( employeeDto.getHireDate() );
        employee.jobLevel( employeeDto.getJobLevel() );
        employee.address( employeeDto.getAddress() );
        employee.employmentStatus( employeeDto.getEmploymentStatus() );
        employee.annualLeave( employeeDto.getAnnualLeave() );
        employee.department( employeeDto.getDepartment() );

        return employee.build();
    }
}
