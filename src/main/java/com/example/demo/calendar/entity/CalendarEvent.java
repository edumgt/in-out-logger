package com.example.demo.calendar.entity;

import com.example.demo.common.entity.CommonProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Entity
@Table(name = "calendar_event")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class CalendarEvent extends CommonProperties {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    @Comment("캘린더 이벤트명")
    private String title;
    @Column(nullable = false)
    @Comment("이벤트 시작일자")
    private LocalDate start;
    @Column
    @Comment("이벤트 종료일자")
    private LocalDate end;
    @Column(columnDefinition = "VARCHAR(30)")
    @Comment("캘린더 이벤트 색상")
    private String backgroundColor;
    @JoinColumn(name = "vacation_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @Comment("연차 FK")
    private Vacation vacation;
}
