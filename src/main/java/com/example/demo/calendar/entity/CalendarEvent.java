package com.example.demo.calendar.entity;

import com.example.demo.common.entity.CommonProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
    private String title;
    @Column
    private LocalDate start;
    @Column
    private LocalDate end;
    @Column(columnDefinition = "VARCHAR(30)")
    private String backgroundColor;

    @JoinColumn(name = "vacation_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private Vacation vacation;
}
