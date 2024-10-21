package com.example.demo.employee.entity;

import com.example.demo.common.entity.CommonProperties;
import com.example.demo.common.enums.EmploymentStatus;
import com.example.demo.common.enums.JobLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "employee")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class Employee extends CommonProperties {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "VARCHAR(30)", nullable = false)
    private String name;
    @Column(unique = true, columnDefinition = "VARCHAR(50)")
    private String email;
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String password;
    @Column(columnDefinition = "VARCHAR(50)", nullable = false)
    private String phoneNumber;
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;
    @Column(columnDefinition = "VARCHAR(50)", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobLevel jobLevel; // 직급
    @Column(columnDefinition = "VARCHAR(200)")
    private String address;
    @Column(name = "employment_status", columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus; // 고용 상태 정규직 / 계약직
//    @Column(columnDefinition = "VARCHAR(50)")
//    private String department; // 부서
}
