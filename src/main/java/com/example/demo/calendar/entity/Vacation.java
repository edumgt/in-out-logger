package com.example.demo.calendar.entity;

import com.example.demo.calendar.enums.VacationStatus;
import com.example.demo.calendar.enums.VacationType;
import com.example.demo.common.entity.CommonProperties;
import com.example.demo.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vacation")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class Vacation extends CommonProperties {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vacation_status", columnDefinition = "varchar(20)", nullable = false)
    @Enumerated(EnumType.STRING)
    private VacationStatus vacationStatus;
    @Column(name = "vacation_type", columnDefinition = "varchar(20)", nullable = false)
    @Enumerated(EnumType.STRING)
    private VacationType vacationType;

    @Column
    private String reason;
    @Column
    private LocalDate start;
    @Column
    private LocalDate end;

    @JoinColumn(name = "approved_at")
    private LocalDateTime approvedAt;

    @JoinColumn(name = "approved_by")
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee approvedBy;

}
