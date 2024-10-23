package com.example.demo.employee.entity;

import com.example.demo.common.entity.CommonProperties;
import com.example.demo.common.enums.EmploymentStatus;
import com.example.demo.common.enums.JobLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

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
    @Comment("성함")
    private String name;
    @Column(unique = true, columnDefinition = "VARCHAR(50)")
    @Comment("이메일")
    private String email;
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("비밀번호")
    private String password;
    @Column(columnDefinition = "VARCHAR(50)")
    @Comment("핸드폰 번호")
    private String phoneNumber;
    @Column
    @Comment("입사일자")
    private LocalDate hireDate;
    @Column(columnDefinition = "VARCHAR(50)", nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("직급(권한)")
    private JobLevel jobLevel; // 직급
    @Column(columnDefinition = "VARCHAR(200)")
    private String address;
    @Column(columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    @Comment("고용 상태 정규직/계약직")
    private EmploymentStatus employmentStatus;
    @Column(columnDefinition = "DOUBLE default 0")
    @Comment("잔여 연차")
    private Double annualLeave;
//    @Column(columnDefinition = "VARCHAR(50)")
//    private String department; // 부서
}
