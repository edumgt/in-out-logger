package com.example.demo.employee.repository;

import com.example.demo.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByName(String name);
    Optional<Employee> findByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Employee e SET e.annualLeave = e.annualLeave - :value WHERE e.id = :employeeId")
    void decreaseAnnualLeave(@Param("employeeId") Long employeeId,@Param("value") double value);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Employee e SET e.annualLeave = e.annualLeave + :value WHERE e.id = :employeeId")
    void increaseAnnualLeave(@Param("employeeId") Long employeeId, @Param("value") double value);
}
