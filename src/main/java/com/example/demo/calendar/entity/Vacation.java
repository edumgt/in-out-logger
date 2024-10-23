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
import org.hibernate.annotations.Comment;

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
    @Column(columnDefinition = "varchar(20)", nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("PENDING / REJECTED / APPROVED")
    private VacationStatus vacationStatus;
    @Column(columnDefinition = "varchar(20)", nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("종일휴가 / 오전반차 / 오후반차 / 병가 / 무급휴가 / 특별휴가")
    private VacationType vacationType;

    @Column(nullable = false)
    @Comment("연차 사유")
    private String reason;
    @Column(nullable = false)
    @Comment("연차 시작일자")
    private LocalDate start;
    @Column(nullable = false)
    @Comment("연차 종료일자")
    private LocalDate end;

    @JoinColumn(name = "approved_at")
    @Comment("승인 날짜")
    private LocalDateTime approvedAt;

    @JoinColumn(name = "approved_by")
    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("승인자")
    private Employee approvedBy;

}
